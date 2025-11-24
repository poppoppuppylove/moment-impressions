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

    class FeedViewHolder extends RecyclerView.ViewHolder {
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

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    FeedItem item = items.get(position);
                    android.content.Intent intent = new android.content.Intent(v.getContext(),
                            com.example.moment_impressions.ui.detail.DetailActivity.class);
                    intent.putExtra(com.example.moment_impressions.ui.detail.DetailActivity.EXTRA_FEED_ID,
                            item.getId());
                    intent.putExtra(com.example.moment_impressions.ui.detail.DetailActivity.EXTRA_IMAGE_URL,
                            item.getImageUrl());
                    intent.putExtra(com.example.moment_impressions.ui.detail.DetailActivity.EXTRA_TITLE,
                            item.getTitle());
                    intent.putExtra(com.example.moment_impressions.ui.detail.DetailActivity.EXTRA_CONTENT,
                            item.getContent());
                    intent.putExtra(com.example.moment_impressions.ui.detail.DetailActivity.EXTRA_TIME,
                            item.getTime());
                    intent.putStringArrayListExtra(com.example.moment_impressions.ui.detail.DetailActivity.EXTRA_IMAGES,
                            new java.util.ArrayList<>(item.getImages()));
                    v.getContext().startActivity(intent);
                }
            });
        }

        public void bind(FeedItem item) {
            tvTitle.setText(item.getTitle());
            tvAuthor.setText(item.getAuthor().getNickname());
            tvLikes.setText(String.valueOf(item.getLikesCount()));

            // Set random height for staggered effect
            ViewGroup.LayoutParams params = ivCover.getLayoutParams();
            if (item.getHeight() > 0) {
                params.height = item.getHeight();
            } else {
                params.height = 400; // Default fallback
            }
            ivCover.setLayoutParams(params);

            // Use ImageLoader
            ImageLoader.loadRounded(itemView.getContext(), item.getImageUrl(), ivCover, 8);
            ImageLoader.loadRounded(itemView.getContext(), item.getAuthor().getAvatarUrl(), ivAvatar, 16);
        }
    }
}
