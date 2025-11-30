package com.example.moment_impressions.ui.detail;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.base.BaseActivity;
import com.example.moment_impressions.core.utils.ImageLoader;
import com.example.moment_impressions.ui.detail.adapter.CommentAdapter;

public class DetailActivity extends BaseActivity<DetailViewModel> {

    public static final String EXTRA_FEED_ID = "extra_feed_id";
    public static final String EXTRA_IMAGE_URL = "extra_image_url";
    public static final String EXTRA_IMAGES = "extra_images";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_CONTENT = "extra_content";
    public static final String EXTRA_TIME = "extra_time";
    public static final String EXTRA_AUTHOR_NAME = "extra_author_name";
    public static final String EXTRA_AUTHOR_AVATAR = "extra_author_avatar";

    private androidx.viewpager2.widget.ViewPager2 viewPagerImages;
    private TextView tvImageIndicator;
    private TextView tvDetailTitle;
    private TextView tvDetailContent;
    private TextView tvDetailTime;
    private ImageView ivAuthorAvatar;
    private TextView tvAuthorNickname;
    private ImageView ivLike;
    private ImageView ivCollect;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private Toolbar toolbar;

    private boolean isLiked = false;
    private boolean isCollected = false;

    private EditText etComment;
    private ImageView ivSendComment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected Class<DetailViewModel> getViewModelClass() {
        return DetailViewModel.class;
    }

    @Override
    protected void initView() {
        viewPagerImages = findViewById(R.id.view_pager_images);
        tvImageIndicator = findViewById(R.id.tv_image_indicator);
        tvDetailTitle = findViewById(R.id.tv_detail_title);
        tvDetailContent = findViewById(R.id.tv_detail_content);
        tvDetailTime = findViewById(R.id.tv_detail_time);
        ivAuthorAvatar = findViewById(R.id.iv_author_avatar);
        tvAuthorNickname = findViewById(R.id.tv_author_nickname);
        ivLike = findViewById(R.id.iv_like);
        ivCollect = findViewById(R.id.iv_collect);
        recyclerViewComments = findViewById(R.id.recycler_view_comments);
        etComment = findViewById(R.id.et_comment);
        ivSendComment = findViewById(R.id.iv_send_comment);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        commentAdapter = new CommentAdapter();
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setAdapter(commentAdapter);

        ivLike.setOnClickListener(v -> toggleLike());
        ivCollect.setOnClickListener(v -> toggleCollect());

        etComment.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivSendComment.setVisibility(s.length() > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        ivSendComment.setOnClickListener(v -> sendComment());
    }

    private void sendComment() {
        String content = etComment.getText().toString().trim();
        if (!content.isEmpty()) {
            String feedId = getIntent().getStringExtra(EXTRA_FEED_ID);
            if (feedId != null) {
                viewModel.addComment(feedId, content);
                etComment.setText("");
                hideKeyboard();
                com.example.moment_impressions.core.utils.ToastUtils.showShort(this, "Comment sent");
            }
        }
    }

    private void hideKeyboard() {
        android.view.View view = this.getCurrentFocus();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(
                    android.content.Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void initData() {
        String feedId = getIntent().getStringExtra(EXTRA_FEED_ID);
        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        java.util.ArrayList<String> images = getIntent().getStringArrayListExtra(EXTRA_IMAGES);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String content = getIntent().getStringExtra(EXTRA_CONTENT);
        String time = getIntent().getStringExtra(EXTRA_TIME);
        String authorName = getIntent().getStringExtra(EXTRA_AUTHOR_NAME);
        String authorAvatar = getIntent().getStringExtra(EXTRA_AUTHOR_AVATAR);

        com.example.moment_impressions.ui.detail.adapter.ImagePagerAdapter imageAdapter = new com.example.moment_impressions.ui.detail.adapter.ImagePagerAdapter();
        viewPagerImages.setAdapter(imageAdapter);

        if (images != null && !images.isEmpty()) {
            imageAdapter.setItems(images);
            updateIndicator(1, images.size());
        } else if (imageUrl != null) {
            java.util.List<String> singleList = new java.util.ArrayList<>();
            singleList.add(imageUrl);
            imageAdapter.setItems(singleList);
            updateIndicator(1, 1);
        }

        viewPagerImages.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicator(position + 1, imageAdapter.getItemCount());
            }
        });

        if (title != null) {
            tvDetailTitle.setText(title);
        }
        if (content != null) {
            tvDetailContent.setText(content);
        }
        if (time != null) {
            tvDetailTime.setText(time);
        }
        if (authorName != null) {
            tvAuthorNickname.setText(authorName);
        }
        if (authorAvatar != null && !authorAvatar.isEmpty()) {
            ImageLoader.loadRounded(this, authorAvatar, ivAuthorAvatar, 16);
        }

        if (feedId != null) {
            viewModel.loadComments(feedId);
        }

        viewModel.getCommentList().observe(this, comments -> {
            commentAdapter.setItems(comments);
        });
    }

    private void updateIndicator(int current, int total) {
        tvImageIndicator.setText(current + "/" + total);
    }

    private void toggleLike() {
        isLiked = !isLiked;
        if (isLiked) {
            ivLike.setImageResource(android.R.drawable.btn_star_big_on);
            ivLike.setColorFilter(getResources().getColor(android.R.color.holo_orange_light));
        } else {
            ivLike.setImageResource(android.R.drawable.btn_star);
            ivLike.setColorFilter(getResources().getColor(android.R.color.black));
        }

        String feedId = getIntent().getStringExtra(EXTRA_FEED_ID);
        if (feedId != null) {
            viewModel.toggleLike(feedId, isLiked);
        }
    }

    private void toggleCollect() {
        isCollected = !isCollected;
        if (isCollected) {
            ivCollect.setColorFilter(getResources().getColor(android.R.color.holo_blue_light));
        } else {
            ivCollect.setColorFilter(getResources().getColor(android.R.color.black));
        }

        String feedId = getIntent().getStringExtra(EXTRA_FEED_ID);
        if (feedId != null) {
            viewModel.toggleFavorite(feedId, isCollected);
            com.example.moment_impressions.core.utils.ToastUtils.showShort(this,
                    isCollected ? "已收藏" : "已取消收藏");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
