package com.example.moment_impressions.ui.home.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.utils.ImageLoader;
import com.example.moment_impressions.data.model.FeedItem;
import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private List<FeedItem> items = new ArrayList<>();

    public void setItems(List<FeedItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItems(List<FeedItem> items) {
        int start = this.items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(start, items.size());
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, int position) {
        FeedItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        ImageView ivAvatar;
        TextView tvAuthor;
        TextView tvLikes;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvLikes = itemView.findViewById(R.id.tv_likes);
        }

        public void bind(FeedItem item) {
            tvTitle.setText(item.getTitle());
            tvAuthor.setText(item.getAuthor().getNickname());
            tvLikes.setText(String.valueOf(item.getLikesCount()));

            // Set random height for staggered effect (optional, better handled by layout
            // params if needed,
            // but here we just let image load naturally or set constraints.
            // For true staggered, image aspect ratio matters.
            // We'll rely on Glide loading the image and adjustViewBounds=true)

            // Use ImageLoader
            ImageLoader.loadRounded(itemView.getContext(), item.getImageUrl(), ivCover, 8);
            ImageLoader.loadRounded(itemView.getContext(), item.getAuthor().getAvatarUrl(), ivAvatar, 16);
        }
    }
}
