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
    private final MutableLiveData<java.util.List<String>> selectedImages = new MutableLiveData<>();

    public PublishViewModel() {
        repository = FeedRepository.getInstance();
    }

    public LiveData<Boolean> getIsPublishing() {
        return isPublishing;
    }

    public LiveData<Boolean> getPublishSuccess() {
        return publishSuccess;
    }

    public void setSelectedImages(java.util.List<String> uris) {
        selectedImages.setValue(uris);
    }

    public LiveData<java.util.List<String>> getSelectedImages() {
        return selectedImages;
    }

    public void publish(String title, String content, java.util.List<String> imageUris) {
        isPublishing.setValue(true);

        com.example.moment_impressions.data.model.User me =
                new com.example.moment_impressions.data.model.User(
                        "me", "Me", "https://api.dicebear.com/7.x/avataaars/png?seed=me");

        String cover = (imageUris != null && !imageUris.isEmpty()) ? imageUris.get(0) : "";
        com.example.moment_impressions.data.model.FeedItem item =
                new com.example.moment_impressions.data.model.FeedItem(
                        java.util.UUID.randomUUID().toString(), title, content, cover, me, 0, "Just now");

        item.setImages(imageUris);
        item.setHeight(400 + new java.util.Random().nextInt(200));

        repository.addFeedItem(item).observeForever(success -> {
            isPublishing.setValue(false);
            publishSuccess.setValue(success);
        });
    }
}
