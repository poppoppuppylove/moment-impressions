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
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvNickname;
        TextView tvContent;
        TextView tvTime;
        TextView tvLikes;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvLikes = itemView.findViewById(R.id.tv_likes);
        }

        public void bind(CommentItem item) {
            tvNickname.setText(item.getAuthor().getNickname());
            tvContent.setText(item.getContent());
            tvTime.setText(item.getTime());
            tvLikes.setText(String.valueOf(item.getLikesCount()));
            ImageLoader.loadRounded(itemView.getContext(), item.getAuthor().getAvatarUrl(), ivAvatar, 32);
        }
    }
}
