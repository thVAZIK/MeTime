package com.example.metime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookingCompleteActivity extends AppCompatActivity {
    private TextView DateTV, TimeTV, SalonNameTV, LocationTV;
    private MaterialButton KeepBookBtn, MainPageBtn;
    private long appointmentTimeMillis;
    private String serviceName, masterName, location;

    private void init() {
        DateTV = findViewById(R.id.DateTV);
        TimeTV = findViewById(R.id.TimeTV);
        SalonNameTV = findViewById(R.id.SalonNameTV);
        LocationTV = findViewById(R.id.LocationTV);
        KeepBookBtn = findViewById(R.id.KeepBookBtn);
        MainPageBtn = findViewById(R.id.MainPageBtn);

        // Get data from Intent
        Intent intent = getIntent();
        appointmentTimeMillis = intent.getLongExtra("appointment_time", 0);
        serviceName = intent.getStringExtra("service_name");
        masterName = intent.getStringExtra("master_name");
        location = intent.getStringExtra("salon_address");

        // Populate UI
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma", Locale.getDefault());
        DateTV.setText(dateFormat.format(new Date(appointmentTimeMillis)));
        TimeTV.setText(timeFormat.format(new Date(appointmentTimeMillis)));
        SalonNameTV.setText(String.format("At %s Salon", masterName));
        LocationTV.setText(location);

        // Listeners
        KeepBookBtn.setOnClickListener(v -> {
            Intent newBookingIntent = new Intent(this, MainPageActivity.class);
            newBookingIntent.putExtra("start_booking", true);
            startActivity(newBookingIntent);
            finish();
        });

        MainPageBtn.setOnClickListener(v -> {
            Intent mainPageIntent = new Intent(this, MainPageActivity.class);
            startActivity(mainPageIntent);
            finish();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_complete);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }
}