package com.example.metime.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.metime.Models.SalonReview;

import java.util.ArrayList;
import java.util.List;
import com.example.metime.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<SalonReview> reviews = new ArrayList<>();

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        LinearLayout authorAndRateLL;
        TextView userFullNameTV;
        TextView rateTV;
        TextView reviewTextTV;

        ReviewViewHolder(View view) {
            super(view);
            authorAndRateLL = view.findViewById(R.id.AuthorAndRateLL);
            userFullNameTV = view.findViewById(R.id.UserFullNameTV);
            rateTV = view.findViewById(R.id.RateTV);
            reviewTextTV = view.findViewById(R.id.ReviewTextTV);
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        SalonReview review = reviews.get(position);
        if (review == null) return;
        holder.userFullNameTV.setText(review.getUser_id() != null ? review.getProfiles().getUsername() : "Anon");
        holder.rateTV.setText(String.valueOf(review.getRating()));
        holder.reviewTextTV.setText(review.getComment() != null ? review.getComment() : "No comment provided");
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void setReviews(List<SalonReview> reviews) {
        this.reviews = reviews != null ? new ArrayList<>(reviews) : new ArrayList<>();
        notifyDataSetChanged();
    }
}