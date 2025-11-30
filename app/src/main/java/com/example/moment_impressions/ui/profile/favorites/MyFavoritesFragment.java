package com.example.moment_impressions.ui.profile.favorites;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.base.BaseFragment;
import com.example.moment_impressions.ui.home.adapter.FeedAdapter;

public class MyFavoritesFragment extends BaseFragment<MyFavoritesViewModel> {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private FeedAdapter adapter;

    @Override
    protected int getLayoutId() { return R.layout.fragment_list_simple; }

    @Override
    protected Class<MyFavoritesViewModel> getViewModelClass() { return MyFavoritesViewModel.class; }

    @Override
    protected void initView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        adapter = new FeedAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(() -> viewModel.load("me"));
    }

    @Override
    protected void initData() {
        viewModel.getList().observe(getViewLifecycleOwner(), items -> {
            adapter.setItems(items);
            if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
        });
        viewModel.load("me");
    }
}