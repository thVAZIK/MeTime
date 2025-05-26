package com.example.metime.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.metime.Models.Service;
import com.example.metime.R;

import java.util.List;

public class ServicesMainPageAdapter extends RecyclerView.Adapter<ServicesMainPageAdapter.ViewHolder> {
    private List<Service> items;
    private Context context;

    public ServicesMainPageAdapter(List<Service> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_services_main_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service item = items.get(position);
        holder.ServiceNameTV.setText(item.getName());
        holder.ServiceCostTV.setText("$" + item.getPrice());
        holder.ServiceDurationTV.setText(item.getDuration() + " mins");
        String url = "https://xjbqmokswhilgipdgose.supabase.co/storage/v1/object/public/servicesimages//";
        Glide.with(context)
                .load(url + item.getImage_link())
                .error(R.drawable.placeholder_banner)
                .into(holder.ImageOfServiceIV);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView ServiceNameTV, ServiceDurationTV, ServiceCostTV;
        ImageView ImageOfServiceIV;
        CardView main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ServiceNameTV = itemView.findViewById(R.id.ServiceNameTV);
            ServiceDurationTV = itemView.findViewById(R.id.ServiceDurationTV);
            ServiceCostTV = itemView.findViewById(R.id.ServiceCostTV);
            ImageOfServiceIV = itemView.findViewById(R.id.ImageOfServiceIV);
            main = itemView.findViewById(R.id.main);
        }
    }
}
