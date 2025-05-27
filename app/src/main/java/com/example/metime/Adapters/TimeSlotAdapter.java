package com.example.metime.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metime.Models.TimeSlotItem;
import com.example.metime.R;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {
    private final List<TimeSlotItem> timeSlots;
    private final Context context;
    private OnTimeSlotSelectedListener listener;

    public TimeSlotAdapter(List<TimeSlotItem> timeSlots, Context context) {
        this.timeSlots = timeSlots;
        this.context = context;
    }

    public void setOnTimeSlotSelectedListener(OnTimeSlotSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_with_master, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimeSlotItem item = timeSlots.get(position);
        String timeText = item.getTime() + "\n" + item.getMasterName();
        holder.timeButton.setText(timeText);
        holder.timeButton.setSelected(item.isSelected());

        holder.timeButton.setOnClickListener(v -> {
            for (int i = 0; i < timeSlots.size(); i++) {
                timeSlots.get(i).setSelected(i == position);
            }
            notifyDataSetChanged();
            if (listener != null) {
                listener.onTimeSlotSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button timeButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeButton = itemView.findViewById(R.id.timeButton);
        }
    }

    public interface OnTimeSlotSelectedListener {
        void onTimeSlotSelected(TimeSlotItem timeSlot);
    }
}