package com.xda.one.ui;

import com.xda.one.util.OneApplication;

import android.support.v7.app.ActionBarActivity;

public abstract class BaseActivity extends ActionBarActivity {

    public OneApplication getOneApplication() {
        return (OneApplication) super.getApplication();
    }
}
