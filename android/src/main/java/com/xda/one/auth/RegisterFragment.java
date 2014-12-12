package com.xda.one.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xda.one.R;
import com.xda.one.api.inteface.UserClient;
import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.Result;
import com.xda.one.api.retrofit.RetrofitUserClient;
import com.xda.one.constants.XDAConstants;
import com.xda.one.event.user.UserProfileEvent;
import com.xda.one.event.user.UserProfileFailedEvent;
import com.xda.one.ui.listener.MultipleNonEmptyTextViewListener;

import retrofit.client.Response;

public class RegisterFragment extends Fragment {

    public final static String ARG_GOOGLE_REGISTRATION = "FROM_GOOGLE";
    public final static String ARG_GOOGLE_TOKEN = "TOKEN_GOOGLE";
    private String mAccessToken = "";
    private boolean isFromGoogle;
    private UserClient mUserClient;

    private EditText mUsername;

    private EditText mPassword;

    private EditText mEmail;

    private EditText mResponse;

    private ReCaptcha mReCaptcha;

    private ProgressDialog mProgressDialog;

    private Object mEventListener;

    private ImageView mRecaptchaView;

    public static Fragment createInstance(boolean isFromGoogle, String token) {

        final Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_GOOGLE_REGISTRATION, isFromGoogle);
        bundle.putString(ARG_GOOGLE_TOKEN, token);

        final RegisterFragment registerFragment = new RegisterFragment();
        registerFragment.setArguments(bundle);
//        return new RegisterFragment();
        return registerFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserClient = RetrofitUserClient.getClient(getActivity());
        isFromGoogle = getArguments().getBoolean(ARG_GOOGLE_REGISTRATION);
        mAccessToken = getArguments().getString(ARG_GOOGLE_TOKEN);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Registering...");
        mProgressDialog.setMessage("Registering...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mEventListener != null) {
            mUserClient.getBus().unregister(mEventListener);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        mRecaptchaView = (ImageView) view.findViewById(R.id.recaptcha_imageview);
        mReCaptcha = new ReCaptcha(XDAConstants.RECAPTCHA_PUBLIC_KEY);
        mReCaptcha.showImageChallenge(mRecaptchaView);

        mUsername = (EditText) view.findViewById(R.id.register_fragment_username);
        mPassword = (EditText) view.findViewById(R.id.register_fragment_password);
        mEmail = (EditText) view.findViewById(R.id.register_fragment_email);
        mResponse = (EditText) view.findViewById(R.id.register_fragment_response);
        final Button submit = (Button) view.findViewById(R.id.register_fragment_submit);
        submit.setEnabled(false);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (isFromGoogle) submitGoogleRegisterData();
                else submitData();
            }
        });

        if (isFromGoogle) {
            checkForGoogle();
            final MultipleNonEmptyTextViewListener listener = new MultipleNonEmptyTextViewListener
                    (submit, mUsername);
            listener.registerAll();
//            submit.setEnabled(true);
        } else {
            final MultipleNonEmptyTextViewListener listener = new MultipleNonEmptyTextViewListener
                    (submit, mUsername, mPassword, mEmail, mResponse);
            listener.registerAll();
        }
    }

    private void submitData() {
        mProgressDialog.show();

        final String username = mUsername.getText().toString();
        final String password = mPassword.getText().toString();
        final String email = mEmail.getText().toString();
        final String response = mResponse.getText().toString();
        mUserClient.register(email, username, password, mReCaptcha.getImageToken(), response,
                new Consumer<Response>() {
                    @Override
                    public void run(final Response data) {
                        mEventListener = new EventListener();
                        mUserClient.getBus().register(mEventListener);

                        // Loggging in...
                        mProgressDialog.setTitle("Logging in...");
                        mProgressDialog.setMessage("Logging in...");
                        mUserClient.getUserProfileAsync();
                    }
                }, new Consumer<Result>() {
                    @Override
                    public void run(final Result data) {
                        mProgressDialog.dismiss();

                        mReCaptcha.showImageChallenge(mRecaptchaView);
                        Toast.makeText(getActivity(), data.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkForGoogle() {
        mPassword.setVisibility(View.GONE);
        mEmail.setVisibility(View.GONE);
        mResponse.setVisibility(View.GONE);
        mRecaptchaView.setVisibility(View.GONE);
    }

    private void submitGoogleRegisterData() {
        mProgressDialog.show();

        final String username = mUsername.getText().toString();
        mUserClient.googleregister(username, mAccessToken, new Consumer<Response>() {
            @Override
            public void run(final Response data) {
                mEventListener = new EventListener();
                mUserClient.getBus().register(mEventListener);

                // Loggging in...
                mProgressDialog.setTitle("Logging in...");
                mProgressDialog.setMessage("Logging in...");
                mUserClient.getUserProfileAsync();
            }
        }, new Consumer<Result>() {
            @Override
            public void run(final Result data) {
                mProgressDialog.dismiss();

                //ReCaptcha.showImageChallenge(mRecaptchaView);
                Toast.makeText(getActivity(), data.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private final class EventListener {

        @Subscribe
        public void onUserProfileReceived(final UserProfileEvent event) {
            mEventListener = null;
            mUserClient.getBus().unregister(this);

            mProgressDialog.setTitle("Updating forums database");
            mProgressDialog.setMessage("Updating forums database");
            getLoaderManager().initLoader(0, null, new AuthForumLoaderCallbacks(getActivity(),
                    event.account, mProgressDialog));
        }

        @Subscribe
        public void onLoginFailed(final UserProfileFailedEvent event) {
            mEventListener = null;
            mUserClient.getBus().unregister(this);
            mProgressDialog.dismiss();

            final String output = event.result == null ? "Something went wrong"
                    : event.result.getMessage();
            mReCaptcha.showImageChallenge(mRecaptchaView);
            Toast.makeText(getActivity(), output, Toast.LENGTH_SHORT).show();
        }
    }
}