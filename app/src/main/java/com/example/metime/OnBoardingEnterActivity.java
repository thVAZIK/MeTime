package com.example.metime;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OnBoardingEnterActivity extends AppCompatActivity {
    Button SkipBtn;
    private void init() {
        SkipBtn = findViewById(R.id.SkipBtn);
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
    }
}