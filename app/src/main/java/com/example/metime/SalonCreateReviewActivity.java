package com.example.metime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.metime.Models.Salon;
import com.example.metime.Models.SalonReviewCreateRequest;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

public class SalonCreateReviewActivity extends AppCompatActivity {
    private static final String TAG = "SalonCreateReviewActivity";
    private ImageView backBtn;
    private TextView salonNameTV;
    private TextInputEditText reviewTextET;
    private ImageView star1Btn, star2Btn, star3Btn, star4Btn, star5Btn;
    private CheckBox anonymousCB;
    private MaterialButton submitBtn, cancelBtn;
    private ApiClient apiClient;
    private Gson gson = new Gson();
    private int salonId = -1;
    private int selectedRating = 0;
    private SharedPreferences prefs;

    private void init() {
        apiClient = new ApiClient(this);
        prefs = getSharedPreferences("MeTimePrefs", Context.MODE_PRIVATE);

        backBtn = findViewById(R.id.BackBtn);
        salonNameTV = findViewById(R.id.SalonNameTV);
        reviewTextET = findViewById(R.id.ReviewTextET);
        star1Btn = findViewById(R.id.Star1Btn);
        star2Btn = findViewById(R.id.Star2Btn);
        star3Btn = findViewById(R.id.Star3Btn);
        star4Btn = findViewById(R.id.Star4Btn);
        star5Btn = findViewById(R.id.Star5Btn);
        anonymousCB = findViewById(R.id.anonymousCB);
        submitBtn = findViewById(R.id.SubmitBtn);
        cancelBtn = findViewById(R.id.CancelBtn);

        salonId = getIntent().getIntExtra("salon_id", -1);
        if (salonId == -1) {
            Log.e(TAG, "Invalid salon ID");
            Toast.makeText(this, "Invalid salon ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        backBtn.setOnClickListener(v -> finish());

        cancelBtn.setOnClickListener(v -> finish());

        setupStarButtons();

        submitBtn.setOnClickListener(v -> submitReview());

        fetchSalonDetails();
    }

    private void setupStarButtons() {
        ImageView[] stars = {star1Btn, star2Btn, star3Btn, star4Btn, star5Btn};
        for (int i = 0; i < stars.length; i++) {
            final int rating = i + 1;
            stars[i].setOnClickListener(v -> {
                selectedRating = rating;
                updateStarUI();
            });
        }
    }

    private void updateStarUI() {
        ImageView[] stars = {star1Btn, star2Btn, star3Btn, star4Btn, star5Btn};
        for (int i = 0; i < stars.length; i++) {
            if (i < selectedRating) {
                stars[i].setImageResource(R.drawable.star);
            } else {
                stars[i].setImageResource(R.drawable.star_outlined);
            }
        }
    }

    private void fetchSalonDetails() {
        apiClient.fetchSalonById(salonId, new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d(TAG, "fetchSalonById response: " + responseBody);
                    List<Salon> salons = gson.fromJson(responseBody, new TypeToken<List<Salon>>() {
                    }.getType());
                    Salon salon = salons.get(0);
                    if (salon == null) {
                        Log.e(TAG, "fetchSalonById: Parsed salon is null");
                        salonNameTV.setText("Unknown Salon");
                        return;
                    }
                    salonNameTV.setText(salon.getName() != null ? salon.getName() : "Unknown Salon");
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchSalonById error: " + errorBody);
                    salonNameTV.setText("Unknown Salon");
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchSalonById failure: " + e.getMessage());
                    salonNameTV.setText("Unknown Salon");
                });
            }
        });
    }

    private void submitReview() {
        String comment = reviewTextET.getText() != null ? reviewTextET.getText().toString().trim() : "";
        if (selectedRating == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }
        if (comment.isEmpty()) {
            Toast.makeText(this, "Please enter a review", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = anonymousCB.isChecked() ? null : prefs.getString("user_id", null);
        if (userId == null && !anonymousCB.isChecked()) {
            Toast.makeText(this, "Please log in to submit a non-anonymous review", Toast.LENGTH_SHORT).show();
            return;
        }

        SalonReviewCreateRequest review = new SalonReviewCreateRequest(salonId, userId, selectedRating, comment);
        apiClient.createSalonReview(review, new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d(TAG, "createSalonReview success");
                    Toast.makeText(SalonCreateReviewActivity.this, "Review submitted", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e(TAG, "createSalonReview error: " + errorBody);
                    Toast.makeText(SalonCreateReviewActivity.this, "Error submitting review", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "createSalonReview failure: " + e.getMessage());
                    Toast.makeText(SalonCreateReviewActivity.this, "Failed to submit review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_salon_create_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
}