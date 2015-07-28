package com.xda.one.auth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.xda.one.R;

public class XDAAuthenticatorActivity extends FragmentActivity {

    private final String SCREEN_NAME = "XDAAuthenticatorActivity";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_activity);

        if (savedInstanceState == null) {
            final String accountName = getIntent().getStringExtra(LoginFragment.ARG_ACCOUNT_NAME);
            final Fragment instance = LoginFragment.createInstance(accountName);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_content,
                    instance).commit();
        }
    }
}