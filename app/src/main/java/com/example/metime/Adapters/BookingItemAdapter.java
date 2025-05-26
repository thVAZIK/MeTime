package com.example.metime.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metime.Tools.ApiClient;
import com.example.metime.BookingsActivity;
import com.example.metime.Fragments.WarningCancelFragment;
import com.example.metime.Models.Appointment;
import com.example.metime.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingItemAdapter extends RecyclerView.Adapter<BookingItemAdapter.ViewHolder> {

    private final List<Appointment> items;

    public BookingItemAdapter(List<Appointment> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment item = items.get(position);
        holder.CostTV.setText("$" + item.getServices().getPrice());
        holder.DateTV.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(item.getAppointment_time()));
        holder.ServiceTV.setText(item.getServices().getName());
        holder.KmsTV.setText(String.format(Locale.getDefault(), "%.1f Kms", item.getMasters().getMasterRatingsSummaries().get(0).getAverage_rating()));
        holder.MasterFullNameTV.setText("with " + item.getMasters().getFirst_name() + " " + item.getMasters().getLast_name());
        holder.SalonNameTV.setText(item.getSalons().getName());

        Date currentTime = new Date();
        if (item.getAppointment_time().after(currentTime)) {
            holder.CancelBtn.setVisibility(View.VISIBLE);
            holder.CancelBtn.setOnClickListener(v -> {
                WarningCancelFragment dialog = WarningCancelFragment.newInstance();
                dialog.setDialogListener(new WarningCancelFragment.DialogListener() {
                    @Override
                    public void onCancelClicked() {
                        cancelAppointment(item.getAppointment_id(), holder);
                    }

                    @Override
                    public void onNoClicked() {
                    }
                });
                dialog.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), "CustomDialog");
            });
        } else {
            holder.CancelBtn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void cancelAppointment(int appointmentId, ViewHolder holder) {
        ApiClient api = new ApiClient((AppCompatActivity) holder.itemView.getContext());
        api.updateAppointmentStatus(appointmentId, 4, new ApiClient.SBC_Callback() {
            @Override
            public void onFailure(IOException e) {
                Log.e("cancelAppointment:onFailure", e.getLocalizedMessage());
                ((AppCompatActivity) holder.itemView.getContext()).runOnUiThread(() -> {
                    Toast.makeText(holder.itemView.getContext(), "Cancel error", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorBody) {
                ((AppCompatActivity) holder.itemView.getContext()).runOnUiThread(() -> {
                    Toast.makeText(holder.itemView.getContext(), "Cancel error", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(String responseBody) {
                ((AppCompatActivity) holder.itemView.getContext()).runOnUiThread(() -> {
                    Toast.makeText(holder.itemView.getContext(), "Appointment canceled!", Toast.LENGTH_SHORT).show();
                    ((BookingsActivity) holder.itemView.getContext()).fetchAppointments();
                });
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView SalonNameTV, MasterFullNameTV, KmsTV, ServiceTV, DateTV, CostTV, CancelBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            SalonNameTV = itemView.findViewById(R.id.SalonNameTV);
            MasterFullNameTV = itemView.findViewById(R.id.MasterFullNameTV);
            KmsTV = itemView.findViewById(R.id.KmsTV);
            ServiceTV = itemView.findViewById(R.id.ServiceTV);
            DateTV = itemView.findViewById(R.id.DateTV);
            CostTV = itemView.findViewById(R.id.CostTV);
            CancelBtn = itemView.findViewById(R.id.CancelBtn);
        }
    }
}