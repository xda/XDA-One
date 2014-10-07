package com.xda.one.ui;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.inteface.PrivateMessageClient;
import com.xda.one.api.retrofit.RetrofitPrivateMessageClient;
import com.xda.one.auth.XDAAccount;
import com.xda.one.event.message.MessageSendingFailedEvent;
import com.xda.one.event.message.MessageSentEvent;
import com.xda.one.model.augmented.AugmentedMessage;
import com.xda.one.ui.listener.NonEmptyTextViewListener;
import com.xda.one.util.AccountUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

public class ReplyMessageFragment extends DialogFragment {

    private static final String MESSAGE = "message";

    private static final String DIALOG_SAVED_STATE = "dialog_saved_state";

    private final EventListener mEventListener = new EventListener();

    private AugmentedMessage mAugmentedMessage;

    private PrivateMessageClient mClient;

    private ProgressDialog mDialog;

    private TextView mMessageReply;

    public static ReplyMessageFragment getInstance(final AugmentedMessage message) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(MESSAGE, message);

        final ReplyMessageFragment fragment = new ReplyMessageFragment();
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

        mClient = RetrofitPrivateMessageClient.getClient(getActivity());
        mClient.getBus().register(mEventListener);

        // Set the dialog style
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Light_Dialog);

        // Setup arguments
        mAugmentedMessage = getArguments().getParcelable(MESSAGE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mClient.getBus().unregister(mEventListener);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.reply_message_dialog_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE);

        final XDAAccount account = AccountUtils.getAccount(getActivity());

        final ImageView imageView = (ImageView) view
                .findViewById(R.id.reply_message_dialog_fragment_avatar);
        Picasso.with(getActivity())
                .load(account.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(imageView);

        final TextView header = (TextView) view.findViewById(R.id
                .reply_message_fragment_header_title);
        header.setText(getString(R.string.replying_to, mAugmentedMessage.getTitle()));

        final View sendButton = view.findViewById(R.id.reply_message_dialog_fragment_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendMessageReply();
            }
        });
        sendButton.setEnabled(false);

        mMessageReply = (TextView) view.findViewById(R.id.reply_message_dialog_fragment_message);
        mMessageReply.addTextChangedListener(new NonEmptyTextViewListener(sendButton));

        final TextView username = (TextView) view.findViewById(R.id
                .reply_message_fragment_header_username);
        username.setText(account.getUserName());

        if (savedInstanceState != null && savedInstanceState.getBoolean(DIALOG_SAVED_STATE)) {
            mDialog.show();
        }
    }

    private void onSendMessageReply() {
        final InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMessageReply.getWindowToken(), 0);
        mClient.sendMessageAsync(mAugmentedMessage.getFromUserName(),
                mAugmentedMessage.getTitle(), mMessageReply.getText().toString());
        mDialog.show();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(DIALOG_SAVED_STATE, mDialog.isShowing());
        mDialog.dismiss();
    }

    private class EventListener {

        @Subscribe
        public void onMessageSentSuccessful(final MessageSentEvent event) {
            mDialog.dismiss();
            Toast.makeText(getActivity(), R.string.message_reply_success,
                    Toast.LENGTH_LONG).show();
            dismiss();
        }

        @Subscribe
        public void onMessageSentSucessful(final MessageSendingFailedEvent event) {
            mDialog.dismiss();
            Toast.makeText(getActivity(), R.string.message_reply_failed,
                    Toast.LENGTH_LONG).show();
        }
    }
}