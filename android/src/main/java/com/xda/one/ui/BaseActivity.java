package com.xda.one.ui;

import com.xda.one.R;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    public void onCreate(final Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);

        final ActionBar bar = getActionBar();
        bar.setIcon(R.drawable.logo);

        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return false;
    }
}
