package com.example.moment_impressions.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.moment_impressions.data.model.CommentItem;
import com.example.moment_impressions.data.model.FeedItem;
import com.example.moment_impressions.data.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.os.Handler;
import android.os.Looper;

public class FeedRepository {

    private static FeedRepository instance;
    private final Random random = new Random();
    private final List<FeedItem> allFeeds = new ArrayList<>();
    private final List<FeedItem> localCache = new ArrayList<>(); // For newly published items

    // High quality sample images
    private static final String[] SAMPLE_IMAGES = {
            "https://images.unsplash.com/photo-1493246507139-91e8fad9978e?w=800&q=80",
            "https://images.unsplash.com/photo-1516483638261-f4dbaf036963?w=800&q=80",
            "https://images.unsplash.com/photo-1523906834658-6e24ef2386f9?w=800&q=80",
            "https://images.unsplash.com/photo-1494548162494-384bba4ab999?w=800&q=80",
            "https://images.unsplash.com/photo-1501504905252-473c47e087f8?w=800&q=80",
            "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=800&q=80",
            "https://images.unsplash.com/photo-1482938289607-e9573fc25ebb?w=800&q=80",
            "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80",
            "https://images.unsplash.com/photo-1533750516457-a7f992034fec?w=800&q=80",
            "https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=800&q=80"
    };

    private FeedRepository() {
        // Initialize mock data
        for (int i = 0; i < 20; i++) {
            String id = String.valueOf(i);
            User user = new User("u" + id, "User " + id, "https://api.dicebear.com/7.x/avataaars/png?seed=" + id);

            // Use fixed images cyclically
            String imageUrl = SAMPLE_IMAGES[i % SAMPLE_IMAGES.length];

            FeedItem item = new FeedItem(id, "Title " + id,
                    "This is the detailed content for feed " + id + ". It describes the moment in detail.",
                    imageUrl, user, random.nextInt(1000), random.nextInt(24) + "h ago");
            item.setHeight(400 + random.nextInt(200)); // Random height for staggered effect

            // Add more images for carousel
            List<String> images = new ArrayList<>();
            images.add(imageUrl);
            int imageCount = 2 + random.nextInt(4); // Total 3-6 images
            for (int j = 0; j < imageCount; j++) {
                // Use other images from the list
                images.add(SAMPLE_IMAGES[(i + j + 1) % SAMPLE_IMAGES.length]);
            }
            item.setImages(images);

            allFeeds.add(item);
        }
    }

    public static synchronized FeedRepository getInstance() {
        if (instance == null) {
            instance = new FeedRepository();
        }
        return instance;
    }

    public LiveData<List<FeedItem>> getFeedList(int page) {
        MutableLiveData<List<FeedItem>> data = new MutableLiveData<>();

        // Simulate network delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<FeedItem> result = new ArrayList<>();

            // If page 0, add local cache (newly published) first
            if (page == 0) {
                result.addAll(localCache);
            }

            int pageSize = 10;
            int start = page * pageSize;
            if (start < allFeeds.size()) {
                int end = Math.min(start + pageSize, allFeeds.size());
                result.addAll(allFeeds.subList(start, end));
            }

            data.setValue(result);
        }, 1000);

        return data;
    }

    public LiveData<List<CommentItem>> getComments(String feedId) {
        MutableLiveData<List<CommentItem>> data = new MutableLiveData<>();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<CommentItem> items = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                String id = feedId + "_c_" + i;
                User user = new User("u" + id, "User " + i, "https://api.dicebear.com/7.x/avataaars/png?seed=" + id);
                items.add(new CommentItem(id, "This is a comment " + i + " for feed " + feedId, user, "1h ago",
                        random.nextInt(100)));
            }
            data.setValue(items);
        }, 500);

        return data;
    }

    public LiveData<Boolean> addFeedItem(FeedItem item) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            localCache.add(0, item);
            result.setValue(true);
        }, 1000);
        return result;
    }

    public void toggleFeedLike(String feedId, boolean isLiked) {
        // Update in allFeeds
        for (FeedItem item : allFeeds) {
            if (item.getId().equals(feedId)) {
                item.setLiked(isLiked);
                item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                return;
            }
        }
        // Update in localCache
        for (FeedItem item : localCache) {
            if (item.getId().equals(feedId)) {
                item.setLiked(isLiked);
                item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                return;
            }
        }
    }

    public LiveData<CommentItem> addComment(String feedId, String content) {
        MutableLiveData<CommentItem> result = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            User currentUser = new User("me", "Me", "https://api.dicebear.com/7.x/avataaars/png?seed=me");
            CommentItem comment = new CommentItem("new_" + System.currentTimeMillis(), content, currentUser, "Just now",
                    0);
            result.setValue(comment);
        }, 500);
        return result;
    }
}
