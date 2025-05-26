package com.example.metime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metime.Adapters.BannerAdapter;
import com.example.metime.Adapters.NavSelectAdapter;
import com.example.metime.Adapters.ServicesMainPageAdapter;
import com.example.metime.Fragments.SidePanelDialogFragment;
import com.example.metime.Models.Appointment;
import com.example.metime.Models.Banner;
import com.example.metime.Models.Profile;
import com.example.metime.Models.Service;
import com.example.metime.Tools.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainPageActivity extends AppCompatActivity implements SidePanelDialogFragment.OnNavigationItemSelectedListener {
    LinearLayout SearchButtonLL;
    ImageView MenuBtn;
    TextView FirstNameUserTV;
    RecyclerView BannersRV, SelectionRV, ServicesRV;
    CardView LastAssignmentCV;
    TextView DateLastAssignmentTV, ServiceLastAssignmentTV, MasterFirstNameLastAssignmentTV, TimeLastAssignmentTV;
    private BannerAdapter bannerAdapter;
    private ServicesMainPageAdapter servicesAdapter;
    private List<Banner> bannerList;
    private List<String> navSelectList;
    private List<Service> servicesList;
    private LinearLayoutManager layoutManager;
    private LinearSnapHelper snapHelper;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private static final long AUTO_SCROLL_INTERVAL = 5000;

    private void init() {
        SearchButtonLL = findViewById(R.id.SearchButtonLL);
        MenuBtn = findViewById(R.id.MenuBtn);
        FirstNameUserTV = findViewById(R.id.FirstNameUserTV);
        BannersRV = findViewById(R.id.BannersRV);
        SelectionRV = findViewById(R.id.SelectionRV);
        ServicesRV = findViewById(R.id.ServicesRV);
        LastAssignmentCV = findViewById(R.id.LastAssignmentCV);
        DateLastAssignmentTV = findViewById(R.id.DateLastAssignmentTV);
        ServiceLastAssignmentTV = findViewById(R.id.ServiceLastAssignmentTV);
        MasterFirstNameLastAssignmentTV = findViewById(R.id.MasterFirstNameLastAssignmentTV);
        TimeLastAssignmentTV = findViewById(R.id.TimeLastAssignmentTV);
        bannerList = new ArrayList<>();
        navSelectList = new ArrayList<>();
        servicesList = new ArrayList<>();
        navSelectList.add("Recommended");
        navSelectList.add("Packages");
        navSelectList.add("Professionals");
        autoScrollHandler = new Handler(Looper.getMainLooper());

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        BannersRV.setLayoutManager(layoutManager);
        bannerAdapter = new BannerAdapter(bannerList, this);
        BannersRV.setAdapter(bannerAdapter);

        servicesAdapter = new ServicesMainPageAdapter(servicesList, MainPageActivity.this);
        ServicesRV.setAdapter(servicesAdapter);
        SelectionRV.setAdapter(new NavSelectAdapter(navSelectList, MainPageActivity.this));

        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(BannersRV);

        getCurrentUser();
        getBanners();
        getServices();
        getLatestAppointment();

        setupAutoScroll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        SearchButtonLL.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
        });

        MenuBtn.setOnClickListener(view -> {
            SidePanelDialogFragment sidePanel = SidePanelDialogFragment.newInstance("home");
            sidePanel.setOnNavigationItemSelectedListener(this);
            sidePanel.show(getSupportFragmentManager(), "SidePanelDialogFragment");
        });
    }

    @Override
    public void onItemSelected(String item) {
        switch (item) {
            case "home":
                break;
            case "profile":
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                break;
            case "bookings":
                startActivity(new Intent(getApplicationContext(), BookingsActivity.class));
                finish();
                break;
            case "support":
                startActivity(new Intent(getApplicationContext(), SupportChatActivity.class));
                finish();
                break;
        }
    }

    private void getCurrentUser() {
        ApiClient supabaseClient = new ApiClient(MainPageActivity.this);
        supabaseClient.getProfile(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Log.e("fetch:user:onFailure", e.getLocalizedMessage()));
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> Log.e("fetch:user:onError", errorBody));
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:user:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Profile>>() {}.getType();
                    List<Profile> profiles = gson.fromJson(responseBody, type);
                    if (profiles != null && !profiles.isEmpty()) {
                        Profile profile = profiles.get(0);
                        FirstNameUserTV.setText(profile.getUsername().split(" ")[0]);
                    }
                });
            }
        });
    }

    private void getBanners() {
        ApiClient supabaseClient = new ApiClient(MainPageActivity.this);
        supabaseClient.fetchAllActiveBanners(new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:banners:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Banner>>() {}.getType();
                    List<Banner> banners = gson.fromJson(responseBody, type);
                    if (banners != null && !banners.isEmpty()) {
                        bannerList.clear();
                        bannerList.addAll(banners);
                        bannerAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Log.e("fetch:banners:onFailure", e.getLocalizedMessage()));
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> Log.e("fetch:banners:onError", errorBody));
            }
        });
    }

    private void getServices() {
        ApiClient supabaseClient = new ApiClient(MainPageActivity.this);
        supabaseClient.fetchAllServices(new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:services:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Service>>() {}.getType();
                    List<Service> services = gson.fromJson(responseBody, type);
                    if (services != null && !services.isEmpty()) {
                        servicesList.clear();
                        servicesList.addAll(services);
                        servicesAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Log.e("fetch:services:onFailure", e.getLocalizedMessage()));
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> Log.e("fetch:services:onError", errorBody));
            }
        });
    }

    private void getLatestAppointment() {
        ApiClient supabaseClient = new ApiClient(MainPageActivity.this);
        supabaseClient.fetchAllUserAppointments(new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:appointments:onResponse", responseBody);
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Appointment>>() {}.getType();
                        List<Appointment> appointments = gson.fromJson(responseBody, type);

                        if (appointments == null || appointments.isEmpty()) {
                            LastAssignmentCV.setVisibility(View.GONE);
                            return;
                        }

                        // Фильтруем только будущие записи (appointment_time >= текущего времени)
                        Date currentTime = new Date();
                        List<Appointment> upcomingAppointments = new ArrayList<>();
                        for (Appointment appointment : appointments) {
                            if (appointment.getAppointment_time() != null &&
                                    appointment.getAppointment_time().after(currentTime)) {
                                upcomingAppointments.add(appointment);
                            }
                        }

                        if (upcomingAppointments.isEmpty()) {
                            LastAssignmentCV.setVisibility(View.INVISIBLE);
                            return;
                        }

                        // Находим ближайшую запись
                        Appointment latestAppointment = Collections.min(upcomingAppointments,
                                Comparator.comparing(Appointment::getAppointment_time));

                        // Обновляем UI
                        LastAssignmentCV.setVisibility(View.VISIBLE);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
                        SimpleDateFormat timeFormat = new SimpleDateFormat("EEEE, hh:mma", Locale.getDefault());
                        DateLastAssignmentTV.setText(dateFormat.format(latestAppointment.getAppointment_time()));
                        ServiceLastAssignmentTV.setText(latestAppointment.getServices().getName());
                        MasterFirstNameLastAssignmentTV.setText("with " + latestAppointment.getMasters().getFirst_name());
                        TimeLastAssignmentTV.setText(timeFormat.format(latestAppointment.getAppointment_time()));
                    } catch (Exception e) {
                        Log.e("fetch:appointments:parseError", e.getMessage());
                        LastAssignmentCV.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("fetch:appointments:onFailure", e.getMessage());
                    LastAssignmentCV.setVisibility(View.GONE);
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("fetch:appointments:onError", errorBody);
                    LastAssignmentCV.setVisibility(View.GONE);
                });
            }
        });
    }

    private void setupAutoScroll() {
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                int totalItemCount = bannerAdapter.getItemCount();
                if (totalItemCount <= 1) return;

                int currentPosition = layoutManager.findFirstVisibleItemPosition();
                int nextPosition = (currentPosition + 1) % totalItemCount;

                BannersRV.smoothScrollToPosition(nextPosition);
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_INTERVAL);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLatestAppointment(); // Обновляем ближайшую запись при возвращении на экран
        if (bannerAdapter.getItemCount() > 1) {
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_INTERVAL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoScrollHandler.removeCallbacks(autoScrollRunnable);
    }
}