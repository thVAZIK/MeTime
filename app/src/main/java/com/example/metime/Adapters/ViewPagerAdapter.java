package com.example.metime.Adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Models.Appointment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private final List<List<Appointment>> dataSets = new ArrayList<>();

    public ViewPagerAdapter() {
        // TODO
        dataSets.add(new ArrayList<>()); // Past
        dataSets.add(new ArrayList<>()); // Upcoming
        // TODO
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView recyclerView = new RecyclerView(parent.getContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        recyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        return new ViewHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingItemAdapter adapter = new BookingItemAdapter(dataSets.get(position));
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return dataSets.size();
    }


    public void updateData(int tabIndex, List<Appointment> newData) {
        dataSets.set(tabIndex, newData);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        ViewHolder(@NonNull RecyclerView itemView) {
            super(itemView);
            recyclerView = itemView;
        }
    }
}