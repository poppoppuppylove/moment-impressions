package com.example.moment_impressions.data.model;

public class CommentItem {
    private String id;
    private String content;
    @com.google.gson.annotations.SerializedName("user")
    private User author;
    private String time;
    private int likesCount;

    private boolean isLiked;

    public CommentItem(String id, String content, User author, String time, int likesCount) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.time = time;
        this.likesCount = likesCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
