package com.example.metime;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Adapters.ChooseMastersAdapter;
import com.example.metime.Adapters.ServiceThatFitAdapter;
import com.example.metime.Models.Master;
import com.example.metime.Models.Service;
import com.example.metime.Tools.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class OnBoardingChooseProfessional extends AppCompatActivity {
    ImageView BackBtn;
    RecyclerView ServicesList;
    List<Master> masters;
    private void init() {
        BackBtn = findViewById(R.id.BackBtn);
        ServicesList = findViewById(R.id.ServicesList);
        GetAllMasters();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_professional);
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

    private void GetAllMasters() {
        ApiClient api = new ApiClient(OnBoardingChooseProfessional.this);
        api.fetchAllMasters(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                Log.e("fetch:GetAllMasters:onFailure", e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:GetAllMasters:onResponse", responseBody);
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Master>>() {
                    }.getType();
                    masters = gson.fromJson(responseBody, type);
                    ServicesList.setAdapter(new ChooseMastersAdapter(masters, OnBoardingChooseProfessional.this, getSupportFragmentManager()));
                });
            }
        });
    }
}