package com.xda.one.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.xda.one.api.inteface.PrivateMessageClient;
import com.xda.one.api.retrofit.RetrofitPrivateMessageClient;
import com.xda.one.auth.XDAAccount;
import com.xda.one.event.message.MessageSendingFailedEvent;
import com.xda.one.event.message.MessageSentEvent;
import com.xda.one.util.AccountUtils;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

public class CreateMessageFragment extends DialogFragment implements TextWatcher {

    private static final int SELECT_EMOTICON_REQUEST_CODE = 101;

    private static final String USERNAME_ARGUMENT = "username";

    private final EventListener mEventListener = new EventListener();

    private EditText mMessageUsername;

    private EditText mMessageContent;

    private EditText mMessageTitle;

    private PrivateMessageClient mPrivateMessageClient;

    private ProgressDialog mDialog;

    private SelectEmoticonFragment mSelectEmoticonFragment;

    public static CreateMessageFragment createInstance(final String username) {
        final Bundle bundle = new Bundle();
        bundle.putString(USERNAME_ARGUMENT, username);

        final CreateMessageFragment fragment = new CreateMessageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public static CreateMessageFragment createInstance() {
        final Bundle bundle = new Bundle();

        final CreateMessageFragment fragment = new CreateMessageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDialog = new ProgressDialog(getActivity());
        mDialog.setTitle(R.string.creating_new_post);
        mDialog.setMessage(getString(R.string.creating_new_post));
        mDialog.setIndeterminate(true);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        mPrivateMessageClient = RetrofitPrivateMessageClient.getClient(getActivity());
        mPrivateMessageClient.getBus().register(mEventListener);

        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Light_Dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPrivateMessageClient.getBus().unregister(mEventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.create_message_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final XDAAccount selectedAccount = AccountUtils.getAccount(getActivity());

        getDialog().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);

        final ImageView imageView = (ImageView) view
                .findViewById(R.id.create_message_fragment_avatar);

        Picasso.with(getActivity())
                .load(selectedAccount.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(imageView);

        final TextView username = (TextView) view.findViewById(R.id
                .create_message_fragment_curr_username);
        username.setText(selectedAccount.getUserName());

        mMessageUsername = (EditText) view.findViewById(R.id.create_message_fragment_username);
        if (savedInstanceState == null) {
            mMessageUsername.setText(getArguments().getString(USERNAME_ARGUMENT));
        }

        mMessageTitle = (EditText) view.findViewById(R.id.create_message_fragment_title);
        mMessageContent = (EditText) view.findViewById(R.id.create_message_fragment_content);

        ImageView submit = (ImageView) view.findViewById(R.id.create_message_fragment_send);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean sendable = true;

                if (mMessageUsername.getText().length() == 0) {
                    mMessageUsername.setError("Please enter a username");
                    sendable = false;
                } else {
                    mMessageUsername.setError(null);
                }

                if (mMessageTitle.getText().length() == 0) {
                    mMessageTitle.setError("Please enter a title");
                    sendable = false;
                } else {
                    mMessageTitle.setError(null);
                }

                if (mMessageContent.getText().length() == 0) {
                    mMessageContent.setError("Please enter a message");
                    sendable = false;
                } else {
                    mMessageTitle.setError(null);
                }

                if (sendable) {
                    sendMessage();
                }
            }
        });

        mSelectEmoticonFragment = SelectEmoticonFragment.createInstance(mMessageContent);

        final ImageView insertEmoticon = (ImageView) view
                .findViewById(R.id.create_message_fragment_emoticon);
        insertEmoticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectEmoticonFragment.setTargetFragment(CreateMessageFragment.this,
                        SELECT_EMOTICON_REQUEST_CODE);
                mSelectEmoticonFragment.show(getFragmentManager(), "Select Emoticon");
            }
        });

        mMessageContent.addTextChangedListener(this);
    }

    private void sendMessage() {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMessageContent.getWindowToken(), 0);
        mPrivateMessageClient.sendMessageAsync(mMessageUsername.getText().toString(),
                mMessageTitle.getText().toString(), mMessageContent.getText().toString());
        mDialog.show();
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

    private class EventListener {

        @Subscribe
        public void onMessageSentSuccessful(final MessageSentEvent event) {
            mDialog.dismiss();
            Toast.makeText(getActivity(), R.string.new_message_send_successful,
                    Toast.LENGTH_LONG).show();
            dismiss();
        }

        @Subscribe
        public void onMessageSentSuccessful(final MessageSendingFailedEvent event) {
            mDialog.dismiss();
            Toast.makeText(getActivity(), R.string.new_message_send_failed,
                    Toast.LENGTH_LONG).show();
        }
    }
}