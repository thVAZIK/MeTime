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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.metime.Fragments.BeforeScheduleFragment;
import com.example.metime.Models.Master;
import com.example.metime.OnBoardingChooseProfessional;
import com.example.metime.R;

import java.util.List;

public class ChooseMastersAdapter extends RecyclerView.Adapter<ChooseMastersAdapter.ViewHolder> {
    private List<Master> items;
    private Context context;
    private FragmentManager fragmentManager;

    public ChooseMastersAdapter(List<Master> items, Context context, FragmentManager fragmentManager) {
        this.items = items;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_professional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Master item = items.get(position);
        holder.MasterFullNameTV.setText(item.getFirst_name() + " " + item.getLast_name());
        holder.MasterSpecializationTV.setText(item.getSpecialization().getName());
        holder.MasterRatingTV.setText(String.valueOf(item.getMasterRatingsSummaries().get(0).getAverage_rating()));
        String url = "https://xjbqmokswhilgipdgose.supabase.co/storage/v1/object/public/mastersimages//";
        Glide.with(context)
                .load(url + item.getImage_link())
                .error(R.drawable.placeholder_banner)
                .into(holder.MasterAvatarIV);
        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeforeScheduleFragment fragment = new BeforeScheduleFragment();
                fragment.show(fragmentManager, "BannerFragment");
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        TextView MasterFullNameTV, MasterSpecializationTV, MasterRatingTV;
        ImageView MasterAvatarIV;
        ConstraintLayout main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MasterFullNameTV = itemView.findViewById(R.id.MasterFullNameTV);
            MasterSpecializationTV = itemView.findViewById(R.id.MasterSpecializationTV);
            MasterRatingTV = itemView.findViewById(R.id.MasterRatingTV);
            MasterAvatarIV = itemView.findViewById(R.id.MasterAvatarIV);
            main = itemView.findViewById(R.id.main);
        }
    }
}
