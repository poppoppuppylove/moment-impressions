package com.example.moment_impressions.ui.detail;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
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
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_CONTENT = "extra_content";
    public static final String EXTRA_TIME = "extra_time";

    private ImageView ivDetailImage;
    private TextView tvDetailTitle;
    private TextView tvDetailContent;
    private TextView tvDetailTime;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private Toolbar toolbar;

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
        ivDetailImage = findViewById(R.id.iv_detail_image);
        tvDetailTitle = findViewById(R.id.tv_detail_title);
        tvDetailContent = findViewById(R.id.tv_detail_content);
        tvDetailTime = findViewById(R.id.tv_detail_time);
        recyclerViewComments = findViewById(R.id.recycler_view_comments);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        commentAdapter = new CommentAdapter();
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComments.setAdapter(commentAdapter);
    }

    @Override
    protected void initData() {
        String feedId = getIntent().getStringExtra(EXTRA_FEED_ID);
        String imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String content = getIntent().getStringExtra(EXTRA_CONTENT);
        String time = getIntent().getStringExtra(EXTRA_TIME);

        if (imageUrl != null) {
            ImageLoader.load(this, imageUrl, ivDetailImage);
        }
        if (title != null) {
            tvDetailTitle.setText(title);
        }
        if (content != null) {
            tvDetailContent.setText(content);
        }
        if (time != null) {
            tvDetailTime.setText(time);
        }

        if (feedId != null) {
            viewModel.loadComments(feedId);
        }

        viewModel.getCommentList().observe(this, comments -> {
            commentAdapter.setItems(comments);
        });
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
