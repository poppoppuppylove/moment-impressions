package com.example.moment_impressions.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.moment_impressions.core.base.BaseViewModel;
import com.example.moment_impressions.data.model.FeedItem;
import com.example.moment_impressions.data.repository.FeedRepository;
import java.util.List;

public class ProfileViewModel extends BaseViewModel {
    private final FeedRepository repository = FeedRepository.getInstance();
    private final MutableLiveData<Integer> totalLikes = new MutableLiveData<>(0);

    public LiveData<Integer> getTotalLikes() { return totalLikes; }

    public void computeTotalLikes(String userId) {
        repository.getMyPosts(userId).observeForever(items -> {
            int sum = 0;
            if (items != null) {
                for (FeedItem i : items) sum += i.getLikesCount();
            }
            totalLikes.setValue(sum);
        });
    }
}