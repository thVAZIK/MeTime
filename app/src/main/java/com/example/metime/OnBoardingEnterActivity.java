package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.metime.Fragments.BeforeScheduleFragment;
import com.example.metime.Tools.ApiKeyLoader;
import com.example.metime.Tools.DataBinding;

public class OnBoardingEnterActivity extends AppCompatActivity {
    Button SkipBtn, StartBtn;
    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";
    private void init() {
        SkipBtn = findViewById(R.id.SkipBtn);
        StartBtn = findViewById(R.id.StartBtn);
        DataBinding.saveBearerToken(ApiKeyLoader.getApiKey(OnBoardingEnterActivity.this));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enter_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        SkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeforeScheduleFragment fragment = new BeforeScheduleFragment();
                fragment.show(getSupportFragmentManager(), "BannerFragment");
            }
        });

        StartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(KEY_FIRST_LAUNCH, false);
                editor.apply();

                startActivity(new Intent(getApplicationContext(), OnBoardingChooseCategoryServiceActivity.class));
                finish();
            }
        });
    }
}