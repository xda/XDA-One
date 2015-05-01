package com.xda.one.ui;

import android.accounts.Account;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.mobsandgeeks.adapters.Sectionizer;
import com.mobsandgeeks.adapters.SimpleSectionAdapter;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.inteface.UserClient;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.api.retrofit.RetrofitClient;
import com.xda.one.api.retrofit.RetrofitUserClient;
import com.xda.one.auth.XDAAccount;
import com.xda.one.auth.XDAAuthenticatorActivity;
import com.xda.one.event.user.UserLoginEvent;
import com.xda.one.event.user.UserProfileEvent;
import com.xda.one.loader.ForumLoader;
import com.xda.one.model.misc.ForumType;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.Utils;

import java.util.List;

import static com.xda.one.ui.NavigationDrawerAdapter.NavigationDrawerItem;

public class NavigationDrawerFragment extends Fragment
        implements AdapterView.OnItemClickListener {

    private final UserListener mUserListener = new UserListener();

    private Callback mCallback;

    private NavigationDrawerAdapter mAdapter;

    private SimpleSectionAdapter<NavigationDrawerItem> mSectionAdapter;

    private TextView mUsernameTextView;

    private TextView mEmailTextView;

    private UserClient mUserClient;

    private ImageView mAvatar;

    private CircularProgressButton mLoginLogout;

    private ListView mListView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserClient = RetrofitUserClient.getClient(getActivity());

        mAdapter = new NavigationDrawerAdapter(getActivity());
        mSectionAdapter = new SimpleSectionAdapter<>(getActivity(), mAdapter,
                R.layout.list_view_header, R.id.list_view_header_title,
                new NavigationSectionizer());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.navigation_drawer_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(android.R.id.list);
        ViewCompat.setOverScrollMode(mListView, ViewCompat.OVER_SCROLL_NEVER);

        mListView.setOnItemClickListener(this);

        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View headerView = inflater.inflate(R.layout.navigation_drawer_header,
                mListView, false);
        mListView.addHeaderView(headerView);

        // Register for the login event
        mUserClient.getBus().register(mUserListener);

        final View headerUser = headerView.findViewById(R.id.navigation_drawer_header_background);
        headerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivityForResult(new Intent(getActivity(), UserProfileActivity.class), 100);
            }
        });

        mUsernameTextView = (TextView) headerView
                .findViewById(R.id.navigation_drawer_fragment_username);

        mEmailTextView = (TextView) headerView
                .findViewById(R.id.navigation_drawer_fragment_email);

        mAvatar = (ImageView) headerView.findViewById(R.id.navigation_drawer_fragment_avatar);

        mLoginLogout = (CircularProgressButton) headerView
                .findViewById(R.id.navigation_drawer_login_logout);
        mLoginLogout.setOnClickListener(new LoginLogoutListener());
        mLoginLogout.setIndeterminateProgressMode(true);

        final XDAAccount selectedAccount = AccountUtils.getAccount(getActivity());
        if (selectedAccount == null) {
            RetrofitClient.setAuthToken(null);
        } else {
            RetrofitClient.setAuthToken(selectedAccount.getAuthToken());
            mUserClient.getUserProfileAsync();
        }
        onUserAccountSelected(selectedAccount);

        mAdapter.onUserProfileChanged(selectedAccount);
        mSectionAdapter.notifyDataSetChanged();

        mListView.setAdapter(mSectionAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mUserClient.getBus().unregister(mUserListener);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                final XDAAccount account = data.getParcelableExtra("account");
                AccountUtils.storeAccount(getActivity(), account);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final NavigationDrawerItem menuItem = mSectionAdapter.getItem(position - 1);
        final int title = menuItem.getTitleId();
        switch (title) {
            case R.string.forum_home_title:
                mCallback.onNavigationItemClicked(ForumPagerFragment.createInstance());
                break;
            case R.string.xda_news:
                mCallback.onNavigationItemClicked(NewsFragment.createInstance());
                break;
            case R.string.find_a_device:
                mCallback.onNavigationItemClicked(FindYourDeviceFragment.createInstance());
                break;
            case R.string.search:
                mCallback.onNavigationItemClicked(SearchFragment.createInstance());
                break;
            case R.string.private_messages:
                mCallback.onNavigationItemClicked(new MessagePagerFragment());
                break;
            case R.string.subscribed:
                mCallback.onNavigationItemClicked(new SubscribedPagerFragment());
                break;
            case R.string.participated:
                mCallback.onNavigationItemClicked(ThreadFragment.createParticipated());
                break;
            case R.string.my_devices:
                mCallback.onNavigationItemClicked(new MyDeviceFragment());
                break;
            case R.string.quote_mentions:
                mCallback.onNavigationItemClicked(QuoteMentionPagerFragment.getInstance());
                break;
            case R.string.settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;
            case R.string.help:
                startActivity(new Intent(getActivity(), HelpActivity.class));
                break;
            case R.string.about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
        }
        mCallback.closeNavigationDrawer();
    }

    public void onUserAccountSelected(final XDAAccount account) {
        if (account == null) {
            mLoginLogout.setIdleText(getString(R.string.login));
            mLoginLogout.setText(getString(R.string.login));

            mUsernameTextView.setText("Anonymous");
            Picasso.with(getActivity()).load(R.drawable.ic_account_circle_light).into(mAvatar);
        } else {
            mLoginLogout.setIdleText(getString(R.string.logout));
            mLoginLogout.setText(getString(R.string.logout));

            mUsernameTextView.setText(account.getUserName());
            mEmailTextView.setText(account.getEmail());
            Picasso.with(getActivity())
                    .load(account.getAvatarUrl())
                    .placeholder(R.drawable.ic_account_circle_light)
                    .error(R.drawable.ic_account_circle_light)
                    .into(mAvatar);
        }
        mLoginLogout.setProgress(0);

        mAdapter.onUserProfileChanged(account);
        mSectionAdapter.notifyDataSetChanged();
    }

    public void login() {
        final Intent intent = new Intent(getActivity(), XDAAuthenticatorActivity.class);
        startActivityForResult(intent, 100);
    }

    private void logout() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Logging out...");
        progressDialog.setMessage("Logging out...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        getLoaderManager().initLoader(0, null, new LogoutForumLoader(progressDialog));
    }

    public interface Callback {

        void closeNavigationDrawer();

        void onNavigationItemClicked(final Fragment fr);
    }

    private class NavigationSectionizer implements Sectionizer<NavigationDrawerItem> {

        @Override
        public String getSectionTitleForItem(final NavigationDrawerItem menuItem) {
            return getString(menuItem.getSectionId());
        }
    }

    private class UserListener {

        @Subscribe
        public void onUserProfile(final UserProfileEvent event) {
            AccountUtils.storeAccount(getActivity(), event.account);
            onUserAccountSelected(event.account);
        }

        @Subscribe
        public void onLogin(final UserLoginEvent event) {
            AccountUtils.storeAccount(getActivity(), event.account);
            onUserAccountSelected(event.account);
        }
    }

    private class LoginLogoutListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final Account currentAccount = AccountUtils.getAccount(getActivity());
            if (currentAccount == null) {
                login();
                return;
            }

            logout();
        }
    }

    private class LogoutForumLoader implements LoaderManager.LoaderCallbacks<List<ResponseForum>> {

        private final ProgressDialog mProgressDialog;

        public LogoutForumLoader(final ProgressDialog progressDialog) {
            mProgressDialog = progressDialog;
        }

        @Override
        public Loader<List<ResponseForum>> onCreateLoader(final int id, final Bundle args) {
            return new ForumLoader(getActivity(), ForumType.ALL, null, true);
        }

        @Override
        public void onLoadFinished(final Loader<List<ResponseForum>> loader,
                final List<ResponseForum> data) {
            mProgressDialog.dismiss();

            if (Utils.isCollectionEmpty(data)) {
                Toast.makeText(getActivity(), R.string.failed_to_logout, Toast.LENGTH_LONG).show();
                return;
            }

            AccountUtils.storeAccount(getActivity(), null);
            onUserAccountSelected(null);
        }

        @Override
        public void onLoaderReset(final Loader<List<ResponseForum>> loader) {
        }
    }
}