package com.example.metime;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.metime.Adapters.CouponAdapter;
import com.example.metime.Models.Coupon;
import com.example.metime.Models.Salon;
import com.example.metime.Models.SalonsRatingsSummary;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalonDetailsActivity extends AppCompatActivity {
    private static final String TAG = "SalonDetailsActivity";
    private static final String IMAGE_BASE_URL = "https://xjbqmokswhilgipdgose.supabase.co/storage/v1/object/public/salonsimages//";
    private ImageView salonImageIV, backBtn;
    private TextView salonNameTV, salonAddressTV, sumRateTV, countRatesTV;
    private TextView star5PercentageTV, star4PercentageTV, star3PercentageTV, star2PercentageTV, star1PercentageTV;
    private LinearLayout callBtnLL, messageBtnLL, directionsBtnLL, shareBtnLL;
    private MaterialButton seeAllReviewsBtn;
    private RecyclerView couponsRV;
    private CouponAdapter couponAdapter;
    private ApiClient apiClient;
    private Gson gson = new Gson();
    private int salonId = -1;

    private void init() {
        // Initialize ApiClient
        apiClient = new ApiClient(this);

        // Initialize views
        salonImageIV = findViewById(R.id.SalonImageIV);
        backBtn = findViewById(R.id.BackBtn);
        salonNameTV = findViewById(R.id.SalonNameTV);
        salonAddressTV = findViewById(R.id.SalonAddressTV);
        sumRateTV = findViewById(R.id.SumRateTV);
        countRatesTV = findViewById(R.id.CountRatesTV);
        star5PercentageTV = findViewById(R.id._5StarPercentageRatesTV);
        star4PercentageTV = findViewById(R.id._4StarPercentageRatesTV);
        star3PercentageTV = findViewById(R.id._3StarPercentageRatesTV);
        star2PercentageTV = findViewById(R.id._2StarPercentageRatesTV);
        star1PercentageTV = findViewById(R.id._1StarPercentageRatesTV);
        callBtnLL = findViewById(R.id.callBtnLL);
        messageBtnLL = findViewById(R.id.messageBtnLL);
        directionsBtnLL = findViewById(R.id.directionsBtnLL);
        shareBtnLL = findViewById(R.id.shareBtnLL);
        seeAllReviewsBtn = findViewById(R.id.SeeAllReviewsBtn);
        couponsRV = findViewById(R.id.CouponsRV);

        // Set up RecyclerView for coupons
        couponAdapter = new CouponAdapter();
        couponsRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        couponsRV.setAdapter(couponAdapter);

        // Get salon_id from Intent
        salonId = getIntent().getIntExtra("salon_id", -1);
        if (salonId == -1) {
            Toast.makeText(this, "Invalid salon ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up back button
        backBtn.setOnClickListener(v -> finish());

        // Fetch salon details
        fetchSalonDetails();
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
                        Toast.makeText(SalonDetailsActivity.this, "Failed to load salon details", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    displaySalonDetails(salon);
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchSalonById error: " + errorBody);
                    Toast.makeText(SalonDetailsActivity.this, "Error loading salon: " + errorBody, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchSalonById failure: " + e.getMessage());
                    Toast.makeText(SalonDetailsActivity.this, "Failed to load salon: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void displaySalonDetails(Salon salon) {
        // Set salon name and address
        salonNameTV.setText(salon.getName() != null ? salon.getName() : "Unknown Salon");
        salonAddressTV.setText(salon.getAddress() != null ? salon.getAddress() : "No Address");

        // Load image using Glide
        String imageUrl = salon.getImage_link() != null && !salon.getImage_link().isEmpty()
                ? IMAGE_BASE_URL + salon.getImage_link()
                : null;
        Glide.with(this)
                .load(imageUrl)
                .error(R.drawable.placeholder_banner)
                .placeholder(R.drawable.placeholder_banner)
                .into(salonImageIV);

        // Set ratings
        double averageRating = 0.0;
        int totalRatings = 0;
        int[] ratingCounts = new int[6]; // Index 1-5 for ratings
        if (salon.getSalons_Ratings_Summary() != null && !salon.getSalons_Ratings_Summary().isEmpty()) {
            SalonsRatingsSummary summary = salon.getSalons_Ratings_Summary().get(0);
            averageRating = summary.getAverage_rating();
            ratingCounts[1] = summary.getRating_1_count();
            ratingCounts[2] = summary.getRating_2_count();
            ratingCounts[3] = summary.getRating_3_count();
            ratingCounts[4] = summary.getRating_4_count();
            ratingCounts[5] = summary.getRating_5_count();
            totalRatings = ratingCounts[1] + ratingCounts[2] + ratingCounts[3] + ratingCounts[4] + ratingCounts[5];
        }
        sumRateTV.setText(String.format("%.1f out of 5", averageRating));
        countRatesTV.setText(String.format(Locale.getDefault(), "%d global ratings", totalRatings));

        // Set rating percentages
        star5PercentageTV.setText(totalRatings > 0 ? String.format(Locale.getDefault(), "%d%%", (ratingCounts[5] * 100) / totalRatings) : "0%");
        star4PercentageTV.setText(totalRatings > 0 ? String.format(Locale.getDefault(), "%d%%", (ratingCounts[4] * 100) / totalRatings) : "0%");
        star3PercentageTV.setText(totalRatings > 0 ? String.format(Locale.getDefault(), "%d%%", (ratingCounts[3] * 100) / totalRatings) : "0%");
        star2PercentageTV.setText(totalRatings > 0 ? String.format(Locale.getDefault(), "%d%%", (ratingCounts[2] * 100) / totalRatings) : "0%");
        star1PercentageTV.setText(totalRatings > 0 ? String.format(Locale.getDefault(), "%d%%", (ratingCounts[1] * 100) / totalRatings) : "0%");

        // Set coupons
        List<Coupon> coupons = salon.getCoupons() != null ? salon.getCoupons() : new ArrayList<>();
        couponAdapter.setCoupons(coupons);
        couponsRV.setVisibility(coupons.isEmpty() ? View.GONE : View.VISIBLE);

        // Set up button listeners
        callBtnLL.setOnClickListener(v -> {
            if (salon.getPhone() != null && !salon.getPhone().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + salon.getPhone()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            }
        });

        messageBtnLL.setOnClickListener(v -> {
            Intent intent = new Intent(SalonDetailsActivity.this, SupportChatActivity.class);
            startActivity(intent);
        });

        directionsBtnLL.setOnClickListener(v -> {
            if (salon.getAddress() != null && !salon.getAddress().isEmpty()) {
                String encodedAddress = Uri.encode(salon.getAddress());
                Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + encodedAddress);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                Log.d(TAG, "Opening browser for address: " + salon.getAddress());
                startActivity(webIntent);

            } else {
                Log.w(TAG, "Address not available for directions");
                Toast.makeText(this, "Address not available", Toast.LENGTH_SHORT).show();
            }
        });

        shareBtnLL.setOnClickListener(v -> {
            String shareText = String.format("Check out %s at %s! Book now on MeTime.",
                    salon.getName() != null ? salon.getName() : "this salon",
                    salon.getAddress() != null ? salon.getAddress() : "this location");
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share Salon"));
        });

        seeAllReviewsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SalonDetailsActivity.this, SalonReviewsActivity.class);
            intent.putExtra("salon_id", salon.getSalon_id());
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_salon_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
}