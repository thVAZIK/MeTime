package com.example.metime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Adapters.ReviewAdapter;
import com.example.metime.Models.Salon;
import com.example.metime.Models.SalonReview;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SalonReviewsActivity extends AppCompatActivity {
    private static final String TAG = "SalonReviewsActivity";
    private ImageView backBtn;
    private TextView salonNameTV;
    private RecyclerView reviewsRV;
    private MaterialButton writeReviewBtn;
    private ReviewAdapter reviewAdapter;
    private ApiClient apiClient;
    private Gson gson = new Gson();
    private int salonId = -1;

    private void init() {
        apiClient = new ApiClient(this);

        backBtn = findViewById(R.id.BackBtn);
        salonNameTV = findViewById(R.id.SalonNameTV);
        reviewsRV = findViewById(R.id.ReviewsRV);
        writeReviewBtn = findViewById(R.id.WriteReviewBtn);

        reviewAdapter = new ReviewAdapter();
        reviewsRV.setLayoutManager(new LinearLayoutManager(this));
        reviewsRV.setAdapter(reviewAdapter);

        salonId = getIntent().getIntExtra("salon_id", -1);
        if (salonId == -1) {
            Log.e(TAG, "Invalid salon ID");
            Toast.makeText(this, "Invalid salon ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        backBtn.setOnClickListener(v -> finish());

        writeReviewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SalonReviewsActivity.this, SalonCreateReviewActivity.class);
            intent.putExtra("salon_id", salonId);
            startActivityForResult(intent, 101);
        });

        fetchSalonDetails();
        fetchReviews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            fetchReviews();
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

    private void fetchReviews() {
        apiClient.fetchSalonReviews(salonId, new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d(TAG, "fetchSalonReviews response: " + responseBody);
                    List<SalonReview> reviews = gson.fromJson(responseBody, new TypeToken<List<SalonReview>>(){}.getType());
                    if (reviews == null || reviews.isEmpty()) {
                        Log.w(TAG, "No reviews found for salon ID: " + salonId);
                        reviewAdapter.setReviews(new ArrayList<>());
                        Toast.makeText(SalonReviewsActivity.this, "No reviews available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    reviewAdapter.setReviews(reviews);
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchSalonReviews error: " + errorBody);
                    reviewAdapter.setReviews(new ArrayList<>());
                    Toast.makeText(SalonReviewsActivity.this, "Error loading reviews", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchSalonReviews failure: " + e.getMessage());
                    reviewAdapter.setReviews(new ArrayList<>());
                    Toast.makeText(SalonReviewsActivity.this, "Failed to load reviews: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_salon_reviews);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
}