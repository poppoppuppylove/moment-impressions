package com.example.moment_impressions.data.model;

import java.util.ArrayList;
import java.util.List;

public class FeedItem {
    private String id;
    private String title;
    private String imageUrl;
    private User author;
    private int likesCount;
    private int height; // For staggered grid layout simulation

    private String content;
    private String time;

    private List<String> images;

    public FeedItem(String id, String title, String imageUrl, User author, int likesCount) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.author = author;
        this.likesCount = likesCount;
        this.content = title
                + " - This is the detailed content of the feed. It contains more information about the topic.";
        this.time = "2h ago";
        this.images = new ArrayList<>();
        this.images.add(imageUrl);
    }

    public FeedItem(String id, String title, String content, String imageUrl, User author, int likesCount,
            String time) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.author = author;
        this.likesCount = likesCount;
        this.time = time;
        this.images = new ArrayList<>();
        this.images.add(imageUrl);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        if (this.images.isEmpty()) {
            this.images.add(imageUrl);
        } else {
            this.images.set(0, imageUrl);
        }
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
        if (images != null && !images.isEmpty()) {
            this.imageUrl = images.get(0);
        }
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
