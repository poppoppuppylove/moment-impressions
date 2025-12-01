package com.example.moment_impressions.core.utils;

import android.content.Context;
import android.widget.ImageView;
import android.net.Uri;
import java.io.File;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class ImageLoader {

    public static void load(Context context, String url, ImageView imageView) {
        if (context == null)
            return;
        Object model = buildModel(url);
        Glide.with(context)
                .load(model)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .thumbnail(0.25f)
                .error(com.example.moment_impressions.R.drawable.sample_cover)
                .into(imageView);
    }

    public static void loadRounded(Context context, String url, ImageView imageView, int radius) {
        if (context == null)
            return;
        Object model = buildModel(url);
        Glide.with(context)
                .load(model)
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(radius)))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .thumbnail(0.25f)
                .error(com.example.moment_impressions.R.drawable.sample_cover)
                .into(imageView);
    }

    public static void loadFitCenter(Context context, String url, ImageView imageView) {
        if (context == null)
            return;
        Object model = buildModel(url);
        Glide.with(context)
                .load(model)
                .apply(new RequestOptions().transform(new FitCenter()))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .thumbnail(0.25f)
                .error(com.example.moment_impressions.R.drawable.sample_cover)
                .into(imageView);
    }

    public static void loadFitCenterRounded(Context context, String url, ImageView imageView, int radius) {
        if (context == null)
            return;
        Object model = buildModel(url);
        Glide.with(context)
                .load(model)
                .apply(new RequestOptions().transform(new FitCenter(), new RoundedCorners(radius)))
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .thumbnail(0.25f)
                .error(com.example.moment_impressions.R.drawable.sample_cover)
                .into(imageView);
    }

    private static Object buildModel(String url) {
        if (url == null) return null;
        String lower = url.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            return url;
        }
        // Handle content://, file://, android.resource:// and local paths as Uri
        if (lower.startsWith("content://") || lower.startsWith("file://") || 
            lower.startsWith("android.resource://") || 
            lower.startsWith("/storage/") || lower.startsWith("/sdcard/") || lower.startsWith("/mnt/")) {
            
            if (lower.startsWith("/")) {
                return Uri.fromFile(new File(url));
            }
            return Uri.parse(url);
        }
        return url;
    }
}
