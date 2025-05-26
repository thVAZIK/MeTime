package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.metime.Models.AuthResponse;
import com.example.metime.Models.LoginRequest;
import com.example.metime.Models.ProfileUpdate;
import com.example.metime.Tools.ApiClient;
import com.example.metime.Tools.DataBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.IOException;

public class EnterOTPActivity extends AppCompatActivity {
    TextInputEditText[] codeFields;
    ImageView BackBtn;
    TextView sendCodeAgainTV;
    ConstraintLayout mainLayout;
    private String email;
    private String fullname;

    private void init() {
        codeFields = new TextInputEditText[]{
                findViewById(R.id._1btnET),
                findViewById(R.id._2btnET),
                findViewById(R.id._3btnET),
                findViewById(R.id._4btnET),
                findViewById(R.id._5btnET),
                findViewById(R.id._6btnET),
        };
        BackBtn = findViewById(R.id.BackBtn);
        sendCodeAgainTV = findViewById(R.id.sendCodeAgainTV);
        mainLayout = findViewById(R.id.main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enterotp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        // Получение email из Intent
        email = getIntent().getStringExtra("email");
        if (email == null) {
            Toast.makeText(this, "Error: Email not provided", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Получение userName из Intent
        fullname = getIntent().getStringExtra("fullname");
        if (fullname == null) {
            Toast.makeText(this, "Error: fullname not provided", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupTextWatchers();
        BackBtn.setOnClickListener(v -> finish());

        // sendCodeAgainTV.setOnClickListener(v -> resendOtp());

        mainLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            mainLayout.getWindowVisibleDisplayFrame(r);
            int screenHeight = mainLayout.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) sendCodeAgainTV.getLayoutParams();
                params.bottomMargin = keypadHeight + (int) (getResources().getDisplayMetrics().density); // 16dp + высота клавиатуры
                sendCodeAgainTV.setLayoutParams(params);
            } else {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) sendCodeAgainTV.getLayoutParams();
                params.bottomMargin = (int) (16 * getResources().getDisplayMetrics().density); // Исходный 16dp
                sendCodeAgainTV.setLayoutParams(params);
            }
        });
    }

    private void setupTextWatchers() {
        for (int i = 0; i < codeFields.length; i++) {
            final int index = i;
            codeFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        if (index < codeFields.length - 1) {
                            codeFields[index + 1].requestFocus();
                        } else {
                            verifyOtp();
                        }
                    }
                }
            });
        }
    }

    private void verifyOtp() {
        StringBuilder code = new StringBuilder();
        for (TextInputEditText field : codeFields) {
            String input = field.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show();
                return;
            }
            code.append(input);
        }

        String otp = code.toString();
        if (!otp.matches("\\d{6}")) {
            Toast.makeText(this, "OTP must be 6 digits", Toast.LENGTH_SHORT).show();
            clearFields();
            return;
        }

        ApiClient api = new ApiClient(this);
        api.verifyUser(email, otp, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("verifyUser:onFailure", e.getLocalizedMessage());
                    clearFields();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("verifyUser:onError", errorBody);
                    if (errorBody != null && errorBody.contains("Invalid token")) {
                        Toast.makeText(EnterOTPActivity.this, "Invalid OTP. Please try again or resend code.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(EnterOTPActivity.this, "Verification failed: " + errorBody, Toast.LENGTH_LONG).show();
                    }
                    clearFields();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("verifyUser:onResponse", responseBody);
                    Gson gson = new Gson();
                    AuthResponse auth;
                    try {
                        auth = gson.fromJson(responseBody, AuthResponse.class);
                        if (auth == null || auth.getUser() == null || auth.getUser().getId() == null) {
                            Toast.makeText(EnterOTPActivity.this, "Registration failed: Invalid response", Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("verifyUser:parseError", e.getLocalizedMessage());
                        Toast.makeText(EnterOTPActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(EnterOTPActivity.this, "Account verified successfully!", Toast.LENGTH_LONG).show();
                    DataBinding.saveUuidUser(auth.getUser().getId());
                    DataBinding.saveBearerToken("Bearer " + auth.getAccess_token());

                    // Сохранение email и пароля в SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MeTimePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("user_email", email);
                    editor.putString("user_password", getIntent().getStringExtra("password"));
                    editor.putString("user_id", auth.getUser().getId());
                    editor.putString("access_token", auth.getAccess_token());
                    editor.apply();

                    updateProfile(fullname);
                    // Переход в SetupPINActivity вместо MainPageActivity
                    startActivity(new Intent(EnterOTPActivity.this, SetupPINActivity.class));
                    finish();
                });
            }
        });
    }

    private void updateProfile(String userName) {
        ApiClient supabaseClient = new ApiClient(EnterOTPActivity.this);
        ProfileUpdate profileUpdate = new ProfileUpdate(userName);
        supabaseClient.updateProfile(profileUpdate, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("updateProfile:onFailure", e.getLocalizedMessage());
                    Toast.makeText(EnterOTPActivity.this, "Failed to update profile", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("updateProfile:onError", errorBody);
                    Toast.makeText(EnterOTPActivity.this, "Failed to update profile", Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("updateProfile:onResponse", responseBody);
                });
            }
        });
    }

    // NOT USED
    private void resendOtp() {
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        if (email == null || password == null) {
            Toast.makeText(this, "Error: Cannot resend OTP", Toast.LENGTH_LONG).show();
            return;
        }

        ApiClient api = new ApiClient(this);
        LoginRequest loginRequest = new LoginRequest(email, password);
        api.register(loginRequest, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("resendOtp:onFailure", e.getLocalizedMessage());
                    Toast.makeText(EnterOTPActivity.this, "Failed to resend OTP: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onError(String errorBody) {

            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("resendOtp:onResponse", responseBody);
                    Toast.makeText(EnterOTPActivity.this, "OTP sent again", Toast.LENGTH_SHORT).show();
                    clearFields();
                });
            }
        });
    }

    private void clearFields() {
        for (TextInputEditText field : codeFields) {
            field.setText("");
        }
        codeFields[0].requestFocus();
    }
}