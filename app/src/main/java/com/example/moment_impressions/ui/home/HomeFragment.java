package com.example.moment_impressions.ui.home;

import android.app.Activity;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private View layoutEmpty;
    private View layoutError;
    private View btnRetry;
    private FeedAdapter adapter;
    private ActivityResultLauncher<android.content.Intent> publishLauncher;

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
        layoutEmpty = view.findViewById(R.id.layout_empty);
        layoutError = view.findViewById(R.id.layout_error);
        btnRetry = view.findViewById(R.id.btn_retry);

        publishLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // 标记刷新状态，确保新发布的帖子在顶部可见
                refreshLayout.setRefreshing(true);
                viewModel.refresh();
            }
        });

        adapter = new FeedAdapter();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        // Prevent item reordering on scroll
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
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

        btnRetry.setOnClickListener(v -> viewModel.retry());

        view.findViewById(R.id.fab_add).setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getContext(),
                    com.example.moment_impressions.ui.publish.PublishActivity.class);
            androidx.core.app.ActivityOptionsCompat options = androidx.core.app.ActivityOptionsCompat
                    .makeCustomAnimation(requireContext(), android.R.anim.fade_in, android.R.anim.fade_out);
            publishLauncher.launch(intent, options);
        });

        view.findViewById(R.id.btn_profile).setOnClickListener(v -> {
            com.example.moment_impressions.ui.profile.ProfileFragment profileFragment =
                    new com.example.moment_impressions.ui.profile.ProfileFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, profileFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    protected void initData() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case LOADING:
                    if (adapter.getItemCount() == 0) {
                        refreshLayout.setRefreshing(true);
                    }
                    layoutEmpty.setVisibility(View.GONE);
                    layoutError.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
                case CONTENT:
                    refreshLayout.setRefreshing(false);
                    layoutEmpty.setVisibility(View.GONE);
                    layoutError.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
                case EMPTY:
                    refreshLayout.setRefreshing(false);
                    layoutEmpty.setVisibility(View.VISIBLE);
                    layoutError.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    break;
                case ERROR:
                    refreshLayout.setRefreshing(false);
                    layoutEmpty.setVisibility(View.GONE);
                    layoutError.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    break;
            }
        });

        viewModel.getFeedList().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                adapter.setItems(items);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            // If loading and we have content, it means we are loading more (or refreshing with content)
            // If refreshing, SwipeRefreshLayout handles it.
            // If loading more, we show footer.
            // How to distinguish? 
            // If refreshLayout.isRefreshing() is true, it's refresh.
            // But refreshLayout.setRefreshing is called in UiState observer.
            
            // Simple logic: If list is not empty, show footer when loading.
            if (adapter.getItemCount() > 0 && Boolean.TRUE.equals(isLoading) && !refreshLayout.isRefreshing()) {
                 adapter.setFooterVisible(true);
            } else {
                 adapter.setFooterVisible(false);
            }
        });

        // Initial load handled by ViewModel constructor but good to ensure
        // viewModel.refresh(); // Already in ViewModel constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list to update like status/counts when returning from DetailActivity
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
