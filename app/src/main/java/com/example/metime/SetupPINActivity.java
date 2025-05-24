package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class SetupPINActivity extends AppCompatActivity {
    ImageView BackBtn;
    TextInputEditText[] codeFields;
    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_PIN_CODE = "pin_code";

    private void init() {
        BackBtn = findViewById(R.id.BackBtn);
        codeFields = new TextInputEditText[] {
                findViewById(R.id._1btnET),
                findViewById(R.id._2btnET),
                findViewById(R.id._3btnET),
                findViewById(R.id._4btnET),
        };
        codeFields[0].requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setuppin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        setupTextWatchers();
        BackBtn.setOnClickListener(v -> finish());
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
                                    Toast.makeText(SetupPINActivity.this, "Please enter all 4 digits", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                code.append(input);
                            }
                            String pin = code.toString();
                            if (!pin.matches("\\d{4}")) {
                                Toast.makeText(SetupPINActivity.this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show();
                                clearFields();
                                return;
                            }

                            // Сохранение PIN-кода в SharedPreferences
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(KEY_PIN_CODE, pin);
                            editor.apply();

                            Toast.makeText(SetupPINActivity.this, "PIN code saved successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SetupPINActivity.this, MainPageActivity.class));
                            finish();
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
}