package com.example.metime;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metime.Adapters.DayAdapter;
import com.example.metime.Adapters.TimeSlotAdapter;
import com.example.metime.Models.DayItem;
import com.example.metime.Models.MasterCalendar;
import com.example.metime.Models.TimeSlotItem;
import com.example.metime.Tools.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BookSelectSlotActivity extends AppCompatActivity {
    private TextView monthSelectTV, yearSelectTV;
    private LinearLayout monthAndYearSelectBtn;
    private RecyclerView dayListRV, timeListRV;
    private MaterialButton bookBtn;
    private ImageView backBtn;
    private DayAdapter dayAdapter;
    private TimeSlotAdapter timeSlotAdapter;
    private List<DayItem> dayItems;
    private List<TimeSlotItem> timeSlots;
    private List<MasterCalendar> masterCalendars;
    private Calendar selectedDate;
    private TimeSlotItem selectedTimeSlot;
    private int serviceId, serviceDuration, salonId;
    private String serviceName, salonAddress;
    private int servicePrice;
    private String serviceImageLink;

    private void init() {
        monthSelectTV = findViewById(R.id.MonthSelectTV);
        yearSelectTV = findViewById(R.id.YearSelectTV);
        monthAndYearSelectBtn = findViewById(R.id.MonthAndYearSelectBtn);
        dayListRV = findViewById(R.id.DayListRV);
        timeListRV = findViewById(R.id.TimeListRV);
        bookBtn = findViewById(R.id.BookBtn);
        backBtn = findViewById(R.id.BackBtn);
        dayItems = new ArrayList<>();
        timeSlots = new ArrayList<>();
        masterCalendars = new ArrayList<>();
        selectedDate = Calendar.getInstance();

        // Get service details from Intent
        Intent intent = getIntent();
        serviceId = intent.getIntExtra("service_id", -1);
        serviceName = intent.getStringExtra("service_name");
        servicePrice = intent.getIntExtra("service_price", 0);
        serviceDuration = intent.getIntExtra("service_duration", 0);
        serviceImageLink = intent.getStringExtra("service_image_link");
        salonId = intent.getIntExtra("salon_id", -1);
        salonAddress = intent.getStringExtra("salon_address");

        // Setup RecyclerView
        dayListRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        dayAdapter = new DayAdapter(dayItems, this);
        dayAdapter.setOnDaySelectedListener(this::onDaySelected);
        dayListRV.setAdapter(dayAdapter);

        timeListRV.setLayoutManager(new GridLayoutManager(this, 2));
        timeSlotAdapter = new TimeSlotAdapter(timeSlots, this);
        timeSlotAdapter.setOnTimeSlotSelectedListener(timeSlot -> {
            selectedTimeSlot = timeSlot;
            bookBtn.setEnabled(true);
        });
        timeListRV.setAdapter(timeSlotAdapter);

        // Initialize month and year
        updateMonthYearDisplay();
        updateDayList();

        // Listeners
        monthAndYearSelectBtn.setOnClickListener(v -> showDatePicker());
        backBtn.setOnClickListener(v -> finish());
        bookBtn.setOnClickListener(v -> proceedToPayment());
        bookBtn.setEnabled(false);

        // Load data
        fetchMasterCalendars();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_select_slot);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void updateMonthYearDisplay() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        monthSelectTV.setText(monthFormat.format(selectedDate.getTime()));
        yearSelectTV.setText(" " + yearFormat.format(selectedDate.getTime()));
    }

    private void updateDayList() {
        dayItems.clear();
        Calendar calendar = (Calendar) selectedDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());

        for (int day = 1; day <= maxDay; day++) {
            calendar.set(Calendar.DAY_OF_MONTH, day);
            String dayName = dayNameFormat.format(calendar.getTime());
            DayItem dayItem = new DayItem(day, dayName, (Calendar) calendar.clone());
            if (day == selectedDate.get(Calendar.DAY_OF_MONTH)) {
                dayItem.setSelected(true);
            }
            dayItems.add(dayItem);
        }
        dayAdapter.notifyDataSetChanged();
        updateTimeSlots();
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, 1);
                    updateMonthYearDisplay();
                    updateDayList();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void fetchMasterCalendars() {
        ApiClient apiClient = new ApiClient(this);
        apiClient.fetchAllMasterCalendars(new ApiClient.SBC_Callback() {
            @Override
            public void onResponse(String responseBody) {
                runOnUiThread(() -> {
                    Log.d("fetch:masterCalendars", responseBody);
                    try {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<MasterCalendar>>() {}.getType();
                        List<MasterCalendar> calendars = gson.fromJson(responseBody, type);
                        if (calendars != null) {
                            masterCalendars.clear();
                            masterCalendars.addAll(calendars);
                            updateTimeSlots();
                        }
                    } catch (Exception e) {
                        Log.e("fetch:masterCalendars:parse", e.getMessage());
                        Toast.makeText(BookSelectSlotActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(IOException e) {
                runOnUiThread(() -> {
                    Log.e("fetch:masterCalendars", e.getMessage());
                    Toast.makeText(BookSelectSlotActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorBody) {
                runOnUiThread(() -> {
                    Log.e("fetch:masterCalendars", errorBody);
                    Toast.makeText(BookSelectSlotActivity.this, "Ошибка API", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void onDaySelected(DayItem dayItem) {
        selectedDate = dayItem.getDate();
        updateTimeSlots();
    }

    private void updateTimeSlots() {
        timeSlots.clear();
        selectedTimeSlot = null;
        bookBtn.setEnabled(false);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDateStr = dateFormat.format(selectedDate.getTime());

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        for (MasterCalendar calendar : masterCalendars) {
            if (calendar.isIs_available()) {
                try {
                    String startTimeStr = calendar.getStart_time().split("\\+")[0];
                    String endTimeStr = calendar.getEnd_time().split("\\+")[0];
                    SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC+06:00"));
                    Date startTime = inputFormat.parse(startTimeStr);
                    Date endTime = inputFormat.parse(endTimeStr);

                    Calendar slotTime = Calendar.getInstance();
                    slotTime.setTime(startTime);
                    Calendar endSlotTime = Calendar.getInstance();
                    endSlotTime.setTime(endTime);

                    while (slotTime.before(endSlotTime)) {
                        Calendar slotEndTime = (Calendar) slotTime.clone();
                        slotEndTime.add(Calendar.MINUTE, serviceDuration);
                        if (slotEndTime.after(endSlotTime)) {
                            break;
                        }

                        String formattedTime = timeFormat.format(slotTime.getTime());
                        String masterName = "with " + calendar.getMasters().getFirst_name();
                        TimeSlotItem timeSlot = new TimeSlotItem(
                                formattedTime,
                                masterName,
                                calendar.getMaster_id(),
                                calendar.getCalendar_id()
                        );
                        timeSlots.add(timeSlot);
                        slotTime.add(Calendar.MINUTE, 30);
                    }
                } catch (Exception e) {
                    Log.e("updateTimeSlots", "Parse error: " + e.getMessage());
                }
            }
        }
        timeSlotAdapter.notifyDataSetChanged();
    }

    private void proceedToPayment() {
        if (selectedTimeSlot == null) {
            Toast.makeText(this, "Выберите время", Toast.LENGTH_SHORT).show();
            return;
        }

        if (serviceId == -1) {
            Toast.makeText(this, "Услуга не выбрана", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar appointmentTime = (Calendar) selectedDate.clone();
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date time = timeFormat.parse(selectedTimeSlot.getTime());
            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(time);
            appointmentTime.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            appointmentTime.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
            appointmentTime.set(Calendar.SECOND, 0);
            appointmentTime.set(Calendar.MILLISECOND, 0);
        } catch (Exception e) {
            Log.e("proceedToPayment", "Time parse error: " + e.getMessage());
            Toast.makeText(this, "Ошибка обработки времени", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("service_id", serviceId);
        intent.putExtra("service_name", serviceName);
        intent.putExtra("service_price", servicePrice);
        intent.putExtra("service_duration", serviceDuration);
        intent.putExtra("service_image_link", serviceImageLink);
        intent.putExtra("master_id", selectedTimeSlot.getMasterId());
        intent.putExtra("appointment_time", appointmentTime.getTimeInMillis());
        intent.putExtra("master_name", selectedTimeSlot.getMasterName().replace("with ", ""));
        intent.putExtra("calendar_id", selectedTimeSlot.getCalendarId());
        intent.putExtra("salon_id", salonId);
        intent.putExtra("salon_address", salonAddress);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}