package com.xda.one.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.inteface.PostClient;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.retrofit.RetrofitPostClient;
import com.xda.one.auth.XDAAccount;
import com.xda.one.event.post.PostCreatedEvent;
import com.xda.one.event.post.PostCreationFailedEvent;
import com.xda.one.model.augmented.AugmentedPost;
import com.xda.one.ui.listener.NonEmptyTextViewListener;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.PostUtils;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

public class CreatePostFragment extends DialogFragment {

    private static final String POST = "post";

    private static final String THREAD = "thread";

    private static final String DIALOG_SAVED_STATE = "dialog_saved_state";

    private final EventListener mEventListener = new EventListener();

    private AugmentedPost[] mPosts;

    private UnifiedThread mDefaultUnifiedThread;

    private EditText mMessageEditText;

    private PostClient mClient;

    private ProgressDialog mDialog;

    public static CreatePostFragment createInstance(final UnifiedThread defaultUnifiedThread,
                                                    final AugmentedPost[] post) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(THREAD, defaultUnifiedThread);
        bundle.putParcelableArray(POST, post);

        final CreatePostFragment fragment = new CreatePostFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static CreatePostFragment createInstance(final UnifiedThread defaultUnifiedThread) {
        return createInstance(defaultUnifiedThread, null);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialog = new ProgressDialog(getActivity());
        mDialog.setTitle(R.string.creating_new_post);
        mDialog.setMessage(getString(R.string.creating_new_post));
        mDialog.setIndeterminate(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        mClient = RetrofitPostClient.getClient(getActivity());
        mClient.getBus().register(mEventListener);

        // Set the dialog style
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Light_Dialog);

        // Setup arguments
        mPosts = (AugmentedPost[]) getArguments().getParcelableArray(POST);
        mDefaultUnifiedThread = getArguments().getParcelable(THREAD);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mClient.getBus().unregister(mEventListener);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_post_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final XDAAccount selectedAccount = AccountUtils.getAccount(getActivity());

        getDialog().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);

        final ImageView imageView = (ImageView) view
                .findViewById(R.id.create_post_dialog_fragment_avatar);
        Picasso.with(getActivity())
                .load(selectedAccount.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(imageView);

        final TextView header = (TextView) view.findViewById(R.id
                .create_post_fragment_header_title);
        header.setText(getString(R.string.replying_to, mDefaultUnifiedThread.getTitle()));

        final View sendButton = view.findViewById(R.id.create_post_dialog_fragment_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewPost();
            }
        });
        sendButton.setEnabled(false);

        mMessageEditText = (EditText) view.findViewById(R.id.create_post_dialog_fragment_message);
        mMessageEditText.addTextChangedListener(new NonEmptyTextViewListener(sendButton));

        if (mPosts != null && mPosts.length > 0) {
            final StringBuilder builder = new StringBuilder();
            final String first = PostUtils.quotePost(mPosts[0]);
            builder.append(first);
            for (int i = 1, length = mPosts.length; i < length; i++) {
                final String text = PostUtils.quotePost(mPosts[i]);
                builder.append("\n\n").append(text);
            }
            mMessageEditText.append(builder.toString());
            mMessageEditText.setSelection(first.length());
        }

        final TextView username = (TextView) view.findViewById(R.id
                .create_post_fragment_header_username);
        username.setText(selectedAccount.getUserName());

        final Fragment holder = this;

        final SelectEmoticonFragment mSelectEmoticonFragment = SelectEmoticonFragment
                .createInstance(mMessageEditText);

        ImageView insertEmoticon = (ImageView) view
                .findViewById(R.id.create_post_dialog_fragment_emoticon);
        insertEmoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectEmoticonFragment.setTargetFragment(holder, 101);
                mSelectEmoticonFragment.show(getFragmentManager(), "Select Emoticon");
            }
        });

        if (savedInstanceState != null && savedInstanceState.getBoolean(DIALOG_SAVED_STATE)) {
            mDialog.show();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(DIALOG_SAVED_STATE, mDialog.isShowing());
        mDialog.dismiss();
    }

    private void sendNewPost() {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);

        if (mPosts == null || mPosts.length == 0) {
            mClient.createNewPostAsync(mDefaultUnifiedThread,
                    mMessageEditText.getText().toString());
        } else {
            mClient.createNewPostAsync(mPosts[0], mMessageEditText.getText().toString());
        }
        mDialog.show();
    }

    private class EventListener {

        @Subscribe
        public void onPostCreationSuccess(final PostCreatedEvent event) {
            mDialog.dismiss();
            getTargetFragment().onActivityResult(PostPagerFragment.CREATE_POST_REQUEST_CODE,
                    Activity.RESULT_OK, null);
            dismiss();
        }

        @Subscribe
        public void onPostCreationFailure(final PostCreationFailedEvent event) {
            mDialog.dismiss();
            Toast.makeText(getActivity(), R.string.post_creation_failed, Toast.LENGTH_LONG).show();
        }
    }
}