package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.metime.Models.AuthResponse;
import com.example.metime.Models.LoginRequest;
import com.example.metime.Models.ProfileUpdate;
import com.example.metime.Tools.ApiClient;
import com.example.metime.Tools.DataBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {
    ImageView BackBtn;
    Button RegisterBtn, LogInBtn;
    TextInputLayout FullnameIL, EmailIL, PasswordIL;
    TextInputEditText FullnameET, EmailET, PasswordET;
    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";

    private void init() {
        BackBtn = findViewById(R.id.BackBtn);
        RegisterBtn = findViewById(R.id.RegisterBtn);
        LogInBtn = findViewById(R.id.LogInBtn);
        FullnameIL = findViewById(R.id.FullnameIL);
        EmailIL = findViewById(R.id.EmailIL);
        PasswordIL = findViewById(R.id.PasswordIL);
        FullnameET = findViewById(R.id.FullnameET);
        EmailET = findViewById(R.id.EmailET);
        PasswordET = findViewById(R.id.PasswordET);
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

        // Обработка завершения онбординга
        if (getIntent().getBooleanExtra("completeOnboarding", false)) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_FIRST_LAUNCH, false);
            editor.apply();
        }

        // Обработчики кликов
        View.OnClickListener exit = (v -> finish());
        BackBtn.setOnClickListener(exit);
        LogInBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

        RegisterBtn.setOnClickListener(v -> {
            if (validateAllFields()) {
                String fullname = FullnameET.getText().toString().trim();
                String email = EmailET.getText().toString().trim();
                String password = PasswordET.getText().toString().trim();
                authUser(fullname, email, password);
            }
        });
    }

    private void authUser(String userName, String email, String password) {
        ApiClient api = new ApiClient(SignUpActivity.this);
        LoginRequest loginRequest = new LoginRequest(email, password);
        api.register(loginRequest, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("authUser:onFailure", e.getLocalizedMessage());
                    Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("authUser:onError", errorBody);
                    Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("authUser:onResponse", responseBody);
                    Gson gson = new Gson();
                    AuthResponse auth;
                    try {
                        auth = gson.fromJson(responseBody, AuthResponse.class);
                        if (auth == null || auth.getId() == null) {
                            Toast.makeText(SignUpActivity.this, "Registration failed: Invalid response", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("authUser:parseError", e.getLocalizedMessage());
                        Toast.makeText(SignUpActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        return;
                    }

                    DataBinding.saveUuidUser(auth.getId());

                    // Переход в EnterOTPActivity, так как Supabase уже отправил OTP
                    Intent intent = new Intent(getApplicationContext(), EnterOTPActivity.class);
                    intent.putExtra("fullname", userName);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("user_id", auth.getId());
                    startActivity(intent);
                });
            }
        });
    }


    // Валидация полного имени
    private boolean validateFullName() {
        String fullName = FullnameET.getText().toString().trim();
        FullnameIL.setError(null);

        if (fullName.isEmpty()) {
            FullnameIL.setError("Full name is required");
            return false;
        }
        if (fullName.length() < 2 || fullName.length() > 50) {
            FullnameIL.setError("Full name must be between 2 and 50 characters");
            return false;
        }
        String[] nameParts = fullName.split("\\s+");
        if (nameParts.length < 2) {
            FullnameIL.setError("Please enter both first and last name");
            return false;
        }
        if (!fullName.matches("^[a-zA-Zа-яА-ЯёЁ\\s-]+$")) {
            FullnameIL.setError("Full name can only contain letters, spaces, or hyphens");
            return false;
        }
        return true;
    }

    // Валидация email
    private boolean validateEmail() {
        String email = EmailET.getText().toString().trim();
        EmailIL.setError(null);

        if (email.isEmpty()) {
            EmailIL.setError("Email is required");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EmailIL.setError("Enter a valid email address");
            return false;
        }
        return true;
    }

    // Валидация пароля
    private boolean validatePassword() {
        String password = PasswordET.getText().toString().trim();
        PasswordIL.setError(null);

        if (password.isEmpty()) {
            PasswordIL.setError("Password is required");
            return false;
        }
        if (password.length() < 8) {
            PasswordIL.setError("Password must be at least 8 characters");
            return false;
        }
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]+$")) {
            PasswordIL.setError("Password must contain at least one letter, one number, and one special character");
            return false;
        }
        return true;
    }

    // Общая валидация всех полей
    private boolean validateAllFields() {
        boolean isFullNameValid = validateFullName();
        boolean isEmailValid = validateEmail();
        boolean isPasswordValid = validatePassword();
        return isFullNameValid && isEmailValid && isPasswordValid;
    }
}