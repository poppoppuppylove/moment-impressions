package com.example.moment_impressions.core.utils;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

public class ImageLoader {

    public static void load(Context context, String url, ImageView imageView) {
        if (context == null)
            return;
        Glide.with(context)
                .load(url)
                .into(imageView);
    }

    public static void loadRounded(Context context, String url, ImageView imageView, int radius) {
        if (context == null)
            return;
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(radius)))
                .into(imageView);
    }
}
