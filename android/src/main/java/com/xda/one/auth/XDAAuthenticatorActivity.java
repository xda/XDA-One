package com.xda.one.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.com.xda.one.googleplus.GPlusImpl;
import com.com.xda.one.googleplus.GPlusLoginClass;
import com.com.xda.one.googleplus.ITokenEventCallback;
import com.com.xda.one.googleplus.OnIntentResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.xda.one.R;

public class XDAAuthenticatorActivity extends FragmentActivity implements ResultCallback<People.LoadPeopleResult> {

    private final String SCREEN_NAME = "XDAAuthenticatorActivity";
    public OnIntentResult mUpdateResult;
    ITokenEventCallback iTokenEventCallback;

    public static XDAAuthenticatorActivity xdaAuthenticatorActivity;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_activity);
        xdaAuthenticatorActivity = this;
        if (savedInstanceState == null) {
            final String accountName = getIntent().getStringExtra(LoginFragment.ARG_ACCOUNT_NAME);
            final Fragment instance = LoginFragment.createInstance(accountName);
            if (instance instanceof ITokenEventCallback)
                iTokenEventCallback = (ITokenEventCallback) instance;
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_activity_content,
                    instance).commit();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.e("XDA-One", "");

        if (requestCode == GPlusImpl.REQUEST_CODE_RESOLVE_ERR
                && resultCode == RESULT_OK) {
            Log.e("On activity result", "inside on activity result");
            if (iTokenEventCallback != null)
                new GPlusLoginClass(this, iTokenEventCallback).loginGPlus();
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                for (int i = 0; i < count; i++) {
                    Log.d("TAG", "Display name: " + personBuffer.get(i).getDisplayName());
                }
            } finally {
                personBuffer.close();
            }
        } else {
            Log.e("TAG", "Error requesting visible circles: " + peopleData.getStatus());
        }
    }
}