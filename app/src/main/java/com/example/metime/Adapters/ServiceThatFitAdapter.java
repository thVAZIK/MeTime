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

import com.bumptech.glide.Glide;
import com.example.metime.Models.Service;
import com.example.metime.OnBoardingChooseProfessional;
import com.example.metime.R;

import java.util.List;

public class ServiceThatFitAdapter extends RecyclerView.Adapter<ServiceThatFitAdapter.ViewHolder> {
    private List<Service> items;
    private Context context;

    public ServiceThatFitAdapter(List<Service> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_that_fit_your_needs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service item = items.get(position);
        holder.ServiceNameTV.setText(item.getName());
        holder.ServiceCostTV.setText("$" + item.getPrice());
        String url = "https://xjbqmokswhilgipdgose.supabase.co/storage/v1/object/public/servicesimages//";
        Glide.with(context)
                .load(url + item.getImage_link())
                .error(R.drawable.placeholder_banner)
                .into(holder.ImageIV);
        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, OnBoardingChooseProfessional.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ImageIV;
        TextView ServiceNameTV, ServiceCostTV;
        ConstraintLayout main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageIV = itemView.findViewById(R.id.ImageIV);
            ServiceNameTV = itemView.findViewById(R.id.ServiceNameTV);
            ServiceCostTV = itemView.findViewById(R.id.ServiceCostTV);
            main = itemView.findViewById(R.id.main);
        }
    }
}
