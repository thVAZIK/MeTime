package com.example.metime.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.metime.Models.DayItem;
import com.example.metime.R;
import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    private final List<DayItem> dayItems;
    private final Context context;
    private OnDaySelectedListener listener;

    public DayAdapter(List<DayItem> dayItems, Context context) {
        this.dayItems = dayItems;
        this.context = context;
    }

    public void setOnDaySelectedListener(OnDaySelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_in_month, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DayItem item = dayItems.get(position);
        String dayText = String.format("%02d %s", item.getDayOfMonth(), item.getDayName());
        holder.dayButton.setText(dayText);
        holder.dayButton.setSelected(item.isSelected());

        holder.dayButton.setOnClickListener(v -> {
            for (int i = 0; i < dayItems.size(); i++) {
                dayItems.get(i).setSelected(i == position);
            }
            notifyDataSetChanged();
            if (listener != null) {
                listener.onDaySelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button dayButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayButton = itemView.findViewById(R.id.dayButton);
        }
    }

    public interface OnDaySelectedListener {
        void onDaySelected(DayItem dayItem);
    }
}