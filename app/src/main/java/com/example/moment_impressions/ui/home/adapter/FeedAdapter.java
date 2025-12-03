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

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<FeedItem> items = new ArrayList<>();
    private boolean isFooterVisible = false;

    public void setItems(List<FeedItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void addItems(List<FeedItem> items) {
        int start = this.items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(start, items.size());
    }

    public void setFooterVisible(boolean visible) {
        if (isFooterVisible != visible) {
            isFooterVisible = visible;
            if (visible) {
                notifyItemInserted(items.size());
            } else {
                notifyItemRemoved(items.size());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterVisible && position == items.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_loading, parent, false);
            // StaggeredGridLayoutManager needs full span for footer
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            if (lp instanceof androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams) {
                ((androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
            } else {
                // If layout params are generic margin params or similar, cast to Staggered... might fail if not attached yet?
                // Safest is to create new Staggered params or handle in onBind if needed.
                // But usually inflation with parent works.
                // Let's ensure it's StaggeredLayoutParams
                androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams staggeredLp = 
                    new androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                staggeredLp.setFullSpan(true);
                view.setLayoutParams(staggeredLp);
            }
            return new FooterViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedViewHolder) {
            FeedItem item = items.get(position);
            ((FeedViewHolder) holder).bind(item);
        } else if (holder instanceof FooterViewHolder) {
            // Ensure full span
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams) {
                ((androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size() + (isFooterVisible ? 1 : 0);
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        ImageView ivAvatar;
        TextView tvAuthor;
        TextView tvLikes;
        ImageView ivLikeIndicator;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvLikes = itemView.findViewById(R.id.tv_likes);
            ivLikeIndicator = itemView.findViewById(R.id.iv_like_indicator);

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
                    intent.putExtra(com.example.moment_impressions.ui.detail.DetailActivity.EXTRA_AUTHOR_NAME,
                            item.getAuthor().getNickname());
                    intent.putExtra(com.example.moment_impressions.ui.detail.DetailActivity.EXTRA_AUTHOR_AVATAR,
                            item.getAuthor().getAvatarUrl());

                    String transitionName = "feed_image_" + item.getId();
                    intent.putExtra("extra_transition_name", transitionName);

                    android.app.Activity activity = getActivityFromView(v);
                    if (activity != null) {
                        androidx.core.app.ActivityOptionsCompat options = androidx.core.app.ActivityOptionsCompat
                                .makeSceneTransitionAnimation(activity, ivCover, transitionName);
                        v.getContext().startActivity(intent, options.toBundle());
                    } else {
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }

        private android.app.Activity getActivityFromView(View view) {
            android.content.Context context = view.getContext();
            while (context instanceof android.content.ContextWrapper) {
                if (context instanceof android.app.Activity) {
                    return (android.app.Activity) context;
                }
                context = ((android.content.ContextWrapper) context).getBaseContext();
            }
            return null;
        }

        public void bind(FeedItem item) {
            tvTitle.setText(item.getTitle());
            tvAuthor.setText(item.getAuthor().getNickname());
            tvLikes.setText(String.valueOf(item.getLikesCount()));

            // 点赞标识高亮
            if (ivLikeIndicator != null) {
                if (item.isLiked()) {
                    ivLikeIndicator.setImageResource(android.R.drawable.btn_star_big_on);
                    ivLikeIndicator.setColorFilter(itemView.getResources().getColor(android.R.color.holo_orange_light));
                } else {
                    ivLikeIndicator.setImageResource(android.R.drawable.btn_star);
                    ivLikeIndicator.setColorFilter(itemView.getResources().getColor(android.R.color.darker_gray));
                }
            }

            // Set random height for staggered effect
            ViewGroup.LayoutParams params = ivCover.getLayoutParams();
            if (item.getHeight() > 0) {
                params.height = item.getHeight();
            } else {
                params.height = 400; // Default fallback
            }
            ivCover.setLayoutParams(params);

            // Use ImageLoader with Rounded (CenterCrop) to fill the space without white gaps
            ImageLoader.loadRounded(itemView.getContext(), item.getImageUrl(), ivCover, 8);
            ivCover.setTransitionName("feed_image_" + item.getId());
            ImageLoader.loadRounded(itemView.getContext(), item.getAuthor().getAvatarUrl(), ivAvatar, 16);
        }
    }
}
