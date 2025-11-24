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
    private final List<FeedItem> localCache = new ArrayList<>();

    private FeedRepository() {
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
            List<FeedItem> items = new ArrayList<>();

            // If page 0, add local cache first
            if (page == 0) {
                items.addAll(localCache);
            }

            for (int i = 0; i < 10; i++) {
                String id = String.valueOf(page * 10 + i);
                User user = new User("u" + id, "User " + id, "https://api.dicebear.com/7.x/avataaars/png?seed=" + id);
                String imageUrl = "https://picsum.photos/400/" + (400 + random.nextInt(200)) + "?random=" + id;
                FeedItem item = new FeedItem(id, "Title " + id,
                        "This is the detailed content for feed " + id + ". It describes the moment in detail.",
                        imageUrl, user, random.nextInt(1000), random.nextInt(24) + "h ago");
                item.setHeight(400 + random.nextInt(200)); // Random height for staggered effect
                items.add(item);
            }
            data.setValue(items);
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
}
