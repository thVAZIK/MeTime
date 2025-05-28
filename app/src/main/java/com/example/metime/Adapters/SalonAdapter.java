package com.example.metime.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.metime.Models.Salon;
import com.example.metime.Models.SalonsRatingsSummary;

import java.util.ArrayList;
import java.util.List;
import com.example.metime.R;
import com.example.metime.SalonDetailsActivity;

public class SalonAdapter extends RecyclerView.Adapter<SalonAdapter.SalonViewHolder> {
    private static final String IMAGE_BASE_URL = "https://xjbqmokswhilgipdgose.supabase.co/storage/v1/object/public/salonsimages//";
    private List<Salon> salons = new ArrayList<>();

    static class SalonViewHolder extends RecyclerView.ViewHolder {
        ImageView salonImageIV;
        TextView salonNameTV;
        TextView salonAddressTV;
        TextView sumRateTV;
        ImageView enterBtn;
        CardView main;

        SalonViewHolder(View view) {
            super(view);
            salonImageIV = view.findViewById(R.id.SalonImageIV);
            salonNameTV = view.findViewById(R.id.SalonNameTV);
            salonAddressTV = view.findViewById(R.id.SalonAddressTV);
            sumRateTV = view.findViewById(R.id.SumRateTV);
            enterBtn = view.findViewById(R.id.EnterBtn);
            main = view.findViewById(R.id.main);
        }
    }

    @NonNull
    @Override
    public SalonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_salon_in_search, parent, false);
        return new SalonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalonViewHolder holder, int position) {
        Salon salon = salons.get(position);
        if (salon == null) return;

        holder.salonNameTV.setText(salon.getName() != null ? salon.getName() : "Unknown Salon");
        holder.salonAddressTV.setText(salon.getAddress() != null ? salon.getAddress() : "No Address");

        double averageRating = 0.0;
        if (salon.getSalons_Ratings_Summary() != null && !salon.getSalons_Ratings_Summary().isEmpty()) {
            SalonsRatingsSummary summary = salon.getSalons_Ratings_Summary().get(0);
            averageRating = summary.getAverage_rating();
        }
        holder.sumRateTV.setText(String.format("%.1f out of 5", averageRating));

        String imageUrl = salon.getImage_link() != null && !salon.getImage_link().isEmpty()
                ? IMAGE_BASE_URL + salon.getImage_link()
                : null;
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .error(R.drawable.placeholder_banner)
                .placeholder(R.drawable.placeholder_banner)
                .into(holder.salonImageIV);

        holder.main.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), SalonDetailsActivity.class); // Replace with actual activity
            intent.putExtra("salon_id", salon.getSalon_id());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return salons.size();
    }

    public void setSalons(List<Salon> salons) {
        this.salons = salons != null ? new ArrayList<>(salons) : new ArrayList<>();
        notifyDataSetChanged();
    }
}