package com.example.metime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.metime.Fragments.SidePanelDialogFragment;
import com.example.metime.Models.Profile;
import com.example.metime.Tools.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MainPageActivity extends AppCompatActivity implements SidePanelDialogFragment.OnNavigationItemSelectedListener {
    LinearLayout SearchButtonLL;
    ImageView MenuBtn;
    TextView FirstNameUserTV;

    private void init() {
        SearchButtonLL = findViewById(R.id.SearchButtonLL);
        MenuBtn = findViewById(R.id.MenuBtn);
        FirstNameUserTV = findViewById(R.id.FirstNameUserTV);
        getCurrentUser();
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
                runOnUiThread(() -> {
                    Log.e("fetch:user:onFailure", e.getLocalizedMessage());
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:user:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Profile>>(){}.getType();
                    List<Profile> profiles = gson.fromJson(responseBody, type);
                    if (profiles != null && !profiles.isEmpty()) {
                        Profile profile = profiles.get(0);
                        FirstNameUserTV.setText(profile.getUsername().split(" ")[0]);
                    }
                });
            }
        });
    }
}