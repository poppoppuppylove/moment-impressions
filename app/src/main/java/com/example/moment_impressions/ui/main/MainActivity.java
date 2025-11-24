package com.example.moment_impressions.ui.main;

import android.os.Bundle;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.base.BaseActivity;

public class MainActivity extends BaseActivity<MainViewModel> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new com.example.moment_impressions.ui.home.HomeFragment())
                    .commit();
        }
    }

    @Override
    protected void initData() {
        // Initialize data here
    }

    @Override
    protected Class<MainViewModel> getViewModelClass() {
        return MainViewModel.class;
    }
}
