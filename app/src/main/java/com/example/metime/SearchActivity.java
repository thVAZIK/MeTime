package com.example.metime;

import android.content.Intent;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Adapters.SalonAdapter;
import com.example.metime.Models.Salon;
import com.example.metime.Models.SalonsRatingsSummary;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private TextInputEditText searchET;
    private RecyclerView salonsRV;
    private TextView noSalonsTV;
    private ImageView backBtn;
    private SalonAdapter adapter;
    private ApiClient apiClient;
    private Gson gson = new Gson();
    private List<Salon> allSalons = new ArrayList<>();

    private void init() {
        // Initialize ApiClient
        apiClient = new ApiClient(this);

        // Initialize views
        searchET = findViewById(R.id.SearchET);
        salonsRV = findViewById(R.id.searchRV);
        noSalonsTV = findViewById(R.id.noSalonsTV);
        backBtn = findViewById(R.id.BackBtn);

        // Set up RecyclerView
        adapter = new SalonAdapter();
        salonsRV.setLayoutManager(new LinearLayoutManager(this));
        salonsRV.setAdapter(adapter);

        // Set up back button
        backBtn.setOnClickListener(v -> finish());

        // Set up search input listener
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSalons(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        fetchSalons();
    }

    private void fetchSalons() {
        apiClient.fetchAllSalons(new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d(TAG, "fetchAllSalons response: " + responseBody);
                    List<Salon> salons = gson.fromJson(responseBody, new TypeToken<List<Salon>>() {
                    }.getType());
                    if (salons == null) {
                        Log.e(TAG, "fetchAllSalons: Parsed salons list is null");
                        Toast.makeText(SearchActivity.this, "Failed to load salons", Toast.LENGTH_SHORT).show();
                        noSalonsTV.setVisibility(View.VISIBLE);
                        salonsRV.setVisibility(View.GONE);
                        return;
                    }
                    allSalons = salons;
                    filterSalons(searchET.getText().toString().trim());
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchAllSalons error: " + errorBody);
                    Toast.makeText(SearchActivity.this, "Error loading salons: " + errorBody, Toast.LENGTH_SHORT).show();
                    noSalonsTV.setVisibility(View.VISIBLE);
                    salonsRV.setVisibility(View.GONE);
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "fetchAllSalons failure: " + e.getMessage());
                    Toast.makeText(SearchActivity.this, "Failed to load salons: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    noSalonsTV.setVisibility(View.VISIBLE);
                    salonsRV.setVisibility(View.GONE);
                });
            }
        });
    }

    private void filterSalons(String query) {
        List<Salon> filteredSalons = new ArrayList<>();
        if (query.isEmpty()) {
            filteredSalons.addAll(allSalons);
        } else {
            String lowerQuery = query.toLowerCase();
            filteredSalons = allSalons.stream()
                    .filter(salon ->
                            (salon.getName() != null && salon.getName().toLowerCase().contains(lowerQuery)) ||
                                    (salon.getAddress() != null && salon.getAddress().toLowerCase().contains(lowerQuery)))
                    .collect(Collectors.toList());
        }

        adapter.setSalons(filteredSalons);
        noSalonsTV.setVisibility(filteredSalons.isEmpty() ? View.VISIBLE : View.GONE);
        salonsRV.setVisibility(filteredSalons.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
}