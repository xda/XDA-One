package com.xda.one.ui.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class MultipleNonEmptyTextViewListener implements TextWatcher {

    private final EditText[] mEditTexts;

    private View mView;

    public MultipleNonEmptyTextViewListener(final View button, final EditText... editTexts) {
        if (editTexts == null) {
            throw new NullPointerException();
        }

        mView = button;
        mEditTexts = editTexts;
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
        for (final EditText editText : mEditTexts) {
            if (editText.length() == 0) {
                mView.setEnabled(false);
                return;
            }
        }
        mView.setEnabled(true);
    }

    public void registerAll() {
        for (final EditText editText : mEditTexts) {
            editText.addTextChangedListener(this);
        }
    }
}