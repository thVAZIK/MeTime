package com.example.metime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.example.metime.Adapters.ViewPagerAdapter;
import com.example.metime.Fragments.SidePanelDialogFragment;
import com.example.metime.Models.Appointment;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingsActivity extends AppCompatActivity implements SidePanelDialogFragment.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageView MenuBtn;
    private SwipeRefreshLayout swipeRefreshLayout;

    private void init() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        MenuBtn = findViewById(R.id.MenuBtn);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        viewPagerAdapter = new ViewPagerAdapter(this, swipeRefreshLayout);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Past");
                    break;
                case 1:
                    tab.setText("Upcoming");
                    break;
            }
        }).attach();

        MenuBtn.setOnClickListener(view -> {
            SidePanelDialogFragment sidePanel = SidePanelDialogFragment.newInstance("bookings");
            sidePanel.setOnNavigationItemSelectedListener(this);
            sidePanel.show(getSupportFragmentManager(), "SidePanelDialogFragment");
        });

        fetchAppointments();
    }

    private void refreshData() {
        fetchAppointments();
    }

    private void completeRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemSelected(String item) {
        switch (item) {
            case "home":
                startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                finish();
                break;
            case "profile":
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
                break;
            case "bookings":
                break;
            case "support":
                startActivity(new Intent(getApplicationContext(), SupportChatActivity.class));
                finish();
                break;
        }
    }

    public void fetchAppointments() {
        swipeRefreshLayout.setRefreshing(true);
        ApiClient api = new ApiClient(this);
        api.fetchAllUserAppointments(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.d("fetchAppointments:OnFailure", e.getLocalizedMessage());
                    Toast.makeText(BookingsActivity.this, "Ошибка загрузки записей", Toast.LENGTH_SHORT).show();
                    completeRefresh();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.d("fetchAppointments:OnError", errorBody);
                    Toast.makeText(BookingsActivity.this, "Ошибка: " + errorBody, Toast.LENGTH_SHORT).show();
                    completeRefresh();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetchAppointments:OnResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Appointment>>() {}.getType();
                    List<Appointment> appointments = gson.fromJson(responseBody, type);

                    List<Appointment> pastAppointments = new ArrayList<>();
                    List<Appointment> upcomingAppointments = new ArrayList<>();
                    Date currentTime = new Date();

                    for (Appointment appointment : appointments) {
                        if (appointment.getAppointment_time().before(currentTime)) {
                            pastAppointments.add(appointment);
                        } else {
                            upcomingAppointments.add(appointment);
                        }
                    }

                    viewPagerAdapter.updateData(0, pastAppointments);
                    viewPagerAdapter.updateData(1, upcomingAppointments);
                    completeRefresh();
                });
            }
        });
    }
}