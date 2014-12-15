package com.xda.one.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.com.xda.one.googleplus.GPlusLoginClass;
import com.com.xda.one.googleplus.ITokenEventCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.otto.Subscribe;
import com.xda.one.R;
import com.xda.one.api.inteface.UserClient;
import com.xda.one.api.retrofit.RetrofitUserClient;
import com.xda.one.event.user.UserLoginEvent;
import com.xda.one.event.user.UserLoginFailedEvent;
import com.xda.one.ui.listener.MultipleNonEmptyTextViewListener;
import com.xda.one.util.FragmentUtils;

public class LoginFragment extends Fragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ITokenEventCallback {
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    private UserClient mUserClient;

    private EditText mUsername;

    private EditText mPassword;

    private View mLogin;

    private Object mEventListener;

    private ProgressDialog mProgressDialog;

    private String mAccountName;
    private String mAccessToken = "";

    /* Client used to interact with Google APIs. */
    //private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    private boolean mIntentInProgress;

    /*
     * Track whether the sign-in button has been clicked so that we know to resolve
     * all issues preventing sign-in without waiting.
     */
    private boolean mSignInClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;

    public static Fragment createInstance(final String accountName) {
        final Bundle bundle = new Bundle();
        bundle.putString(ARG_ACCOUNT_NAME, accountName);

        final LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(bundle);
        return loginFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserClient = RetrofitUserClient.getClient(getActivity());

        mAccountName = getArguments().getString(ARG_ACCOUNT_NAME);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle("Logging in...");
        mProgressDialog.setMessage("Logging in...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

       /* mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                //.addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();*/
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
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        mLogin = view.findViewById(R.id.submit);
        mLogin.setEnabled(false);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        view.findViewById(R.id.sign_in_button).setOnClickListener(this);

        mUsername = (EditText) view.findViewById(R.id.accountName);
        mUsername.setText(mAccountName);

        mPassword = (EditText) view.findViewById(R.id.accountPassword);

        final MultipleNonEmptyTextViewListener listener = new MultipleNonEmptyTextViewListener
                (mLogin, mUsername, mPassword);
        listener.registerAll();

        final Button register = (Button) view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final FragmentTransaction transaction = FragmentUtils
                        .getDefaultTransaction(getFragmentManager());
                transaction.replace(R.id.frame_activity_content, RegisterFragment.createInstance(false, mAccessToken))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

       /* mGoogleApiClient.connect();*/
    }

    @Override
    public void onStop() {
        super.onStop();

       /* if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }*/
    }

    public void submit() {
        final String userName = mUsername.getText().toString();
        final String userPass = mPassword.getText().toString();

        mProgressDialog.show();

        mEventListener = new EventListener();
        mUserClient.getBus().register(mEventListener);
        mUserClient.login(userName, userPass);
    }

    @Override
    public void onTokenResult(final String token) {
        mAccessToken = token;
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                mProgressDialog.show();

                mEventListener = new EventListener();
                mUserClient.getBus().register(mEventListener);
                mUserClient.googlelogin(token);
            }
        });
    }

    /* A helper method to resolve the current ConnectionResult error. */
   /* private void resolveSignInError() {
        if (mConnectionResult!=null && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }*/

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                // resolveSignInError();
            }
        }
    }

    @Override
    public void onClick(View view) {
        new GPlusLoginClass((XDAAuthenticatorActivity)getActivity(), this).loginGPlus();
        /*if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
           // resolveSignInError();
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);

        if (requestCode == RC_SIGN_IN) {
            if (responseCode != Activity.RESULT_OK) {
                mSignInClicked = false;
            }

           /* mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();

                String accessToken = null;
                try {
                    accessToken = GoogleAuthUtil.getToken(getActivity(),
                            Plus.AccountApi.getAccountName(mGoogleApiClient),
                            "oauth2:" + Scopes.PROFILE);
                } catch (IOException transientEx) {
                    // network or server error, the call is expected to succeed if you try again later.
                    // Don't attempt to call again immediately - the request is likely to
                    // fail, you'll hit quotas or back-off.
                    return;
                } catch (UserRecoverableAuthException e) {
                    // Recover
                    accessToken = null;
                } catch (GoogleAuthException authEx) {
                    // Failure. The call is not expected to ever succeed so it should not be
                    // retried.
                    return;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                Log.e("XDA-One", accessToken);
            }*/
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mSignInClicked = false;
        Toast.makeText(getActivity(), "User is connected!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(final int i) {
        //mGoogleApiClient.connect();
    }

    private final class EventListener {

        @Subscribe
        public void onUserProfileReceived(final UserLoginEvent event) {
            mEventListener = null;
            mUserClient.getBus().unregister(this);

            mProgressDialog.setTitle("Updating forums database");
            mProgressDialog.setMessage("Updating forums database");
            getLoaderManager().initLoader(0, null, new AuthForumLoaderCallbacks(getActivity(),
                    event.account, mProgressDialog));
        }

        @Subscribe
        public void onLoginFailed(final UserLoginFailedEvent event) {
            mEventListener = null;
            mUserClient.getBus().unregister(this);
            mProgressDialog.dismiss();

            if (event.result.isFromGoogle()) {
                final FragmentTransaction transaction = FragmentUtils
                        .getDefaultTransaction(getFragmentManager());
                transaction.replace(R.id.frame_activity_content, RegisterFragment.createInstance(event.result.isFromGoogle(), mAccessToken))
                        .addToBackStack(null)
                        .commit();
            } else {
                final String output = event.result == null ? "Something went wrong"
                        : event.result.getMessage();
                Toast.makeText(getActivity(), output, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

//mMessage = {java.lang.String@830030741768}"Forbidden: No XDA account found for Google "