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
    private final Object lock = new Object(); // 读写安全锁

    // 打包资源示例（备用）
    private static final String LOCAL_SAMPLE_IMAGE = "android.resource://com.example.moment_impressions/drawable/sample_cover";
    // 虚拟机设备图片路径（用户提供）
    private static final String DEVICE_IMAGE_1 = "/storage/self/primary/Android/data/下载.jpg";
    private static final String DEVICE_IMAGE_2 = "/storage/self/primary/Android/data/【李惠利】💻电脑壁纸 聚光杂志系列桌布_4_泰兰德壁纸bot_来自小红书网页版.jpg";

    private FeedRepository() {
        // Initialize mock data
        for (int i = 0; i < 20; i++) {
            String id = String.valueOf(i);
            User user = new User("u" + id, "User " + id, "https://api.dicebear.com/7.x/avataaars/png?seed=" + id);

            // 使用打包资源示例图片作为封面，确保任意设备可正常显示
            String imageUrl = LOCAL_SAMPLE_IMAGE;

            FeedItem item = new FeedItem(id, "Title " + id,
                    "This is the detailed content for feed " + id + ". It describes the moment in detail.",
                    imageUrl, user, random.nextInt(1000), random.nextInt(24) + "h ago");
            item.setHeight(400 + random.nextInt(200)); // Random height for staggered effect

            // 为轮播添加更多图片（统一使用资源URI，避免设备路径不可读）
            List<String> images = new ArrayList<>();
            images.add(imageUrl);
            images.add(LOCAL_SAMPLE_IMAGE);
            int extraCount = 1 + random.nextInt(3);
            for (int j = 0; j < extraCount; j++) {
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
            synchronized (lock) {
                if (page == 0) {
                    result.addAll(localCache);
                }
                int pageSize = 10;
                int start = page * pageSize;
                if (start < allFeeds.size()) {
                    int end = Math.min(start + pageSize, allFeeds.size());
                    result.addAll(allFeeds.subList(start, end));
                }
            }
            data.setValue(result);
        }, 600);

        return data;
    }

    public LiveData<List<FeedItem>> getMyPosts(String userId) {
        MutableLiveData<List<FeedItem>> data = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            synchronized (lock) {
                data.setValue(new ArrayList<>(myPosts));
            }
        }, 200);
        return data;
    }

    public LiveData<List<FeedItem>> getFavorites(String userId) {
        MutableLiveData<List<FeedItem>> data = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            synchronized (lock) {
                data.setValue(new ArrayList<>(favoritesList));
            }
        }, 200);
        return data;
    }

    // 简单的网络刷新：拉取示例图片并更新部分 Feed 的图片列表
    public LiveData<Boolean> refreshFromNetwork() {
        androidx.lifecycle.MutableLiveData<Boolean> result = new androidx.lifecycle.MutableLiveData<>();
        com.example.moment_impressions.core.net.NetworkClient.getService().listPhotos(6)
                .enqueue(new retrofit2.Callback<java.util.List<com.example.moment_impressions.core.net.model.Photo>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.List<com.example.moment_impressions.core.net.model.Photo>> call,
                            retrofit2.Response<java.util.List<com.example.moment_impressions.core.net.model.Photo>> response) {
                        java.util.List<com.example.moment_impressions.core.net.model.Photo> photos = response.body();
                        if (photos != null && !photos.isEmpty()) {
                            java.util.List<String> urls = new java.util.ArrayList<>();
                            for (com.example.moment_impressions.core.net.model.Photo p : photos) {
                                if (p.url != null) urls.add(p.url);
                            }
                            synchronized (lock) {
                                // 将网络图片注入前两个帖子作为轮播补充
                                int count = Math.min(2, allFeeds.size());
                                for (int i = 0; i < count; i++) {
                                    FeedItem item = allFeeds.get(i);
                                    java.util.List<String> merged = new java.util.ArrayList<>();
                                    if (item.getImages() != null) merged.addAll(item.getImages());
                                    merged.addAll(urls);
                                    item.setImages(merged);
                                }
                            }
                            result.setValue(true);
                        } else {
                            result.setValue(false);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<java.util.List<com.example.moment_impressions.core.net.model.Photo>> call,
                            Throwable t) {
                        result.setValue(false);
                    }
                });
        return result;
    }

    public LiveData<List<CommentItem>> getComments(String feedId) {
        MutableLiveData<List<CommentItem>> data = new MutableLiveData<>();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            List<CommentItem> items;
            synchronized (lock) {
                if (commentCache.containsKey(feedId)) {
                    items = commentCache.get(feedId);
                } else {
                    items = new ArrayList<>();
                    for (int i = 0; i < 5; i++) {
                        String id = feedId + "_c_" + i;
                        User user = new User("u" + id, "User " + i,
                                "https://api.dicebear.com/7.x/avataaars/png?seed=" + id);
                        items.add(new CommentItem(id,
                                "This is a comment " + i + " for feed " + feedId,
                                user, "1h ago", random.nextInt(100)));
                    }
                    commentCache.put(feedId, items);
                }
            }
            data.setValue(items);
        }, 500);

        return data;
    }

    public LiveData<Boolean> addFeedItem(FeedItem item) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            synchronized (lock) {
                localCache.add(0, item);
                myPosts.add(0, item);
            }
            result.setValue(true);
        }, 500);
        return result;
    }

    public void toggleFeedLike(String feedId, boolean isLiked) {
        synchronized (lock) {
            for (FeedItem item : allFeeds) {
                if (item.getId().equals(feedId)) {
                    item.setLiked(isLiked);
                    item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                    break;
                }
            }
            for (FeedItem item : localCache) {
                if (item.getId().equals(feedId)) {
                    item.setLiked(isLiked);
                    item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                    break;
                }
            }
            for (FeedItem item : myPosts) {
                if (item.getId().equals(feedId)) {
                    item.setLiked(isLiked);
                    item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                    break;
                }
            }
            for (FeedItem item : favoritesList) {
                if (item.getId().equals(feedId)) {
                    item.setLiked(isLiked);
                    item.setLikesCount(isLiked ? item.getLikesCount() + 1 : item.getLikesCount() - 1);
                    break;
                }
            }
        }
    }

    public void toggleFavorite(String feedId, boolean isFavorite) {
        synchronized (lock) {
            FeedItem target = null;
            for (FeedItem item : allFeeds) { if (item.getId().equals(feedId)) { target = item; break; } }
            if (target == null) { for (FeedItem item : localCache) { if (item.getId().equals(feedId)) { target = item; break; } } }
            if (target == null) { for (FeedItem item : myPosts) { if (item.getId().equals(feedId)) { target = item; break; } } }
            if (target != null) {
                if (isFavorite) {
                    boolean exists = false;
                    for (FeedItem f : favoritesList) { if (f.getId().equals(feedId)) { exists = true; break; } }
                    if (!exists) favoritesList.add(0, target);
                } else {
                    for (int i = 0; i < favoritesList.size(); i++) {
                        if (favoritesList.get(i).getId().equals(feedId)) { favoritesList.remove(i); break; }
                    }
                }
            }
        }
    }

    public void toggleCommentLike(String feedId, String commentId, boolean isLiked) {
        synchronized (lock) {
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
    }

    public LiveData<CommentItem> addComment(String feedId, String content) {
        MutableLiveData<CommentItem> result = new MutableLiveData<>();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            User currentUser = new User("me", "Me", "https://api.dicebear.com/7.x/avataaars/png?seed=me");
            CommentItem comment = new CommentItem("new_" + System.currentTimeMillis(), content, currentUser, "Just now",
                    0);

            synchronized (lock) {
                List<CommentItem> comments = commentCache.get(feedId);
                if (comments == null) {
                    comments = new ArrayList<>();
                    commentCache.put(feedId, comments);
                }
                comments.add(0, comment);
            }

            result.setValue(comment);
        }, 400);
        return result;
    }

    // 查询是否已收藏，用于详情页初始状态展示
    public boolean isFavorite(String feedId) {
        synchronized (lock) {
            for (FeedItem f : favoritesList) {
                if (f.getId().equals(feedId)) return true;
            }
            return false;
        }
    }
}
