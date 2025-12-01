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
        // 先本地/模拟加载一页，提高首屏速度
        loadData();
        // 并行触发网络刷新，成功后再加载一次以合并网络数据
        repository.refreshFromNetwork().observeForever(success -> {
            if (success != null && success) {
                loadData();
            }
        });
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
                // If refreshing (page 0), replace list. If loading more, append.
                if (currentPage == 0) {
                     feedList.setValue(items);
                } else {
                     List<FeedItem> current = feedList.getValue();
                     if (current != null) {
                         // Avoid simple addAll to prevent duplicates if Repository returns overlap
                         // For now, just append.
                         current.addAll(items);
                         feedList.setValue(current);
                     } else {
                         feedList.setValue(items);
                     }
                }
            }
        });
    }
}
