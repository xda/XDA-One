package com.xda.one.ui.helper;

import com.xda.one.util.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ActionModeHelper implements View.OnClickListener, View.OnLongClickListener,
        ActionMode.Callback {

    public static final String ACTIVATED_POSITIONS = "ACTIVATED_POSITIONS";

    private final View.OnClickListener mClickListener;

    private final RecyclerViewActionModeCallback mActionModeCallback;

    private final Set<Integer> mActivatedPositions;

    private final Activity mActivity;

    private final SelectionMode mSelectionMode;

    private RecyclerView mRecyclerView;

    private ActionMode mActionMode;

    public ActionModeHelper(final Activity activity,
            final RecyclerViewActionModeCallback callback, final View.OnClickListener listener,
            final SelectionMode selectionMode) {
        mActivity = activity;
        mActionModeCallback = callback;
        mClickListener = listener;
        mSelectionMode = selectionMode;
        mActivatedPositions = new HashSet<>();
    }

    @Override
    public void onClick(final View view) {
        if (mActionMode != null) {
            toggleViewActivatedState(view);
        } else if (mClickListener != null) {
            mClickListener.onClick(view);
        }
    }

    @Override
    public boolean onLongClick(final View view) {
        if (mActionMode == null) {
            mActionMode = mActivity.startActionMode(this);
        }
        toggleViewActivatedState(view);
        return true;
    }

    private void toggleViewActivatedState(final View view) {
        final int position = mRecyclerView.getChildPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }

        if (mSelectionMode == SelectionMode.SINGLE) {
            final boolean checked = mActivatedPositions.contains(position);
            view.setActivated(!checked);
            mRecyclerView.getAdapter().notifyItemChanged(position);

            if (mActivatedPositions.size() > 0) {
                final int previous = mActivatedPositions.iterator().next();
                mActivatedPositions.remove(previous);
                mRecyclerView.getAdapter().notifyItemChanged(previous);
            }

            if (checked) {
                mActivatedPositions.remove(position);
            } else {
                mActivatedPositions.add(position);
            }
            onCheckedStateChanged(mActionMode, position, !checked);

            if (mActivatedPositions.isEmpty()) {
                finish();
            }
        } else if (mSelectionMode == SelectionMode.MULTIPLE) {
            final boolean checked = mActivatedPositions.contains(position);
            view.setActivated(!checked);
            mRecyclerView.getAdapter().notifyItemChanged(position);

            if (checked) {
                mActivatedPositions.remove(position);
            } else {
                mActivatedPositions.add(position);
            }
            onCheckedStateChanged(mActionMode, position, !checked);

            if (mActivatedPositions.isEmpty()) {
                finish();
            }
        }
    }

    public void updateActivatedState(final View view, final int position) {
        view.setActivated(mActivatedPositions.contains(position));
    }

    @Override
    public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
        return mActionModeCallback != null && mActionModeCallback.onCreateActionMode(actionMode,
                menu);
    }

    @Override
    public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
        return mActionModeCallback != null && mActionModeCallback.onPrepareActionMode(actionMode,
                menu);
    }

    @Override
    public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
        return mActionModeCallback != null && mActionModeCallback.onActionItemClicked(actionMode,
                menuItem);
    }

    public void onCheckedStateChanged(final ActionMode actionMode, int position,
            boolean isNowChecked) {
        if (mActionModeCallback != null) {
            mActionModeCallback.onCheckedStateChanged(actionMode, position, isNowChecked);
        }
    }

    public void saveInstanceState(Bundle outBundle) {
        // TODO: support non-stable IDs by persisting positions instead of IDs
        if (mActionMode != null) {
            outBundle.putIntegerArrayList(ACTIVATED_POSITIONS,
                    new ArrayList<>(mActivatedPositions));
        }
    }

    public void restoreInstanceState(final Bundle bundle) {
        if (bundle == null) {
            return;
        }
        final List<Integer> items = bundle.getIntegerArrayList(ACTIVATED_POSITIONS);
        if (Utils.isCollectionEmpty(items)) {
            return;
        }

        mActivatedPositions.addAll(items);
        mActionMode = mActivity.startActionMode(this);
        notifyActivatedItemsChanged();
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mActionMode = null;

        notifyActivatedItemsChanged();
        mActivatedPositions.clear();
    }

    private void notifyActivatedItemsChanged() {
        if (mRecyclerView == null || mRecyclerView.getAdapter() == null) {
            return;
        }

        for (final Integer integer : mActivatedPositions) {
            mRecyclerView.getAdapter().notifyItemChanged(integer);
        }
    }

    public List<Integer> getCheckedPositions() {
        return new ArrayList<>(mActivatedPositions);
    }

    public int getCheckedItemCount() {
        return mActivatedPositions.size();
    }

    public void addViewToActionMode(final View view) {
        if (mActionMode == null) {
            mActionMode = mActivity.startActionMode(this);
        }
        final int position = mRecyclerView.getChildPosition(view);
        if (!mActivatedPositions.contains(position)) {
            toggleViewActivatedState(view);
        }
    }

    public void finish() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        mActionMode = null;
    }

    public boolean isActionModeStarted() {
        return mActionMode != null;
    }

    public static enum SelectionMode {
        SINGLE, MULTIPLE
    }

    public static class RecyclerViewActionModeCallback {

        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            return true;
        }

        public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
            return true;
        }

        public void onCheckedStateChanged(final ActionMode actionMode, int position,
                boolean isNowChecked) {
        }
    }
}