package com.example.moment_impressions.ui.profile.posts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.moment_impressions.core.base.BaseViewModel;
import com.example.moment_impressions.data.model.FeedItem;
import com.example.moment_impressions.data.repository.FeedRepository;
import java.util.List;

public class MyPostsViewModel extends BaseViewModel {
    private final FeedRepository repository = FeedRepository.getInstance();
    private final MutableLiveData<List<FeedItem>> list = new MutableLiveData<>();

    public LiveData<List<FeedItem>> getList() { return list; }

    public void load(String userId) {
        repository.getMyPosts(userId).observeForever(items -> list.setValue(items));
    }
}