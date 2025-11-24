package com.example.moment_impressions.core.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public abstract class BaseActivity<VM extends BaseViewModel> extends AppCompatActivity {

    protected VM viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initViewModel();
        initView();
        initData();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void initData();

    private void initViewModel() {
        Class<VM> viewModelClass = getViewModelClass();
        if (viewModelClass != null) {
            viewModel = new ViewModelProvider(this).get(viewModelClass);
        }
    }

    protected abstract Class<VM> getViewModelClass();
}
