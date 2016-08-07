package com.xda.one.ui.helper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.XDALinerLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;

import com.xda.one.util.UIUtils;

public class QuickReturnHelper {

    private static final int ANIMATION_DURATION_MILLIS = 300;

    private static final int DEFAULT_ANIMATED_POSITION = 0;

    private final View mQuickReturnView;

    private final Toolbar mToolbar;

    private SparseArray<QuickReturnOnScroll> mScrollSparseArray = new SparseArray<>();

    private int mActionBarHeight;

    private int mPosition;

    private int mQuickReturnHeight;

    private boolean mHeaderVisible = true;

    private int mTouchSlop;

    public QuickReturnHelper(final Context context, final View quickReturnView,
                             final Toolbar toolbar) {
        mQuickReturnView = quickReturnView;
        mToolbar = toolbar;

        final ViewConfiguration vc = ViewConfiguration.get(toolbar.getContext());
        mTouchSlop = vc.getScaledTouchSlop();

        mActionBarHeight = UIUtils.calculateActionBarSize(context);

        if (quickReturnView == null) {
            return;
        }
        mHeaderVisible = mQuickReturnView.getVisibility() == View.VISIBLE;
        quickReturnView.post(new Runnable() {
            @Override
            public void run() {
                mQuickReturnHeight = mQuickReturnView.getHeight();
            }
        });
    }

    public static void postPaddingToQuickReturn(final View quickReturnView, final View content) {
        quickReturnView.post(new Runnable() {
            @Override
            public void run() {
                final int paddingLeft = content.getPaddingLeft();
                final int paddingTop = content.getPaddingTop();
                final int paddingRight = content.getPaddingRight();
                final int paddingBottom = content.getPaddingBottom();
                content.setPadding(paddingLeft,
                        UIUtils.calculateActionBarSize(content.getContext()) +
                                quickReturnView.getHeight() +
                                paddingTop, paddingRight, paddingBottom);
            }
        });
    }

    public void addOnScrollListener(final RecyclerView recyclerView, final int position) {
        final XDALinerLayoutManager linerLayoutManager = (XDALinerLayoutManager) recyclerView
                .getLayoutManager();
        final QuickReturnOnScroll onScroll = new QuickReturnOnScroll(position, linerLayoutManager);
        mScrollSparseArray.put(position, onScroll);
        recyclerView.addOnScrollListener(onScroll);
    }

    public void showTopBar() {
        if (mHeaderVisible || mQuickReturnView == null) {
            return;
        }
        animateToExpanding();
        mHeaderVisible = true;
    }

    public void hideTopBar() {
        if (!mHeaderVisible || mQuickReturnView == null) {
            return;
        }
        animateToOffScreen();
        mHeaderVisible = false;
    }

    private void animateToOffScreen() {
        animateView(mToolbar, -mActionBarHeight);
        animateView(mQuickReturnView, -mQuickReturnHeight - mActionBarHeight);

        mQuickReturnView.setVisibility(View.INVISIBLE);
    }

    private void animateToExpanding() {
        animateView(mToolbar, DEFAULT_ANIMATED_POSITION);
        animateView(mQuickReturnView, DEFAULT_ANIMATED_POSITION);

        mQuickReturnView.setVisibility(View.VISIBLE);
    }

    private void animateView(final View view, final int animationOffset) {
        view.animate()
                .setDuration(ANIMATION_DURATION_MILLIS)
                .translationY(animationOffset)
                .setInterpolator(new AccelerateInterpolator())
                .start();
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public void showToolbar() {
        animateView(mToolbar, DEFAULT_ANIMATED_POSITION);
    }

    private class QuickReturnOnScroll extends RecyclerView.OnScrollListener {

        private final int mFragmentPosition;

        private final XDALinerLayoutManager mLinerLayoutManager;

        private QuickReturnOnScroll(final int fragmentPosition,
                                    final XDALinerLayoutManager linerLayoutManager) {
            mFragmentPosition = fragmentPosition;
            mLinerLayoutManager = linerLayoutManager;
        }

        private void goingUp() {
            showTopBar();
        }

        private void goingDown() {
            hideTopBar();
        }

        @Override
        public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
            if (mPosition != mFragmentPosition) {
                return;
            }

            if (mLinerLayoutManager.isListEnd()) {
                showTopBar();
            } else if (dy - mTouchSlop > 0) {
                goingDown();
            } else if (dy + mTouchSlop < 0) {
                goingUp();
            }
        }
    }
}