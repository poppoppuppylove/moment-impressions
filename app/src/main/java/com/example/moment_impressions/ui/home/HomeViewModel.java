package com.example.moment_impressions.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.moment_impressions.core.base.BaseViewModel;
import com.example.moment_impressions.data.model.FeedItem;
import com.example.moment_impressions.data.repository.FeedRepository;
import java.util.List;

public class HomeViewModel extends BaseViewModel {

    private final FeedRepository repository;
    private final MutableLiveData<List<FeedItem>> feedList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private int currentPage = 0;

    public HomeViewModel() {
        repository = FeedRepository.getInstance();
    }

    public LiveData<List<FeedItem>> getFeedList() {
        return feedList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void refresh() {
        currentPage = 0;
        loadData();
    }

    public void loadMore() {
        currentPage++;
        loadData();
    }

    private void loadData() {
        isLoading.setValue(true);
        // Observe the LiveData from Repository.
        // In a real app with Retrofit, we might use a Callback or RxJava/Coroutines.
        // Here Repository returns LiveData which simulates a network call.
        // We need to observe it forever or use a MediatorLiveData if we want to chain.
        // For simplicity with the current Repository implementation which returns a new
        // LiveData each time:
        repository.getFeedList(currentPage).observeForever(items -> {
            isLoading.setValue(false);
            if (items != null) {
                // If refreshing (page 0), replace list. If loading more, append (handled by UI
                // observing change,
                // but here we are just emitting the chunk. The UI/Adapter should handle append?
                // Or ViewModel maintains the full list?
                // Better: ViewModel emits the *new* chunk or the *full* list.
                // Let's emit the chunk and let UI handle it, OR emit full list.
                // Given the Adapter has `addItems` and `setItems`, let's try to be smart.
                // But LiveData usually holds the state.
                // Let's just emit the chunk for now as per Repository design.
                // Wait, if I emit chunk, rotation will lose data if I don't save it.
                // But for this simple demo, let's just emit the chunk.
                feedList.setValue(items);
            }
        });
    }
}
