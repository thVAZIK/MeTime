package com.example.metime.Adapters;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.transition.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.metime.Models.Banner;
import com.example.metime.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {
    private List<Banner> items;
    private Context context;

    public BannerAdapter(List<Banner> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Banner item = items.get(position);
        holder.BannerTextTV.setText(item.getText());
        String url = "https://xjbqmokswhilgipdgose.supabase.co/storage/v1/object/public/bannersimages//";
        Glide.with(context)
                .load(url + item.getImage_url())
                .error(R.drawable.placeholder_banner)
                .into(holder.BannerImageIV);
        if (item.getLink_url() != null) {
            holder.main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String url = item.getLink_url();
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "https://" + url;
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to open link", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.OpenLinkIconIV.setVisibility(VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        ImageView BannerImageIV, OpenLinkIconIV;
        TextView BannerTextTV;
        CardView main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            BannerImageIV = itemView.findViewById(R.id.BannerImageIV);
            BannerTextTV = itemView.findViewById(R.id.BannerTextTV);
            OpenLinkIconIV = itemView.findViewById(R.id.OpenLinkIconIV);
            main = itemView.findViewById(R.id.main);
        }
    }
}
