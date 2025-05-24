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
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_PIN_CODE = "pin_code";

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

    private void loginUser(String email, String password) {
        ApiClient api = new ApiClient(this);
        LoginRequest loginRequest = new LoginRequest(email, password);
        api.login(loginRequest, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("loginUser:onFailure", e.getLocalizedMessage());
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("code=400")) {
                        Toast.makeText(LoginActivity.this, "Неверный email или пароль", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Ошибка входа: " + errorMessage, Toast.LENGTH_LONG).show();
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
                        if (auth == null || auth.getAccess_token() == null || auth.getUser() == null) {
                            Toast.makeText(LoginActivity.this, "Ошибка входа: неверный ответ сервера", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("loginUser:parseError", e.getLocalizedMessage());
                        Toast.makeText(LoginActivity.this, "Ошибка обработки ответа сервера", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Сохранение данных в SharedPreferences
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(KEY_EMAIL, email);
                    editor.putString(KEY_PASSWORD, password);
                    editor.putString(KEY_USER_ID, auth.getUser().getId());
                    editor.putString(KEY_ACCESS_TOKEN, auth.getAccess_token());
                    editor.apply();

                    // Сохранение в DataBinding
                    DataBinding.saveBearerToken("Bearer " + auth.getAccess_token());
                    DataBinding.saveUuidUser(auth.getUser().getId());

                    // Проверка наличия PIN-кода
                    String savedPin = prefs.getString(KEY_PIN_CODE, null);
                    Intent intent;
                    if (savedPin == null) {
                        intent = new Intent(LoginActivity.this, SetupPINActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, MainPageActivity.class);
                    }

                    Toast.makeText(LoginActivity.this, "Вход выполнен успешно!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
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
            EmailIL.setError("Email обязателен");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            EmailIL.setError("Введите корректный email");
            return false;
        }
        return true;
    }

    // Валидация пароля
    private boolean validatePassword() {
        String password = PasswordET.getText().toString().trim();
        PasswordIL.setError(null);

        if (password.isEmpty()) {
            PasswordIL.setError("Пароль обязателен");
            return false;
        }
        if (password.length() < 8) {
            PasswordIL.setError("Пароль должен содержать минимум 8 символов");
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