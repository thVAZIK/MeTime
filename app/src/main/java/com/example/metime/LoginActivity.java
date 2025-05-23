package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.metime.Models.AuthResponse;
import com.example.metime.Models.LoginRequest;
import com.example.metime.Tools.ApiClient;
import com.example.metime.Tools.DataBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    Button SignUpBtn, ContinueBtn;
    TextInputLayout EmailIL, PasswordIL;
    TextInputEditText EmailET, PasswordET;
    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_USER_ID = "user_id";

    private void init() {
        SignUpBtn = findViewById(R.id.SignUpBtn);
        ContinueBtn = findViewById(R.id.ContinueBtn);
        EmailIL = findViewById(R.id.EmailIL);
        PasswordIL = findViewById(R.id.PasswordIL);
        EmailET = findViewById(R.id.EmailET);
        PasswordET = findViewById(R.id.PasswordET);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        // Проверка автоматической авторизации
        if (isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            finish();
            return;
        }

        // Обработка завершения онбординга
        if (getIntent().getBooleanExtra("completeOnboarding", false)) {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_FIRST_LAUNCH, false);
            editor.apply();
        }

        // Обработчики кликов
        SignUpBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        ContinueBtn.setOnClickListener(v -> {
            if (validateAllFields()) {
                String email = EmailET.getText().toString().trim();
                String password = PasswordET.getText().toString().trim();
                loginUser(email, password);
            }
        });
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String accessToken = prefs.getString(KEY_ACCESS_TOKEN, null);
        return accessToken != null;
    }

    private void loginUser(String email, String password) {
        ApiClient api = new ApiClient(this);
        LoginRequest loginRequest = new LoginRequest(email, password);
        api.login(loginRequest, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("loginUser:onFailure", e.getLocalizedMessage());
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("Invalid login credentials")) {
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("loginUser:onResponse", responseBody);
                    Gson gson = new Gson();
                    AuthResponse auth;
                    try {
                        auth = gson.fromJson(responseBody, AuthResponse.class);
                        if (auth.getAccess_token() == null) {
                            Toast.makeText(LoginActivity.this, "Login failed: Invalid response", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("loginUser:parseError", e.getLocalizedMessage());
                        Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Сохранение данных в SharedPreferences
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(KEY_ACCESS_TOKEN, "Bearer " + auth.getAccess_token());
                    editor.putString(KEY_USER_ID, auth.getId());
                    editor.apply();

                    // Сохранение в DataBinding для совместимости с другими частями приложения
                    DataBinding.saveBearerToken("Bearer " + auth.getAccess_token());
                    DataBinding.saveUuidUser(auth.getId());

                    // Переход в MainPageActivity
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                    finish();
                });
            }
        });
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
        return true;
    }

    // Общая валидация всех полей
    private boolean validateAllFields() {
        boolean isEmailValid = validateEmail();
        boolean isPasswordValid = validatePassword();
        return isEmailValid && isPasswordValid;
    }
}