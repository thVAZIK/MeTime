package com.example.metime.Adapters;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.metime.Models.Appointment;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private final Context context;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private final List<List<Appointment>> dataSets = new ArrayList<>();

    public ViewPagerAdapter(Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        dataSets.add(new ArrayList<>()); // Past
        dataSets.add(new ArrayList<>()); // Upcoming
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
        recyclerView.setNestedScrollingEnabled(false); // Prevent nested scrolling issues

        // Disable SwipeRefreshLayout during RecyclerView scrolling
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startY = event.getY();
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaY = Math.abs(event.getY() - startY);
                        if (deltaY > 10) { // Threshold for vertical scroll
                            swipeRefreshLayout.setEnabled(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false; // Let RecyclerView handle the event
            }
        });

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
        notifyItemChanged(tabIndex);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        ViewHolder(@NonNull RecyclerView itemView) {
            super(itemView);
            recyclerView = itemView;
        }
    }
}