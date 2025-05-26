package com.example.metime.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.R;

import java.util.List;

public class NavSelectAdapter extends RecyclerView.Adapter<NavSelectAdapter.ViewHolder> {
    private List<String> items;
    private Context context;

    public NavSelectAdapter(List<String> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.Button.setText(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        Button Button;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Button = itemView.findViewById(R.id.Button);
        }
    }
}
