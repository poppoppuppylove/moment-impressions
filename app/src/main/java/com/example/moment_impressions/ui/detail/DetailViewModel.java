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
}
