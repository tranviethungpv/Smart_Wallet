package com.example.smartwallet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartwallet.R;

import java.util.List;

public class BannerPagerAdapter extends RecyclerView.Adapter<BannerPagerAdapter.BannerViewHolder> {
    private final List<Integer> layoutIds;

    public BannerPagerAdapter(List<Integer> layoutIds) {
        this.layoutIds = layoutIds;
    }

    @NonNull
    @Override
    public BannerPagerAdapter.BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new BannerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerPagerAdapter.BannerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return layoutIds.size();
    }

    @Override
    public int getItemViewType(int position) {
        return layoutIds.get(position);
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
