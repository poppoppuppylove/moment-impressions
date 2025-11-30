package com.example.moment_impressions.ui.publish;

import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.base.BaseActivity;
import com.example.moment_impressions.core.utils.ImageLoader;
import com.example.moment_impressions.core.utils.ToastUtils;

public class PublishActivity extends BaseActivity<PublishViewModel> {

    private ImageView ivClose;
    private Button btnPublish;
    private androidx.recyclerview.widget.RecyclerView rvPreview;
    private EditText etTitle;
    private EditText etContent;
    private java.util.List<Uri> selectedImageUris = new java.util.ArrayList<>();
    private PublishImageAdapter previewAdapter;

    private final ActivityResultLauncher<String> pickImages = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(), uris -> {
                selectedImageUris.clear();
                if (uris != null && !uris.isEmpty()) {
                    selectedImageUris.addAll(uris);
                    java.util.List<String> imageStrings = new java.util.ArrayList<>();
                    for (Uri u : uris) imageStrings.add(u.toString());
                    previewAdapter.setItems(imageStrings);
                } else {
                    previewAdapter.setItems(java.util.Collections.emptyList());
                }
                updatePublishButtonState();
            });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_publish;
    }

    @Override
    protected Class<PublishViewModel> getViewModelClass() {
        return PublishViewModel.class;
    }

    @Override
    protected void initView() {
        ivClose = findViewById(R.id.iv_close);
        btnPublish = findViewById(R.id.btn_publish);
        rvPreview = findViewById(R.id.rv_preview);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);

        ivClose.setOnClickListener(v -> finish());

        previewAdapter = new PublishImageAdapter();
        rvPreview.setLayoutManager(new GridLayoutManager(this, 3));
        rvPreview.setAdapter(previewAdapter);
        rvPreview.setOnClickListener(v -> pickImages.launch("image/*"));

        etTitle.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePublishButtonState();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        btnPublish.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (!btnPublish.isEnabled()) {
                // 双保险：按钮未激活时给予提示
                if (selectedImageUris == null || selectedImageUris.isEmpty()) {
                    ToastUtils.showShort(this, getString(R.string.publish_select_image));
                } else {
                    ToastUtils.showShort(this, getString(R.string.publish_enter_title));
                }
                return;
            }
            java.util.List<String> imageUris = new java.util.ArrayList<>();
            for (Uri uri : selectedImageUris) {
                imageUris.add(uri.toString());
            }
            viewModel.setSelectedImages(imageUris);
            viewModel.publish(title, content, imageUris);
        });
    }

    @Override
    protected void initData() {
        viewModel.getIsPublishing().observe(this, isPublishing -> {
            btnPublish.setText(isPublishing ? getString(R.string.publish_publishing) : getString(R.string.publish_post));
            updatePublishButtonState();
        });

        viewModel.getPublishSuccess().observe(this, success -> {
            if (success) {
                ToastUtils.showShort(this, getString(R.string.publish_success));
                setResult(android.app.Activity.RESULT_OK);
                finish();
            } else {
                ToastUtils.showShort(this, getString(R.string.publish_fail));
            }
        });

        updatePublishButtonState();
    }

    private void updatePublishButtonState() {
        boolean hasImages = selectedImageUris != null && !selectedImageUris.isEmpty();
        boolean hasTitle = etTitle.getText() != null && !etTitle.getText().toString().trim().isEmpty();
        boolean isPublishing = viewModel.getIsPublishing().getValue() != null && viewModel.getIsPublishing().getValue();
        btnPublish.setEnabled(hasImages && hasTitle && !isPublishing);
    }
}
