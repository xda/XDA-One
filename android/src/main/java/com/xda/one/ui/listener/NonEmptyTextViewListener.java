package com.xda.one.ui.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class NonEmptyTextViewListener implements TextWatcher {

    private View mView;

    public NonEmptyTextViewListener(final View button) {
        mView = button;
    }

    @Override
    public void afterTextChanged(final Editable s) {
    }

    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count,
                                  final int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mView.setEnabled(s != null && s.length() != 0);
    }
}