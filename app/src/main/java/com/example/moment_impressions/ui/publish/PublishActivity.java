package com.example.moment_impressions.ui.publish;

import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.example.moment_impressions.R;
import com.example.moment_impressions.core.base.BaseActivity;
import com.example.moment_impressions.core.utils.ImageLoader;
import com.example.moment_impressions.core.utils.ToastUtils;

public class PublishActivity extends BaseActivity<PublishViewModel> {

    private ImageView ivClose;
    private Button btnPublish;
    private ImageView ivPreview;
    private EditText etTitle;
    private EditText etContent;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    ImageLoader.load(this, uri.toString(), ivPreview);
                }
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
        ivPreview = findViewById(R.id.iv_preview);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);

        ivClose.setOnClickListener(v -> finish());

        ivPreview.setOnClickListener(v -> pickImage.launch("image/*"));

        btnPublish.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (selectedImageUri == null) {
                ToastUtils.showShort(this, getString(R.string.publish_select_image));
                return;
            }
            if (title.isEmpty()) {
                ToastUtils.showShort(this, getString(R.string.publish_enter_title));
                return;
            }

            viewModel.publish(title, content, selectedImageUri.toString());
        });
    }

    @Override
    protected void initData() {
        viewModel.getIsPublishing().observe(this, isPublishing -> {
            btnPublish.setEnabled(!isPublishing);
            btnPublish
                    .setText(isPublishing ? getString(R.string.publish_publishing) : getString(R.string.publish_post));
        });

        viewModel.getPublishSuccess().observe(this, success -> {
            if (success) {
                ToastUtils.showShort(this, getString(R.string.publish_success));
                finish();
            } else {
                ToastUtils.showShort(this, getString(R.string.publish_fail));
            }
        });
    }
}
