package com.xda.one.ui;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.inteface.PrivateMessageClient;
import com.xda.one.api.retrofit.RetrofitPrivateMessageClient;
import com.xda.one.event.message.MessageDeletedEvent;
import com.xda.one.model.augmented.AugmentedMessage;
import com.xda.one.parser.TextDataStructure;
import com.xda.one.ui.widget.FloatingActionButton;
import com.xda.one.util.CompatUtils;
import com.xda.one.util.SectionUtils;
import com.xda.one.util.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewMessageFragment extends Fragment {

    public static final String MESSAGE_ARGUMENT = "message";

    public static final String ACTIVITY_RESULT_BUNDLE_ARGUMENTS = "bundle_arguments";

    public static final String ACTIVITY_RESULT_BUNDLE_ARGUMENTS_RESULT = "bundle_arguments_result";

    private final Object mEventHandler = new Object() {
        @Subscribe
        public void onMessageDeleted(final MessageDeletedEvent event) {
            Toast.makeText(getActivity(), R.string.message_delete_successful, Toast.LENGTH_LONG)
                    .show();
            mBundle.putSerializable(ACTIVITY_RESULT_BUNDLE_ARGUMENTS_RESULT,
                    ViewMessageResult.DELETED);

            setupIntent();
            getActivity().finish();
        }
    };

    public static final int REPLY_MESSAGE_REQUEST_CODE = 1;

    private AugmentedMessage mMessage;

    private PrivateMessageClient mPrivateMessageClient;

    private Bundle mBundle;

    public static ViewMessageFragment createInstance(final AugmentedMessage message) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(MESSAGE_ARGUMENT, message);

        final ViewMessageFragment viewMessageFragment = new ViewMessageFragment();
        viewMessageFragment.setArguments(bundle);

        return viewMessageFragment;
    }

    private void setupIntent() {
        mBundle.putParcelable(MESSAGE_ARGUMENT, mMessage);

        final Intent intent = new Intent();
        intent.putExtra(ACTIVITY_RESULT_BUNDLE_ARGUMENTS, mBundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessage = getArguments().getParcelable(MESSAGE_ARGUMENT);

        mBundle = new Bundle();

        mPrivateMessageClient = RetrofitPrivateMessageClient.getClient(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_message_fragment, container, false);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        final TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(mMessage.getTitle());

        final TextView nameView = (TextView) view.findViewById(R.id.user_name);
        nameView.setText(mMessage.getFromUserName());

        final TextView dateView = (TextView) view.findViewById(R.id.date);
        dateView.setText(Utils.getRelativeDate(getActivity(), mMessage.getDate()));

        final LinearLayout layout = (LinearLayout) view.findViewById(R.id
                .view_message_fragment_content);
        final TextDataStructure structure = mMessage.getTextDataStructure();
        SectionUtils.setupSections(getActivity(), getLayoutInflater(savedInstanceState), layout,
                structure, new PostAdapter.GoToQuoteListener() {
                    @Override
                    public void onClick(final String postId) {

                    }
                });

        final ImageView avatarView = (ImageView) view.findViewById(R.id.avatar);
        Picasso.with(getActivity())
                .load(mMessage.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(avatarView);

        if (mMessage.isMessageUnread()) {
            mPrivateMessageClient.markMessageReadAsync(mMessage);
        }

        final FloatingActionButton button = (FloatingActionButton) view
                .findViewById(R.id.view_message_fragment_floating_reply_button);
        button.setOnClickListener(new CreateReplyListener());
        if (CompatUtils.hasL()) {
            final Drawable drawable = getResources().getDrawable(R.drawable.fab_background);
            button.setBackground(drawable);
        } else {
            final int color = getResources().getColor(R.color.fab_color);
            button.setBackgroundColor(color);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mPrivateMessageClient.getBus().register(mEventHandler);
    }

    @Override
    public void onPause() {
        super.onPause();

        setupIntent();
        mPrivateMessageClient.getBus().unregister(mEventHandler);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.view_message_fragment_ab, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_message_fragment_ab_mark_read_unread:
                mPrivateMessageClient.markMessageUnreadAsync(mMessage);

                // Replicated Gmail behaviour
                getActivity().finish();
                return true;
            case R.id.view_message_fragment_ab_delete:
                mPrivateMessageClient.deleteMessageAsync(mMessage);
                return true;
        }
        return false;
    }

    public void onBackPressed() {
        setupIntent();
    }

    public static enum ViewMessageResult {
        CHANGED,
        DELETED
    }

    private class CreateReplyListener implements View.OnClickListener {

        @Override
        public void onClick(final View view) {
            final DialogFragment fragment = ReplyMessageFragment.getInstance(mMessage);
            fragment.setTargetFragment(ViewMessageFragment.this, REPLY_MESSAGE_REQUEST_CODE);
            fragment.show(getFragmentManager(), "createPost");
        }
    }
}