package com.example.moment_impressions.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.moment_impressions.core.base.BaseViewModel;
import com.example.moment_impressions.data.model.CommentItem;
import com.example.moment_impressions.data.repository.FeedRepository;
import java.util.List;

public class DetailViewModel extends BaseViewModel {

    private final FeedRepository repository;
    private final MutableLiveData<List<CommentItem>> commentList = new MutableLiveData<>();

    public DetailViewModel() {
        repository = FeedRepository.getInstance();
    }

    public LiveData<List<CommentItem>> getCommentList() {
        return commentList;
    }

    public void loadComments(String feedId) {
        repository.getComments(feedId).observeForever(comments -> {
            commentList.setValue(comments);
        });
    }

    public void toggleLike(String feedId, boolean isLiked) {
        repository.toggleFeedLike(feedId, isLiked);
    }

    public void toggleCommentLike(String feedId, String commentId, boolean isLiked) {
        repository.toggleCommentLike(feedId, commentId, isLiked);
    }

    public void addComment(String feedId, String content) {
        repository.addComment(feedId, content).observeForever(comment -> {
            List<CommentItem> currentList = commentList.getValue();
            if (currentList != null) {
                currentList.add(0, comment);
                commentList.setValue(currentList);
            }
        });
    }

    public void toggleFavorite(String feedId, boolean isFavorite) {
        repository.toggleFavorite(feedId, isFavorite);
    }

    public boolean isFavorite(String feedId) {
        return repository.isFavorite(feedId);
    }
}
