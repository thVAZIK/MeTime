package com.example.metime.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Models.CategoryService;
import com.example.metime.OnBoardingChooseFitActivity;
import com.example.metime.R;

import java.util.List;

public class CategoryServiceAdapter extends RecyclerView.Adapter<CategoryServiceAdapter.ViewHolder> {
    private List<CategoryService> items;
    private Context context;

    public CategoryServiceAdapter(List<CategoryService> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryService item = items.get(position);
        holder.CategoryNameTV.setText(item.getName());
        holder.ImageIV.setImageResource(item.getImageDrawableId());
        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, OnBoardingChooseFitActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView CategoryNameTV;
        ImageView ImageIV;
        ConstraintLayout main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CategoryNameTV = itemView.findViewById(R.id.CategoryNameTV);
            ImageIV = itemView.findViewById(R.id.ImageIV);
            main = itemView.findViewById(R.id.main);
        }
    }
}
