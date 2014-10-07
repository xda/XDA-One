package com.xda.one.ui;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.Result;
import com.xda.one.api.retrofit.RetrofitThreadClient;
import com.xda.one.auth.XDAAccount;
import com.xda.one.util.AccountUtils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

public class CreateThreadFragment extends DialogFragment implements TextWatcher {

    private EditText mPostTitle;

    private EditText mPostContent;

    private int mForumId;

    private ThreadClient mClient;

    public static CreateThreadFragment createInstance(final int forumId) {
        final Bundle bundle = new Bundle();
        bundle.putInt(ThreadFragment.FORUM_ID_ARGUMENT, forumId);

        final CreateThreadFragment fragment = new CreateThreadFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mForumId = getArguments().getInt(ThreadFragment.FORUM_ID_ARGUMENT, 0);
        mClient = RetrofitThreadClient.getClient(getActivity());

        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_thread_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final XDAAccount account = AccountUtils.getAccount(getActivity());
        getDialog().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);

        final ImageView imageView = (ImageView) view
                .findViewById(R.id.create_thread_fragment_avatar);

        Picasso.with(getActivity())
                .load(account.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(imageView);

        final TextView username = (TextView) view.findViewById(R.id
                .create_message_thread_curr_username);
        username.setText(account.getUserName());

        mPostTitle = (EditText) view.findViewById(R.id.create_thread_fragment_title);
        mPostContent = (EditText) view.findViewById(R.id.create_thread_fragment_content);

        final ImageView submit = (ImageView) view.findViewById(R.id.create_thread_fragment_send);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean creatable = true;

                if (mPostContent.getText().length() == 0) {
                    mPostContent.setError("Please enter a message");
                    creatable = false;
                } else {
                    mPostContent.setError(null);
                }

                if (mPostTitle.getText().length() == 0) {
                    mPostTitle.setError("Please enter a title");
                    creatable = false;
                } else {
                    mPostTitle.setError(null);
                }

                if (creatable) {
                    createNewThread();
                }
            }
        });

        final SelectEmoticonFragment mSelectEmoticonFragment = SelectEmoticonFragment
                .createInstance(mPostContent);

        final ImageView insertEmoticon = (ImageView) view
                .findViewById(R.id.create_thread_fragment_emoticon);
        insertEmoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectEmoticonFragment.setTargetFragment(CreateThreadFragment.this, 101);
                mSelectEmoticonFragment.show(getFragmentManager(), "Select Emoticon");
            }
        });

        mPostContent.addTextChangedListener(this);
    }


    void createNewThread() {
        if (!AccountUtils.isAccountAvailable(getActivity())) {
            Toast.makeText(getActivity(), "You are not logged in", Toast.LENGTH_LONG).show();
            dismiss();
        }
        final String postTitle = mPostTitle.getText().toString();
        final String message = mPostContent.getText().toString();

        mClient.createThread(mForumId, postTitle, message, new Consumer<Result>() {
            @Override
            public void run(Result result) {
                Toast.makeText(getActivity(), "Post has been created", Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence sequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence sequence, int i, int i2, int i3) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        getActivity().supportInvalidateOptionsMenu();
    }
}