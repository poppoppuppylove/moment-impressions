package com.example.moment_impressions.ui.profile;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.base.BaseFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.moment_impressions.core.utils.ImageLoader;

public class ProfileFragment extends BaseFragment<ProfileViewModel> {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvLikes;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    protected Class<ProfileViewModel> getViewModelClass() {
        return ProfileViewModel.class;
    }

    @Override
    protected void initView(@NonNull View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        ivAvatar = view.findViewById(R.id.avatar);
        tvNickname = view.findViewById(R.id.nickname);
        tvLikes = view.findViewById(R.id.likes);

        viewPager.setAdapter(new androidx.viewpager2.adapter.FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) return new com.example.moment_impressions.ui.profile.posts.MyPostsFragment();
                return new com.example.moment_impressions.ui.profile.favorites.MyFavoritesFragment();
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "我的帖子" : "我的收藏");
        }).attach();
    }

    @Override
    protected void initData() {
        // 绑定用户信息与获赞数
        tvNickname.setText("Me");
        String avatarUrl = "https://api.dicebear.com/7.x/avataaars/png?seed=me";
        ImageLoader.loadRounded(requireContext(), avatarUrl, ivAvatar, 16);

        viewModel.getTotalLikes().observe(getViewLifecycleOwner(), likes -> {
            tvLikes.setText("获赞 " + (likes == null ? 0 : likes));
        });
        viewModel.computeTotalLikes("me");
    }
}