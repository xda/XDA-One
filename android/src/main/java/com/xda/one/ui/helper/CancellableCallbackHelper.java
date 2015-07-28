package com.xda.one.ui.helper;

import android.app.Dialog;
import android.content.DialogInterface;

import com.xda.one.api.misc.Consumer;

public abstract class CancellableCallbackHelper<T> implements Consumer<T>, Runnable,
        DialogInterface.OnCancelListener {

    private boolean mCancel;

    public CancellableCallbackHelper(final Dialog dialog) {
        dialog.setOnCancelListener(this);
    }

    @Override
    public final void run(final T data) {
        if (!mCancel) {
            safeCallback(data);
        }
    }

    @Override
    public void onCancel(final DialogInterface dialog) {
        mCancel = true;
    }

    public abstract void safeCallback(final T data);
}