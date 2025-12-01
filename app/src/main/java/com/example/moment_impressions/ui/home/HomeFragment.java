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
        viewModel.getFeedList().observe(getViewLifecycleOwner(), items -> {
            if (items == null) return;
            
            // If it's a refresh (or initial load), items will contain the full list or first page.
            // Since ViewModel now manages the full list for pagination, we just set it.
            // However, Adapter.setItems triggers a full refresh.
            // Adapter.addItems triggers range insert.
            // We need to know if we should replace or append.
            // But since ViewModel now emits the WHOLE list (in my change above), 
            // we should probably just use setItems or DiffUtil.
            // For simplicity, let's use setItems for now.
            
            adapter.setItems(items);
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
                recyclerView.scrollToPosition(0);
            }
        });

        // Initial load
        viewModel.refresh();
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
