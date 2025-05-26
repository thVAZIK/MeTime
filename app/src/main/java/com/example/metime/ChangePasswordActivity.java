package com.example.metime;

import android.content.SharedPreferences;
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

import com.example.metime.Tools.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

public class ChangePasswordActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextInputLayout currentPasswordTL, newPasswordTL, repeatPasswordTL;
    private TextInputEditText currentPasswordET, newPasswordET, repeatPasswordET;
    private MaterialButton cancelBtn, saveChangesBtn;
    private ApiClient apiClient;

    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_EMAIL = "user_email";

    private void init() {
        backBtn = findViewById(R.id.BackBtn);
        currentPasswordTL = findViewById(R.id.CurrentPasswordTL);
        newPasswordTL = findViewById(R.id.NewPasswordTL);
        repeatPasswordTL = findViewById(R.id.RepeatPasswordTL);
        currentPasswordET = findViewById(R.id.CurrentPasswordET);
        newPasswordET = findViewById(R.id.NewPasswordET);
        repeatPasswordET = findViewById(R.id.RepeatPasswordET);
        cancelBtn = findViewById(R.id.CancelBtn);
        saveChangesBtn = findViewById(R.id.SaveChangesBtn);
        apiClient = new ApiClient(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        setupListeners();
    }

    private void setupListeners() {
        // Назад
        backBtn.setOnClickListener(v -> finish());

        // Отмена
        cancelBtn.setOnClickListener(v -> finish());

        // Сохранение изменений
        saveChangesBtn.setOnClickListener(v -> {
            if (validateFields()) {
                updatePassword();
            }
        });
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Очистка ошибок
        currentPasswordTL.setError(null);
        newPasswordTL.setError(null);
        repeatPasswordTL.setError(null);

        // Получение введённых данных
        String currentPassword = currentPasswordET.getText().toString().trim();
        String newPassword = newPasswordET.getText().toString().trim();
        String repeatPassword = repeatPasswordET.getText().toString().trim();

        // Получение сохранённого пароля и email из SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedPassword = prefs.getString(KEY_PASSWORD, null);
        String email = prefs.getString(KEY_EMAIL, null);

        // Проверка текущего пароля
        if (currentPassword.isEmpty()) {
            currentPasswordTL.setError("Current password is required");
            isValid = false;
        } else if (savedPassword == null || !currentPassword.equals(savedPassword)) {
            currentPasswordTL.setError("Incorrect current password");
            isValid = false;
        }

        // Проверка нового пароля
        if (newPassword.isEmpty()) {
            newPasswordTL.setError("New password is required");
            isValid = false;
        } else if (newPassword.length() < 8) {
            newPasswordTL.setError("Password must be at least 8 characters");
            isValid = false;
        } else if (!newPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&_])[A-Za-z\\d@$!%*?&_]+$")) {
            newPasswordTL.setError("Password can only contain letters, numbers, and special characters");
            isValid = false;
        }

        // Проверка повторного пароля
        if (repeatPassword.isEmpty()) {
            repeatPasswordTL.setError("Please repeat the new password");
            isValid = false;
        } else if (!repeatPassword.equals(newPassword)) {
            repeatPasswordTL.setError("Passwords do not match");
            isValid = false;
        }

        // Проверка email
        if (email == null) {
            Toast.makeText(this, "Error: User email not found", Toast.LENGTH_LONG).show();
            isValid = false;
        }

        return isValid;
    }

    private void updatePassword() {
        saveChangesBtn.setEnabled(false);

        String email = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_EMAIL, null);
        String newPassword = newPasswordET.getText().toString().trim();

        apiClient.updateUser(email, newPassword, new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(KEY_PASSWORD, newPassword);
                    editor.apply();

                    Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                    saveChangesBtn.setEnabled(true);
                    finish();
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("updateUser:onFailure", e.getLocalizedMessage());
                    saveChangesBtn.setEnabled(true);
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("updatePassword:onError", errorBody);
                    if (errorBody != null && errorBody.contains("user_banned")) {
                        Toast.makeText(ChangePasswordActivity.this, "This account is banned. Please, contact admin", Toast.LENGTH_LONG).show();
                    } else if (errorBody != null && errorBody.contains("invalid_credentials")) {
                        Toast.makeText(ChangePasswordActivity.this, "Неверный email или пароль", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Ошибка входа: " + errorBody, Toast.LENGTH_LONG).show();
                    }
                    saveChangesBtn.setEnabled(true);
                });
            }
        });
    }
}