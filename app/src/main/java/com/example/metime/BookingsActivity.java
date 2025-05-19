package com.example.metime;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.metime.Adapters.ViewPagerAdapter;
import com.example.metime.Fragments.WarningCancelFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BookingsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private void init() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Past");
                    break;
                case 1:
                    tab.setText("Upcoming");
                    break;
            }
        }).attach();

        findViewById(R.id.MenuBtn).setOnClickListener(v -> {
            WarningCancelFragment dialog = WarningCancelFragment.newInstance(); // Убрали параметр message
            dialog.setDialogListener(new WarningCancelFragment.DialogListener()  {
                @Override
                public void onCancelClicked() {
                    Toast.makeText(getApplicationContext(), "Бронирование отменено!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNoClicked() {
                    Toast.makeText(getApplicationContext(), "Отмена отклонена!", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show(getSupportFragmentManager(), "CustomDialog");
        });
    }
}