package com.xda.one.ui.helper;

import com.xda.one.R;
import com.xda.one.api.inteface.PostClient;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.api.retrofit.RetrofitPostClient;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.util.FragmentUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.util.ArrayList;

public class ThreadUnreadPostHelper extends CancellableCallbackHelper<ResponsePostContainer> {

    private final Context mContext;

    private final FragmentManager mFragmentManager;

    private final AugmentedUnifiedThread mUnifiedThread;

    private AlertDialog mDialog;

    public ThreadUnreadPostHelper(final Context context, final FragmentManager fragmentManager,
            final AugmentedUnifiedThread unifiedThread, final AlertDialog dialog) {
        super(dialog);

        mContext = context;
        mFragmentManager = fragmentManager;
        mUnifiedThread = unifiedThread;
        mDialog = dialog;
    }

    public void start() {
        final PostClient client = RetrofitPostClient.getClient(mContext);
        client.getUnreadPostFeed(mUnifiedThread, this, this);
    }

    @Override
    public void run() {
        Toast.makeText(mDialog.getContext(), R.string.something_went_wrong_request,
                Toast.LENGTH_LONG).show();
        mDialog.dismiss();
    }

    @Override
    public void safeCallback(final ResponsePostContainer data) {
        mDialog.dismiss();

        final Fragment fragment = FragmentUtils
                .switchToPostList(mUnifiedThread, new ArrayList<String>(),
                        data);

        final FragmentTransaction transaction = FragmentUtils
                .getDefaultTransaction(mFragmentManager);
        transaction.addToBackStack(mUnifiedThread.getTitle());
        transaction.replace(R.id.content_frame, fragment).commit();
    }
}