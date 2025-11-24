package com.example.moment_impressions.ui.publish;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.moment_impressions.core.base.BaseViewModel;
import com.example.moment_impressions.data.model.FeedItem;
import com.example.moment_impressions.data.model.User;
import com.example.moment_impressions.data.repository.FeedRepository;
import java.util.UUID;

public class PublishViewModel extends BaseViewModel {

    private final FeedRepository repository;
    private final MutableLiveData<Boolean> isPublishing = new MutableLiveData<>();
    private final MutableLiveData<Boolean> publishSuccess = new MutableLiveData<>();

    public PublishViewModel() {
        repository = FeedRepository.getInstance();
    }

    public LiveData<Boolean> getIsPublishing() {
        return isPublishing;
    }

    public LiveData<Boolean> getPublishSuccess() {
        return publishSuccess;
    }

    public void publish(String title, String content, String imageUri) {
        isPublishing.setValue(true);

        // Mock User
        User me = new User("me", "Me", "https://api.dicebear.com/7.x/avataaars/png?seed=me");

        // Create FeedItem
        FeedItem item = new FeedItem(UUID.randomUUID().toString(), title, content, imageUri, me, 0, "Just now");
        item.setHeight(600); // Default height

        repository.addFeedItem(item).observeForever(success -> {
            isPublishing.setValue(false);
            publishSuccess.setValue(success);
        });
    }
}
