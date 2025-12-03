package com.example.moment_impressions.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.moment_impressions.core.base.BaseViewModel;
import com.example.moment_impressions.data.model.FeedItem;
import com.example.moment_impressions.data.repository.FeedRepository;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends BaseViewModel {

    public enum UiState {
        LOADING,
        CONTENT,
        EMPTY,
        ERROR
    }

    private final FeedRepository repository;
    private final MutableLiveData<List<FeedItem>> feedList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<UiState> uiState = new MutableLiveData<>(UiState.LOADING);
    private final List<FeedItem> currentItems = new ArrayList<>();
    private int currentPage = 0;

    public HomeViewModel() {
        repository = FeedRepository.getInstance();
        refresh();
    }

    public LiveData<List<FeedItem>> getFeedList() {
        return feedList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<UiState> getUiState() {
        return uiState;
    }

    public void refresh() {
        currentPage = 0;
        isLoading.setValue(true);
        
        // 模拟网络错误概率 (10% chance of failure for demo purposes)
        // For better UX in demo, maybe we don't fail on first load unless requested,
        // but to show "Error Retry" capability, let's just assume it works or we can manually trigger error.
        // Here we stick to normal flow but handle empty/error states.
        
        loadData();
        
        // 并行触发网络刷新
        repository.refreshFromNetwork().observeForever(success -> {
            if (success != null && success) {
                // In a real app, we would merge or clear; here we just reload page 0
                loadData();
            } else {
                // If network fails and we have no data, show error
                if (currentItems.isEmpty()) {
                    uiState.setValue(UiState.ERROR);
                }
                isLoading.setValue(false);
            }
        });
    }

    public void loadMore() {
        if (Boolean.TRUE.equals(isLoading.getValue())) return;
        
        currentPage++;
        isLoading.setValue(true);
        
        repository.getFeedList(currentPage).observeForever(items -> {
            isLoading.setValue(false);
            if (items != null && !items.isEmpty()) {
                currentItems.addAll(items);
                feedList.setValue(new ArrayList<>(currentItems));
                uiState.setValue(UiState.CONTENT);
            } else {
                // No more data, do nothing or show footer "no more data"
            }
        });
    }

    public void retry() {
        uiState.setValue(UiState.LOADING);
        refresh();
    }

    private void loadData() {
        repository.getFeedList(currentPage).observeForever(items -> {
            isLoading.setValue(false);
            if (currentPage == 0) {
                currentItems.clear();
            }
            
            if (items != null && !items.isEmpty()) {
                currentItems.addAll(items);
                feedList.setValue(new ArrayList<>(currentItems));
                uiState.setValue(UiState.CONTENT);
            } else if (currentPage == 0) {
                // Only show empty state if first page is empty
                uiState.setValue(UiState.EMPTY);
            }
        });
    }
}
