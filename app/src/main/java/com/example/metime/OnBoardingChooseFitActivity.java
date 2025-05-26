package com.example.metime;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Adapters.CategoryServiceAdapter;
import com.example.metime.Adapters.ServiceThatFitAdapter;
import com.example.metime.Fragments.BeforeScheduleFragment;
import com.example.metime.Models.CategoryService;
import com.example.metime.Models.Service;
import com.example.metime.Tools.ApiClient;
import com.example.metime.Tools.ApiKeyLoader;
import com.example.metime.Tools.DataBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OnBoardingChooseFitActivity extends AppCompatActivity {
    ImageView BackBtn;
    RecyclerView ServicesList;
    List<Service> services;

    private void init() {
        BackBtn = findViewById(R.id.BackBtn);
        ServicesList = findViewById(R.id.ServicesList);
        GetAllServices();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_one_needs_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void GetAllServices() {
        ApiClient api = new ApiClient(OnBoardingChooseFitActivity.this);
        api.fetchAllServices(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("fetch:GetAllServices:onFailure", e.getLocalizedMessage());
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("fetch:GetAllServices:onError", errorBody);
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:GetAllServices:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Service>>() {
                    }.getType();
                    services = gson.fromJson(responseBody, type);
                    ServicesList.setAdapter(new ServiceThatFitAdapter(services, OnBoardingChooseFitActivity.this));
                });
            }
        });
    }
}