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
import com.example.metime.Models.PaymentMethodCreateRequest;
import com.example.metime.Models.PaymentMethodDetails;
import com.example.metime.Models.Profile;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class AddCardActivity extends AppCompatActivity {
    private TextInputEditText cardHolderET, cardNumberET, expDateET, cvvET;
    private MaterialButton addCardBtn;
    private ImageView backBtn;

    private void init() {
        cardHolderET = findViewById(R.id.CardHolderET);
        cardNumberET = findViewById(R.id.CardNumberET);
        expDateET = findViewById(R.id.ExpDateET);
        cvvET = findViewById(R.id.CVVET);
        addCardBtn = findViewById(R.id.AddCardBtn);
        backBtn = findViewById(R.id.BackBtn);

        backBtn.setOnClickListener(v -> finish());

        addCardBtn.setOnClickListener(v -> addCard());
        getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_card);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void addCard() {
        String cardHolder = cardHolderET.getText().toString().trim();
        String cardNumber = cardNumberET.getText().toString().trim();
        String expDate = expDateET.getText().toString().trim();
        String cvv = cvvET.getText().toString().trim();

        // Basic validation
        if (cardHolder.isEmpty()) {
            cardHolderET.setError("Cardholder name is required");
            return;
        }
        if (cardNumber.length() != 16) {
            cardNumberET.setError("Invalid card number");
            return;
        }
        if (!expDate.matches("\\d{2}/\\d{2}")) {
            expDateET.setError("Invalid format (MM/YY)");
            return;
        }
        if (cvv.length() != 3) {
            cvvET.setError("Invalid CVV");
            return;
        }

        SharedPreferences prefs = getSharedPreferences("MeTimePrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        PaymentMethodDetails details = new PaymentMethodDetails(cvv, cardNumber, expDate, cardHolder);
        PaymentMethodCreateRequest request = new PaymentMethodCreateRequest(userId, 4, details, true);

        ApiClient api = new ApiClient(this);
        api.createPaymentMethod(request, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Toast.makeText(AddCardActivity.this, "Failed to add card", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> Toast.makeText(AddCardActivity.this, "Error: " + errorBody, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Toast.makeText(AddCardActivity.this, "Card added successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
        });
    }

    private void getCurrentUser() {
        ApiClient supabaseClient = new ApiClient(this);
        supabaseClient.getProfile(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> Log.e("fetch:user:onFailure", e.getLocalizedMessage()));
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> Log.e("fetch:user:onError", errorBody));
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:user:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Profile>>() {}.getType();
                    List<Profile> profiles = gson.fromJson(responseBody, type);
                    if (profiles != null && !profiles.isEmpty()) {
                        Profile profile = profiles.get(0);
                        cardHolderET.setText(profile.getUsername());
                    }
                });
            }
        });
    }
}