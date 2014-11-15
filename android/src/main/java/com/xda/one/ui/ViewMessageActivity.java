package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.model.augmented.AugmentedMessage;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.WindowManager;

public class ViewMessageActivity extends BaseActivity {

    private static final String MESSAGE_ARGUMENT = "message";

    private ViewMessageFragment mMessageFragment;

    private final String SCREEN_NAME = "ViewMessageActivity";

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.frame_activity);

        if (bundle == null) {
            final AugmentedMessage message = getIntent().getParcelableExtra(MESSAGE_ARGUMENT);
            mMessageFragment = ViewMessageFragment.createInstance(message);

            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_activity_content, mMessageFragment).commit();
        } else {
            mMessageFragment = (ViewMessageFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.frame_activity_content);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


    }

    @Override
    public void onBackPressed() {
        mMessageFragment.onBackPressed();
        super.onBackPressed();
    }

}
