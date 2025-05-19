package com.example.metime.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Models.Appointment;
import com.example.metime.R;

import java.util.List;

public class BookingItemAdapter extends RecyclerView.Adapter<BookingItemAdapter.ViewHolder>{
    private List<Appointment> items;

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
        holder.DateTV.setText(item.getAppointment_time().getDate());
        holder.ServiceTV.setText(item.getServices().getName());
        // TODO
        holder.KmsTV.setText("5.0 Kms");
        // TODO
        holder.MasterFullNameTV.setText("with " + item.getMasters().getFirst_name() + " " + item.getMasters().getLast_name());
        holder.SalonNameTV.setText(item.getSalons().getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView SalonNameTV, MasterFullNameTV, KmsTV, ServiceTV, DateTV, CostTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            SalonNameTV = itemView.findViewById(R.id.SalonNameTV);
            MasterFullNameTV = itemView.findViewById(R.id.MasterFullNameTV);
            KmsTV = itemView.findViewById(R.id.KmsTV);
            ServiceTV = itemView.findViewById(R.id.ServiceTV);
            DateTV = itemView.findViewById(R.id.DateTV);
            CostTV = itemView.findViewById(R.id.CostTV);
        }
    }
}
