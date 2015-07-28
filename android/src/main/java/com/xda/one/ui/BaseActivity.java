package com.xda.one.ui;

import android.support.v7.app.AppCompatActivity;

import com.xda.one.util.OneApplication;

public abstract class BaseActivity extends AppCompatActivity {

    public OneApplication getOneApplication() {
        return (OneApplication) super.getApplication();
    }
}
