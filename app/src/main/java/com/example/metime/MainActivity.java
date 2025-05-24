package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.gson.Gson;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_PIN_CODE = "pin_code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean(KEY_FIRST_LAUNCH, true);

        // Проверка автоматической авторизации
        if (!isFirstLaunch && isLoggedIn()) {
            String email = prefs.getString(KEY_EMAIL, null);
            String password = prefs.getString(KEY_PASSWORD, null);
            String savedPin = prefs.getString(KEY_PIN_CODE, null);
            if (email != null && password != null) {
                autoLogin(email, password, savedPin != null);
            } else {
                // Если email или password отсутствуют, очистить данные и перейти в LoginActivity
                clearLoginData();
                proceedToLogin();
            }
        } else {
            // Первый запуск или пользователь не вошёл
            proceedToNextActivity(isFirstLaunch);
        }
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, null);
        return userId != null;
    }

    private void autoLogin(String email, String password, boolean hasPin) {
        ApiClient api = new ApiClient(this);
        LoginRequest loginRequest = new LoginRequest(email, password);
        api.login(loginRequest, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("autoLogin:onFailure", e.getLocalizedMessage());
                    String errorMessage = e.getMessage();
                    if (errorMessage != null && errorMessage.contains("Invalid login credentials")) {
                        Toast.makeText(MainActivity.this, "Автоматический вход не удался: неверные данные", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Автоматический вход не удался: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                    // Очистить данные и перейти в LoginActivity
                    clearLoginData();
                    proceedToLogin();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("autoLogin:onResponse", responseBody);
                    Gson gson = new Gson();
                    AuthResponse auth;
                    try {
                        auth = gson.fromJson(responseBody, AuthResponse.class);
                        if (auth == null || auth.getAccess_token() == null || auth.getUser() == null) {
                            Toast.makeText(MainActivity.this, "Автоматический вход не удался: неверный ответ сервера", Toast.LENGTH_LONG).show();
                            clearLoginData();
                            proceedToLogin();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("autoLogin:parseError", e.getLocalizedMessage());
                        Toast.makeText(MainActivity.this, "Ошибка обработки ответа сервера", Toast.LENGTH_LONG).show();
                        clearLoginData();
                        proceedToLogin();
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

                    // Переход в зависимости от наличия PIN-кода
                    Intent intent;
                    if (hasPin) {
                        intent = new Intent(MainActivity.this, EnterPINActivity.class);
                    } else {
                        intent = new Intent(MainActivity.this, SetupPINActivity.class);
                    }
                    startActivity(intent);
                    finish();
                });
            }
        });
    }

    private void clearLoginData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PASSWORD);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_ACCESS_TOKEN);
        editor.remove(KEY_PIN_CODE);
        editor.apply();
        DataBinding.saveBearerToken(null);
        DataBinding.saveUuidUser(null);
    }

    private void proceedToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void proceedToNextActivity(boolean isFirstLaunch) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (isFirstLaunch) {
                intent = new Intent(getApplicationContext(), OnBoardingEnterActivity.class);
            } else {
                intent = new Intent(getApplicationContext(), LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 3000);
    }
}