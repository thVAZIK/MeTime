package com.example.metime;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.metime.Fragments.AddPaymentMethodFragment;
import com.example.metime.Fragments.BeforeScheduleFragment;

public class CheckoutActivity extends AppCompatActivity {
    LinearLayout PaymentMethodLL;
    private void init() {
        PaymentMethodLL = findViewById(R.id.PaymentMethodLL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();

        PaymentMethodLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPaymentMethodFragment fragment = new AddPaymentMethodFragment();
                fragment.show(getSupportFragmentManager(), "BannerFragment");
            }
        });
    }
}