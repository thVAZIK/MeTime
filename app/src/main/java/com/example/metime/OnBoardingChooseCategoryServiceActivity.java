package com.example.metime;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Adapters.CategoryServiceAdapter;
import com.example.metime.Fragments.BeforeScheduleFragment;
import com.example.metime.Models.CategoryService;

import java.util.ArrayList;
import java.util.List;

public class OnBoardingChooseCategoryServiceActivity extends AppCompatActivity {
    Button SkipBtn;
    RecyclerView ServicesList;
    List<CategoryService> categoryServices;
    private void init() {
        SkipBtn = findViewById(R.id.SkipBtn);
        ServicesList = findViewById(R.id.ServicesList);
        categoryServices = new ArrayList<>();
        categoryServices.add(new CategoryService("Nail", R.drawable.nail_category));
        categoryServices.add(new CategoryService("Eyebrowns", R.drawable.eyebrowns_category));
        categoryServices.add(new CategoryService("Massage", R.drawable.massage_category));
        categoryServices.add(new CategoryService("Hair", R.drawable.hair_category));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_service_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        SkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeforeScheduleFragment fragment = new BeforeScheduleFragment();
                fragment.show(getSupportFragmentManager(), "BannerFragment");
            }
        });

        ServicesList.setAdapter(new CategoryServiceAdapter(categoryServices, OnBoardingChooseCategoryServiceActivity.this));
    }
}