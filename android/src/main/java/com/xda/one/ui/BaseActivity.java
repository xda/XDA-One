package com.xda.one.ui;

import com.xda.one.util.OneApplication;

import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public OneApplication getOneApplication() {
        return (OneApplication) super.getApplication();
    }
}
