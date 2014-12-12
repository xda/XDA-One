package com.xda.one.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.Toast;

import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.loader.ForumLoader;
import com.xda.one.model.misc.ForumType;
import com.xda.one.util.Utils;

import java.util.List;

public class AuthForumLoaderCallbacks
        implements LoaderManager.LoaderCallbacks<List<ResponseForum>> {

    private final Activity mActivity;

    private final XDAAccount mAccount;

    private final ProgressDialog mProgressDialog;

    public AuthForumLoaderCallbacks(final Activity activity, final XDAAccount account,
            final ProgressDialog progressDialog) {
        mActivity = activity;
        mAccount = account;
        mProgressDialog = progressDialog;
    }

    @Override
    public Loader<List<ResponseForum>> onCreateLoader(final int id, final Bundle args) {
        return new ForumLoader(mActivity, ForumType.ALL, null, true);
    }

    @Override
    public void onLoadFinished(final Loader<List<ResponseForum>> loader,
            final List<ResponseForum> data) {
        mProgressDialog.dismiss();
        if (Utils.isCollectionEmpty(data) || mAccount == null) {
            Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        finishLogin(mAccount);
    }

    @Override
    public void onLoaderReset(final Loader<List<ResponseForum>> loader) {
    }

    private void finishLogin(final XDAAccount account) {
        final Intent intent = new Intent();
        intent.putExtra("account", account);

        mActivity.setResult(Activity.RESULT_OK, intent);
        mActivity.finish();
    }
}
