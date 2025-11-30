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
import java.util.HashMap;
import java.util.Map;

public class FeedRepository {

    private static FeedRepository instance;
    private final Random random = new Random();
    private final List<FeedItem> allFeeds = new ArrayList<>();
    private final List<FeedItem> localCache = new ArrayList<>(); // For newly published items
    private final List<FeedItem> myPosts = new ArrayList<>(); // User's own posts
    private final List<FeedItem> favoritesList = new ArrayList<>(); // Favorite posts
    private final Map<String, List<CommentItem>> commentCache = new HashMap<>();

    // Use local sample image for all demo items
    private static final String LOCAL_SAMPLE_IMAGE = "file:///D:/MyHome/momentimpressions/2025109212058.jpg";

    private FeedRepository() {
        // Initialize mock data
        for (int i = 0; i < 20; i++) {
            String id = String.valueOf(i);
            User user = new User("u" + id, "User " + id, "https://api.dicebear.com/7.x/avataaars/png?seed=" + id);

            // Use local sample image as cover
            String imageUrl = LOCAL_SAMPLE_IMAGE;

            FeedItem item = new FeedItem(id, "Title " + id,
                    "This is the detailed content for feed " + id + ". It describes the moment in detail.",
                    imageUrl, user, random.nextInt(1000), random.nextInt(24) + "h ago");
            item.setHeight(400 + random.nextInt(200)); // Random height for staggered effect

            // Add more images for carousel (use same local image multiple times)
            List<String> images = new ArrayList<>();
            images.add(imageUrl);
            int imageCount = 2 + random.nextInt(4); // Total 3-6 images
            for (int j = 0; j < imageCount; j++) {
                images.add(LOCAL_SAMPLE_IMAGE);
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

    public LiveData<List<FeedItem>> getMyPosts(String userId) {
        MutableLiveData<List<FeedItem>> data = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            data.setValue(new ArrayList<>(myPosts));
        }, 300);
        return data;
    }

    public LiveData<List<FeedItem>> getFavorites(String userId) {
        MutableLiveData<List<FeedItem>> data = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            data.setValue(new ArrayList<>(favoritesList));
        }, 300);
        return data;
    }

    public LiveData<List<CommentItem>> getComments(String feedId) {
        MutableLiveData<List<CommentItem>> data = new MutableLiveData<>();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (commentCache.containsKey(feedId)) {
                data.setValue(commentCache.get(feedId));
                return;
            }

            List<CommentItem> items = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                String id = feedId + "_c_" + i;
                User user = new User("u" + id, "User " + i, "https://api.dicebear.com/7.x/avataaars/png?seed=" + id);
                items.add(new CommentItem(id, "This is a comment " + i + " for feed " + feedId, user, "1h ago",
                        random.nextInt(100)));
            }
            commentCache.put(feedId, items);
            data.setValue(items);
        }, 500);

        return data;
    }

    public LiveData<Boolean> addFeedItem(FeedItem item) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            localCache.add(0, item);
            // Add to my posts as well
            myPosts.add(0, item);
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
                break;
            }
        }
        // Update in localCache
        for (FeedItem item : localCache) {
            if (item.getId().equals(feedId)) {
                item.setLiked(isLiked);
                item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                break;
            }
        }
        // Update in myPosts
        for (FeedItem item : myPosts) {
            if (item.getId().equals(feedId)) {
                item.setLiked(isLiked);
                item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                break;
            }
        }
        // Update in favoritesList
        for (FeedItem item : favoritesList) {
            if (item.getId().equals(feedId)) {
                item.setLiked(isLiked);
                item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                break;
            }
        }
    }

    public void toggleFavorite(String feedId, boolean isFavorite) {
        // Find item in allFeeds or localCache
        FeedItem target = null;
        for (FeedItem item : allFeeds) {
            if (item.getId().equals(feedId)) { target = item; break; }
        }
        if (target == null) {
            for (FeedItem item : localCache) {
                if (item.getId().equals(feedId)) { target = item; break; }
            }
        }
        if (target == null) {
            for (FeedItem item : myPosts) {
                if (item.getId().equals(feedId)) { target = item; break; }
            }
        }
        if (target != null) {
            if (isFavorite) {
                // Add if not existing
                boolean exists = false;
                for (FeedItem f : favoritesList) {
                    if (f.getId().equals(feedId)) { exists = true; break; }
                }
                if (!exists) favoritesList.add(0, target);
            } else {
                // Remove if existing
                for (int i = 0; i < favoritesList.size(); i++) {
                    if (favoritesList.get(i).getId().equals(feedId)) {
                        favoritesList.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public void toggleCommentLike(String feedId, String commentId, boolean isLiked) {
        List<CommentItem> comments = commentCache.get(feedId);
        if (comments != null) {
            for (CommentItem item : comments) {
                if (item.getId().equals(commentId)) {
                    item.setLiked(isLiked);
                    item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                    return;
                }
            }
        }
    }

    public LiveData<CommentItem> addComment(String feedId, String content) {
        MutableLiveData<CommentItem> result = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            User currentUser = new User("me", "Me", "https://api.dicebear.com/7.x/avataaars/png?seed=me");
            CommentItem comment = new CommentItem("new_" + System.currentTimeMillis(), content, currentUser, "Just now",
                    0);

            List<CommentItem> comments = commentCache.get(feedId);
            if (comments == null) {
                comments = new ArrayList<>();
                commentCache.put(feedId, comments);
            }
            comments.add(0, comment);

            result.setValue(comment);
        }, 500);
        return result;
    }
}
