package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.metime.Tools.DataBinding;
import com.google.android.material.textfield.TextInputEditText;

public class EnterPINActivity extends AppCompatActivity {
    TextView LogoutBtn;
    TextInputEditText[] codeFields;
    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_PIN_CODE = "pin_code";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private void init() {
        codeFields = new TextInputEditText[] {
                findViewById(R.id._1btnET),
                findViewById(R.id._2btnET),
                findViewById(R.id._3btnET),
                findViewById(R.id._4btnET),
        };
        LogoutBtn = findViewById(R.id.LogoutBtn);
        codeFields[0].requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enterpin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        setupTextWatchers();

        LogoutBtn.setOnClickListener(v -> {
            // Очистка данных логина и PIN-кода
            clearLoginData();
            Toast.makeText(EnterPINActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EnterPINActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupTextWatchers() {
        for (int i = 0; i < codeFields.length; i++) {
            final int index = i;
            codeFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) {
                        if (index < codeFields.length - 1) {
                            codeFields[index + 1].requestFocus();
                        } else {
                            StringBuilder code = new StringBuilder();
                            for (TextInputEditText field : codeFields) {
                                String input = field.getText().toString().trim();
                                if (input.isEmpty()) {
                                    Toast.makeText(EnterPINActivity.this, "Please enter all 4 digits", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                code.append(input);
                            }
                            String enteredPin = code.toString();
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            String savedPin = prefs.getString(KEY_PIN_CODE, null);

                            if (savedPin != null && savedPin.equals(enteredPin)) {
                                Toast.makeText(EnterPINActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(EnterPINActivity.this, MainPageActivity.class));
                                finish();
                            } else {
                                Toast.makeText(EnterPINActivity.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                                clearFields();
                            }
                        }
                    }
                }
            });
        }
    }

    private void clearFields() {
        for (TextInputEditText field : codeFields) {
            field.setText("");
        }
        codeFields[0].requestFocus();
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
}