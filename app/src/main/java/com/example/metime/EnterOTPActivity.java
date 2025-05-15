package com.example.metime;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.android.material.textfield.TextInputEditText;

public class EnterOTPActivity extends AppCompatActivity {
    TextInputEditText[] codeFields;
    ImageView BackBtn;
    TextView sendCodeAgainTV;
    ConstraintLayout mainLayout;
    private void init() {
        codeFields = new TextInputEditText[] {
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
        setupTextWatchers();
        BackBtn.setOnClickListener(v -> finish());

        sendCodeAgainTV.setOnClickListener(v -> {
            Toast.makeText(this, "Code sent again", Toast.LENGTH_SHORT).show();
            clearFields();
        });

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
                                    "Code entered: " + code.toString() + ". Data sent.",
                                    Toast.LENGTH_LONG).show();
                                    // TODO
                        }
                    }
                }
            });
        }
    }

    private void clearFields() {
        // TODO
        for (TextInputEditText field : codeFields) {
            field.setText("");
        }
        codeFields[0].requestFocus();
        // TODO
    }
}