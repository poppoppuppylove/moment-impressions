package com.example.moment_impressions.ui.detail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.utils.ImageLoader;
import com.example.moment_impressions.data.model.CommentItem;
import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<CommentItem> items = new ArrayList<>();

    public void setItems(List<CommentItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private OnCommentLikeListener likeListener;

    public void setOnCommentLikeListener(OnCommentLikeListener listener) {
        this.likeListener = listener;
    }

    public interface OnCommentLikeListener {
        void onLikeClick(CommentItem item, int position);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvNickname;
        TextView tvContent;
        TextView tvTime;
        TextView tvLikes;
        ImageView ivLike; // Need to find this view

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLikes = itemView.findViewById(R.id.tv_likes);
            // Assuming the like icon is the ImageView inside the vertical LinearLayout at
            // the end
            // Let's find it by traversing or assuming ID.
            // The layout item_comment.xml has a LinearLayout at the end with an ImageView
            // and TextView.
            // The ImageView doesn't have an ID in the previous xml content I saw (it was
            // just <ImageView ... />).
            // I need to update item_comment.xml to give it an ID first?
            // Wait, I can find it via the parent layout if I know the structure, but adding
            // ID is better.
            // Let's assume I will add ID `iv_comment_like` to item_comment.xml.
            ivLike = itemView.findViewById(R.id.iv_comment_like);
        }

        public void bind(CommentItem item, OnCommentLikeListener listener) {
            tvNickname.setText(item.getAuthor().getNickname());
            tvContent.setText(item.getContent());
            tvTime.setText(item.getTime());
            tvLikes.setText(String.valueOf(item.getLikesCount()));
            ImageLoader.loadRounded(itemView.getContext(), item.getAuthor().getAvatarUrl(), ivAvatar, 32);

            if (ivLike != null) {
                updateLikeState(item);
                ivLike.setOnClickListener(v -> {
                    item.setLiked(!item.isLiked());
                    item.setLikesCount(item.isLiked() ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                    tvLikes.setText(String.valueOf(item.getLikesCount()));
                    updateLikeState(item);
                    if (listener != null) {
                        listener.onLikeClick(item, getAdapterPosition());
                    }
                });
            }
        }

        private void updateLikeState(CommentItem item) {
            if (item.isLiked()) {
                ivLike.setColorFilter(itemView.getContext().getResources().getColor(android.R.color.holo_red_light));
            } else {
                ivLike.setColorFilter(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentItem item = items.get(position);
        holder.bind(item, likeListener);
    }
}
