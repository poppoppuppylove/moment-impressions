package com.example.moment_impressions.ui.profile.favorites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.moment_impressions.core.base.BaseViewModel;
import com.example.moment_impressions.data.model.FeedItem;
import com.example.moment_impressions.data.repository.FeedRepository;
import java.util.List;

public class MyFavoritesViewModel extends BaseViewModel {
    private final FeedRepository repository = FeedRepository.getInstance();
    private final MutableLiveData<List<FeedItem>> list = new MutableLiveData<>();

    public LiveData<List<FeedItem>> getList() { return list; }

    public void load(String userId) {
        repository.getFavorites(userId).observeForever(items -> list.setValue(items));
    }
}