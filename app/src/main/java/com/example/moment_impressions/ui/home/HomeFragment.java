package com.example.moment_impressions.ui.home;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.base.BaseFragment;
import com.example.moment_impressions.ui.home.adapter.FeedAdapter;

public class HomeFragment extends BaseFragment<HomeViewModel> {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private FeedAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected Class<HomeViewModel> getViewModelClass() {
        return HomeViewModel.class;
    }

    @Override
    protected void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        refreshLayout = view.findViewById(R.id.refresh_layout);

        adapter = new FeedAdapter();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        // Prevent item reordering on scroll
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).invalidateSpanAssignments();
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.loadMore();
                }
            }
        });

        refreshLayout.setOnRefreshListener(() -> viewModel.refresh());

        view.findViewById(R.id.fab_add).setOnClickListener(v -> {
            startActivity(new android.content.Intent(getContext(),
                    com.example.moment_impressions.ui.publish.PublishActivity.class));
        });
    }

    @Override
    protected void initData() {
        viewModel.getFeedList().observe(getViewLifecycleOwner(), items -> {
            if (refreshLayout.isRefreshing()) {
                adapter.setItems(items);
                refreshLayout.setRefreshing(false);
            } else {
                adapter.addItems(items);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (!isLoading && refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
        });

        // Initial load
        viewModel.refresh();
    }
}
