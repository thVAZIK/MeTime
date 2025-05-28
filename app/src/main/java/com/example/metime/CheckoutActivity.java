package com.example.metime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.metime.Fragments.AddPaymentMethodFragment;
import com.example.metime.Models.AppointmentInsert;
import com.example.metime.Models.PaymentMethod;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {
    private LinearLayout PaymentMethodLL;
    private TextView AppointmentDateTV, ServiceWithMasterFNTV, LocationTV, TotalTV;
    private MaterialButton BookBtn;
    private ImageView BackBtn, PaymentMethodImageIV;
    private TextView PaymentMethodTV;
    private int serviceId, serviceDuration, calendarId, salonId;
    private String serviceName, masterId, masterName, userId, salonAddress;
    private int servicePrice;
    private long appointmentTimeMillis;
    private static final String PREFS_NAME = "MeTimePrefs";
    private static final String KEY_USER_ID = "user_id";
    private ActivityResultLauncher<Intent> paymentMethodLauncher;

    private void init() {
        PaymentMethodLL = findViewById(R.id.PaymentMethodLL);
        AppointmentDateTV = findViewById(R.id.AppointmentDateTV);
        ServiceWithMasterFNTV = findViewById(R.id.ServiceWithMasterFNTV);
        LocationTV = findViewById(R.id.LocationTV);
        TotalTV = findViewById(R.id.TotalTV);
        BookBtn = findViewById(R.id.BookBtn);
        BackBtn = findViewById(R.id.BackBtn);
        PaymentMethodImageIV = findViewById(R.id.PaymentMethodImageIV);
        PaymentMethodTV = findViewById(R.id.PaymentMethodTV);

        // Get user_id from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userId = prefs.getString(KEY_USER_ID, null);
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Please log in to book an appointment", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get data from Intent
        Intent intent = getIntent();
        serviceId = intent.getIntExtra("service_id", -1);
        serviceName = intent.getStringExtra("service_name");
        servicePrice = intent.getIntExtra("service_price", 0);
        serviceDuration = intent.getIntExtra("service_duration", 0);
        masterId = intent.getStringExtra("master_id");
        masterName = intent.getStringExtra("master_name");
        appointmentTimeMillis = intent.getLongExtra("appointment_time", 0);
        calendarId = intent.getIntExtra("calendar_id", -1);
        salonId = intent.getIntExtra("salon_id", -1);
        salonAddress = intent.getStringExtra("salon_address");

        // Populate UI
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE, dd MMM hh:mma", Locale.getDefault());
        AppointmentDateTV.setText(dateTimeFormat.format(new Date(appointmentTimeMillis)));
        ServiceWithMasterFNTV.setText(String.format("%s with %s", serviceName, masterName));
        LocationTV.setText(salonAddress != null && !salonAddress.isEmpty() ? salonAddress : "Unknown Address");
        TotalTV.setText("$" + servicePrice);
        PaymentMethodTV.setText("None");
        PaymentMethodImageIV.setVisibility(View.GONE);

        // Initialize ActivityResultLauncher
        paymentMethodLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        fetchPaymentMethods(); // Refresh payment methods
                    }
                }
        );

        // Listeners
        BackBtn.setOnClickListener(v -> finish());
        PaymentMethodLL.setOnClickListener(v -> {
            AddPaymentMethodFragment fragment = new AddPaymentMethodFragment();
            fragment.show(getSupportFragmentManager(), "AddPaymentMethodFragment");
        });
        BookBtn.setOnClickListener(v -> confirmBooking());

        // Fetch payment methods
        fetchPaymentMethods();
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
    }

    public void fetchPaymentMethods() {
        ApiClient api = new ApiClient(this);
        api.fetchAllPaymentMethods(new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("fetchPaymentMethods", e.getMessage());
                    Toast.makeText(CheckoutActivity.this, "Failed to fetch payment methods", Toast.LENGTH_SHORT).show();
                    updatePaymentMethodUI(null);
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("fetchPaymentMethods", errorBody);
                    Toast.makeText(CheckoutActivity.this, "Error fetching payment methods", Toast.LENGTH_SHORT).show();
                    updatePaymentMethodUI(null);
                });
            }

            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetchPaymentMethods", responseBody);
                    Gson gson = new Gson();
                    TypeToken<List<PaymentMethod>> typeToken = new TypeToken<List<PaymentMethod>>() {};
                    List<PaymentMethod> paymentMethods = gson.fromJson(responseBody, typeToken.getType());
                    PaymentMethod cardMethod = null;
                    for (PaymentMethod method : paymentMethods) {
                        if (method.getType() == 4) {
                            cardMethod = method;
                            break;
                        }
                    }
                    updatePaymentMethodUI(cardMethod);
                });
            }
        });
    }

    private void updatePaymentMethodUI(PaymentMethod cardMethod) {
        if (cardMethod != null && cardMethod.getDetails() != null) {
            String cardNumber = cardMethod.getDetails().getCard_number();
            if (cardNumber != null && cardNumber.length() >= 8) {
                String lastEight = cardNumber.substring(cardNumber.length() - 8);
                String formatted = "**** " + lastEight.substring(4);
                PaymentMethodTV.setText(formatted);
                PaymentMethodImageIV.setImageResource(R.drawable.credit_card);
                PaymentMethodImageIV.setVisibility(View.VISIBLE);
            } else {
                PaymentMethodTV.setText("None");
                PaymentMethodImageIV.setVisibility(View.GONE);
            }
        } else {
            PaymentMethodTV.setText("None");
            PaymentMethodImageIV.setVisibility(View.GONE);
        }
    }

    private void confirmBooking() {
        // Bypassing payment method check for testing
        createAppointment();
    }

    private void createAppointment() {
        if (salonId == -1) {
            Toast.makeText(this, "Invalid salon ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        if (serviceId == -1 || masterId == null || appointmentTimeMillis == 0) {
            Toast.makeText(this, "Invalid appointment details", Toast.LENGTH_SHORT).show();
            return;
        }

        AppointmentInsert appointment = new AppointmentInsert(
                userId,
                masterId,
                serviceId,
                salonId,
                new Date(appointmentTimeMillis),
                null // coupon_id
        );

        ApiClient apiClient = new ApiClient(this);
        apiClient.createAppointment(appointment, new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("create:appointment", "success");
                    Intent intent = new Intent(CheckoutActivity.this, BookingCompleteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("appointment_time", appointmentTimeMillis);
                    intent.putExtra("service_name", serviceName);
                    intent.putExtra("master_name", masterName);
                    intent.putExtra("salon_address", salonAddress);
                    startActivity(intent);
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("create:appointment", e.getMessage());
                    Toast.makeText(CheckoutActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("create:appointment", errorBody);
                    Toast.makeText(CheckoutActivity.this, "Failed to create appointment", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}