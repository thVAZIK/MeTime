package com.example.metime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {
    ImageView BackBtn;
    Button RegisterBtn, LogInBtn;
    private void init() {
        BackBtn = findViewById(R.id.BackBtn);
        RegisterBtn = findViewById(R.id.RegisterBtn);
        LogInBtn = findViewById(R.id.LogInBtn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        View.OnClickListener exit = (v -> finish());

        BackBtn.setOnClickListener(exit);
        LogInBtn.setOnClickListener(exit);
        RegisterBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), EnterOTPActivity.class)));
    }
}