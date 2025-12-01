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
            // repository.addComment returns a new list or the added comment
            // The repository ALREADY updates the cache.
            // If we manually add it here again, it might be duplicated if the observer triggers twice or if we are not careful.
            // However, the repository returns the *added comment* in the LiveData.
            // And the `getCommentList()` is observing `repository.getComments(feedId)`.
            
            // Actually, `repository.addComment` returns a LiveData<CommentItem> (the new comment).
            // `repository.getComments(feedId)` returns a LiveData<List<CommentItem>>.
            
            // If we modify the list inside `commentList` (which is `MutableLiveData`), we should be fine.
            // BUT wait, `repository.getComments` uses `commentCache`. 
            // When `repository.addComment` runs, it adds to `commentCache`.
            // If `DetailViewModel` is observing the list from `repository` (it's not, it gets a one-time snapshot in `loadComments` which observes `repository.getComments`), 
            // then we need to update our local list.
            
            // The issue is likely that `addComment` is triggered, and maybe the UI is observing something that updates automatically?
            // Or `addComment` in Repository does something weird.
            
            // Let's look at Repository.addComment:
            // It adds to `commentCache` and returns the comment.
            // `DetailViewModel.loadComments` observes `repository.getComments`.
            // `repository.getComments` returns a *new* LiveData with the list.
            
            // In `DetailViewModel.addComment`:
            List<CommentItem> currentList = commentList.getValue();
            if (currentList != null) {
                // Check if it already exists to avoid duplicates (basic protection)
                boolean exists = false;
                for(CommentItem c : currentList) {
                    if(c.getId().equals(comment.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    currentList.add(0, comment);
                    commentList.setValue(currentList);
                }
            }
        });
    }

    public void toggleFavorite(String feedId, boolean isFavorite) {
        repository.toggleFavorite(feedId, isFavorite);
    }

    public boolean isFavorite(String feedId) {
        return repository.isFavorite(feedId);
    }

    public boolean isLiked(String feedId) {
        return repository.isLiked(feedId);
    }
}
