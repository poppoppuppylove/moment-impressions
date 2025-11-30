package com.example.moment_impressions.ui.profile.posts;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.base.BaseFragment;
import com.example.moment_impressions.ui.home.adapter.FeedAdapter;

public class MyPostsFragment extends BaseFragment<MyPostsViewModel> {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private FeedAdapter adapter;

    @Override
    protected int getLayoutId() { return R.layout.fragment_list_simple; }

    @Override
    protected Class<MyPostsViewModel> getViewModelClass() { return MyPostsViewModel.class; }

    @Override
    protected void initView(@NonNull View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        refreshLayout = view.findViewById(R.id.swipe_refresh);
        adapter = new FeedAdapter();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(layoutManager);
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