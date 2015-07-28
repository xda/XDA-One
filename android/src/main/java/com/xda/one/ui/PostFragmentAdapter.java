package com.xda.one.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.response.container.ResponsePostContainer;

public class PostFragmentAdapter extends FragmentStatePagerAdapter {

    private final int mCount;

    private ResponsePostContainer mContainerArgument;

    private UnifiedThread mUnifiedThread;

    public PostFragmentAdapter(final FragmentManager fm, final UnifiedThread unifiedThread,
                               final int count, final ResponsePostContainer containerArgument) {
        super(fm);

        mUnifiedThread = unifiedThread;
        mCount = count;
        mContainerArgument = containerArgument;
    }

    @Override
    public Fragment getItem(int position) {
        if (mContainerArgument != null && mContainerArgument.getCurrentPage() == position + 1) {
            return PostFragment.getInstance(mContainerArgument);
        }
        return PostFragment.getInstance(mUnifiedThread, position + 1);
    }

    public void setContainerArgument(final ResponsePostContainer containerArgument) {
        mContainerArgument = containerArgument;
    }

    @Override
    public int getCount() {
        return mCount;
    }
}
