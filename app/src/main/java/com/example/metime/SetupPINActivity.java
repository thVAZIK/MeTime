package com.example.metime;

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
    private void init() {
        BackBtn = findViewById(R.id.BackBtn);
        codeFields = new TextInputEditText[] {
                findViewById(R.id._1btnET),
                findViewById(R.id._2btnET),
                findViewById(R.id._3btnET),
                findViewById(R.id._4btnET),
        };
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
                                code.append(field.getText().toString());
                            }
                            Toast.makeText(getApplicationContext(),
                                    // TODO
                                    "Code entered: " + code.toString() + ". Data saved.",
                                    Toast.LENGTH_LONG).show();
                            // TODO
                        }
                    }
                }
            });
        }
    }
}