package com.example.metime.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Models.Coupon;
import com.example.metime.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {
    private List<Coupon> coupons = new ArrayList<>();

    static class CouponViewHolder extends RecyclerView.ViewHolder {
        ImageView couponIconIV;
        TextView percentCouponTV;
        TextView codeCouponTV;

        CouponViewHolder(View view) {
            super(view);
            couponIconIV = view.findViewById(R.id.CouponIconIV);
            percentCouponTV = view.findViewById(R.id.percentCouponTV);
            codeCouponTV = view.findViewById(R.id.CodeCouponTV);
        }
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        if (coupon == null) return;

        holder.percentCouponTV.setText(String.format(Locale.getDefault(), "%.0f%% off", coupon.getDiscount_percent() * 100));

        holder.codeCouponTV.setText(String.format("use code %s", coupon.getCode() != null ? coupon.getCode() : "N/A"));
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    public void setCoupons(List<Coupon> coupons) {
        this.coupons = coupons != null ? new ArrayList<>(coupons) : new ArrayList<>();
        notifyDataSetChanged();
    }
}
