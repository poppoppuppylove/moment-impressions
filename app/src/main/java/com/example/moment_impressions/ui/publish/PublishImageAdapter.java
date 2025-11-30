package com.example.moment_impressions.ui.publish;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.utils.ImageLoader;
import java.util.ArrayList;
import java.util.List;

public class PublishImageAdapter extends RecyclerView.Adapter<PublishImageAdapter.ImageViewHolder> {

    private final List<String> imageUris = new ArrayList<>();

    public void setItems(List<String> uris) {
        imageUris.clear();
        if (uris != null) imageUris.addAll(uris);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publish_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(imageUris.get(position));
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }

        public void bind(String uri) {
            ImageLoader.load(itemView.getContext(), uri, imageView);
        }
    }
}