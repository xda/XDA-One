package com.xda.one.ui;

import com.squareup.otto.Subscribe;
import com.xda.one.R;
import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.api.model.response.ResponseUnifiedThread;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.api.retrofit.RetrofitThreadClient;
import com.xda.one.event.thread.ThreadSubscriptionChangedEvent;
import com.xda.one.event.thread.ThreadSubscriptionChangingFailedEvent;
import com.xda.one.model.augmented.AugmentedPost;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.ui.helper.QuickReturnHelper;
import com.xda.one.ui.widget.FloatingActionButton;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.CompatUtils;
import com.xda.one.util.UIUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.XDALinerLayoutManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PostPagerFragment extends Fragment
        implements PostFragment.Callback {

    public static final String THREAD_PAGE_COUNT_ARGUMENT = "thread_page_count";

    public static final String POST_PAGE_ARGUMENT = "post_page";

    public static final int CREATE_POST_REQUEST_CODE = 101;

    private static final String THREAD_ARGUMENT = "thread";

    private static final String PAGE_CONTAINER_ARGUMENT = "page_container";

    private static final String FORUM_HIERARCHY_ARGUMENT = "hierarchy";

    private final ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager
            .OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            final int page = position + 1;
            mTopBar.setText("Page " + page);
            updatePagingButtonVisibility(position);

            mQuickReturnHelper.setPosition(position);
            mQuickReturnHelper.showTopBar();

            getCurrentFragment().finishActionMode();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private final EventHandler mEventHandler = new EventHandler();

    private ViewPager mViewPager;

    private QuickReturnHelper mQuickReturnHelper;

    private RecyclerView mPageRecyclerView;

    private PostFragmentAdapter mAdapter;

    private int mTotalPages;

    private AugmentedUnifiedThread mUnifiedThread;

    private ThreadClient mThreadClient;

    private List<String> mHierarchy;

    private HierarchySpinnerAdapter mSpinnerAdapter;

    private ResponsePostContainer mContainerArgument;

    private Callback mCallback;

    private int mTargetHeight;

    private PostPageAdapter mPageAdapter;

    private TextView mTopBar;

    private View mFirst;

    private View mLast;

    public static PostPagerFragment getInstance(final AugmentedUnifiedThread unifiedThread,
            final ResponsePostContainer container, final int pageCount,
            final ArrayList<String> hierarchy) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(THREAD_ARGUMENT, unifiedThread);
        bundle.putParcelable(PAGE_CONTAINER_ARGUMENT, container);
        bundle.putInt(THREAD_PAGE_COUNT_ARGUMENT, pageCount);
        bundle.putStringArrayList(FORUM_HIERARCHY_ARGUMENT, hierarchy);

        final PostPagerFragment postPagerFragment = new PostPagerFragment();
        postPagerFragment.setArguments(bundle);

        return postPagerFragment;
    }

    public void collapse(final View view) {
        final Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.getLayoutParams().height = 0;
                    view.setLayoutParams(view.getLayoutParams());
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = mTargetHeight - (int) (mTargetHeight
                            * interpolatedTime);
                    view.setLayoutParams(view.getLayoutParams());
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        animation.setDuration(100);
        view.startAnimation(animation);
    }

    public void expand(final View view) {
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);
        final Animation animation = new Animation() {
            @Override
            protected void applyTransformation(final float interpolatedTime,
                    final Transformation transformation) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (mTargetHeight * interpolatedTime);
                view.setLayoutParams(view.getLayoutParams());
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(200);
        view.startAnimation(animation);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        mCallback = (Callback) activity;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mThreadClient = RetrofitThreadClient.getClient(getActivity());

        mUnifiedThread = getArguments().getParcelable(THREAD_ARGUMENT);
        mTotalPages = getArguments().getInt(THREAD_PAGE_COUNT_ARGUMENT, 0);
        mHierarchy = getArguments().getStringArrayList(FORUM_HIERARCHY_ARGUMENT);

        if (savedInstanceState == null) {
            mContainerArgument = getArguments().getParcelable(PAGE_CONTAINER_ARGUMENT);
        }

        mAdapter = new PostFragmentAdapter(getChildFragmentManager(), mUnifiedThread,
                mTotalPages, mContainerArgument);
        mSpinnerAdapter = new HierarchySpinnerAdapter();

        mPageAdapter = new PostPageAdapter(getActivity(), mTotalPages, new PostPageClickListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.post_pager_fragment, container, false);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        final View header = view.findViewById(R.id.pagination_bar);
        mPageRecyclerView = (RecyclerView) view.findViewById(R.id.page_list);
        mPageRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));

        if (mTotalPages > 1) {
            mPageRecyclerView.setAdapter(mPageAdapter);
            ViewCompat.setOverScrollMode(mPageRecyclerView, ViewCompat.OVER_SCROLL_NEVER);
            mPageRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mTargetHeight = mPageRecyclerView.getMeasuredHeight();
                    mPageRecyclerView.getLayoutParams().height = 0;
                    mPageRecyclerView.setLayoutParams(mPageRecyclerView.getLayoutParams());
                    mPageRecyclerView.setVisibility(View.GONE);
                }
            });

            mTopBar = (TextView) header.findViewById(R.id.page_top_textview);
            mTopBar.setText("Page 1");
            mTopBar.setOnClickListener(new TopBarClickListener());
            setupPagingButtons(header);

            final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
            mQuickReturnHelper = new QuickReturnHelper(getActivity(), header, actionBar);
        } else {
            header.setVisibility(View.GONE);
            mPageRecyclerView.setVisibility(View.GONE);
        }

        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        actionBar.setTitle(null);
        actionBar.setSubtitle(null);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mSpinnerAdapter);
        actionBar.setSelectedNavigationItem(mSpinnerAdapter.getCount() - 1);

        final Drawable background = new ColorDrawable(
                getResources().getColor(R.color.colorPrimary));
        actionBar.setBackgroundDrawable(background);

        mViewPager = (ViewPager) view.findViewById(R.id.post_pager_fragment_view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
        if (mContainerArgument != null) {
            mViewPager.setCurrentItem(mContainerArgument.getCurrentPage() - 1);
        }

        final FloatingActionButton button = (FloatingActionButton) view
                .findViewById(R.id.post_pager_fragment_floating_reply_button);
        button.setOnClickListener(new CreatePostListener());
        if (CompatUtils.hasLollipop()) {
            final Drawable drawable = getResources().getDrawable(R.drawable.fab_background);
            button.setBackground(drawable);
        } else {
            final int color = getResources().getColor(R.color.fab_color);
            button.setBackgroundColor(color);
        }

        mThreadClient.getBus().register(mEventHandler);
    }

    private void setupPagingButtons(final View header) {
        mFirst = header.findViewById(R.id.page_first);
        mFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        mLast = header.findViewById(R.id.page_last);
        mLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mViewPager.setCurrentItem(mTotalPages - 1);
            }
        });
    }

    private void updatePagingButtonVisibility(final int position) {
        mFirst.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        mLast.setVisibility(position == mTotalPages - 1 ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        mThreadClient.getBus().unregister(mEventHandler);
    }

    // Keeping this around because we'll almost certainly need this in the future
    @SuppressWarnings("unused")
    public PostFragment getCurrentFragment() {
        return (PostFragment) mViewPager.getAdapter().instantiateItem(mViewPager,
                mViewPager.getCurrentItem());
    }

    @Override
    public void quotePost(final AugmentedPost... posts) {
        final DialogFragment fragment = posts == null
                ? CreatePostFragment.createInstance(mUnifiedThread)
                : CreatePostFragment.createInstance(mUnifiedThread, posts);
        fragment.setTargetFragment(this, CREATE_POST_REQUEST_CODE);
        fragment.show(getFragmentManager(), "createPost");
    }

    @Override
    public void switchToFragment(final ResponsePostContainer container) {
        mAdapter.setContainerArgument(container);
        mViewPager.setCurrentItem(container.getCurrentPage() - 1);

        if (getCurrentFragment().getView() != null) {
            getCurrentFragment().scrollToPosition(container);
        }
    }

    @Override
    public void setQuickReturnListener(final RecyclerView recyclerView, final int position) {
        if (mTotalPages != 1) {
            mQuickReturnHelper.setOnScrollListener(recyclerView, position);
        }
    }

    @Override
    public void postPaddingToQuickReturn(final View content) {
        final View header = getView().findViewById(R.id.pagination_bar);
        QuickReturnHelper.postPaddingToQuickReturn(header, content);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == CREATE_POST_REQUEST_CODE) {
            onNewPostCreated();
        }
    }

    // Options menu
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.post_pager_fragment_ab, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final boolean visible = AccountUtils.isAccountAvailable(getActivity());

        final MenuItem item = menu.findItem(R.id.post_pager_fragment_subscribe_unsubscribe);
        item.setVisible(visible);

        if (visible) {
            item.setIcon(mUnifiedThread.isSubscribed()
                    ? R.drawable.ic_star_light
                    : R.drawable.ic_action_star_outline);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.post_pager_fragment_subscribe_unsubscribe:
                toggleThreadSubscription();
                return true;
        }
        return false;
    }

    private void toggleThreadSubscription() {
        mThreadClient.toggleSubscribeAsync(mUnifiedThread);
    }

    private void onNewPostCreated() {
        if (mUnifiedThread.getTotalPosts() % 10 == 0) {
            // TODO - need to work out what we need to do in this scenario
        } else {
            mViewPager.setCurrentItem(mTotalPages - 1);
            getCurrentFragment().refreshPageAndScrollToBottom();
        }
    }

    public interface Callback {

        public void login(final Runnable runnable);
    }

    private class CreatePostListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (AccountUtils.isAccountAvailable(getActivity())) {
                quoteOrReply();
            } else {
                mCallback.login(new Runnable() {
                    @Override
                    public void run() {
                        quoteOrReply();
                    }
                });
            }
        }

        private void quoteOrReply() {
            final PostFragment postFragment = getCurrentFragment();
            if (postFragment.isActionModeStarted()) {
                quotePost(postFragment.getCheckedItems());
                postFragment.finishActionMode();
            } else {
                quotePost();
            }
        }
    }

    private class HierarchySpinnerAdapter extends BaseAdapter
            implements ActionBar.OnNavigationListener {

        private final LayoutInflater mLayouInflater;

        public HierarchySpinnerAdapter() {
            mLayouInflater = LayoutInflater.from(getActivity());
        }

        @Override
        public int getCount() {
            return mHierarchy.size();
        }

        @Override
        public String getItem(int position) {
            return mHierarchy.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = convertView == null ? mLayouInflater.inflate(R.layout
                    .hierarchy_spinner_item, parent, false) : convertView;
            final TextView title = (TextView) convertView
                    .findViewById(R.id.hierarchy_spinner_item_name);
            final String text = getItem(position);
            final CharSequence formatted = Html.fromHtml(text);
            title.setText(formatted);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            final View view = getView(position, convertView, parent);
            final View imageView = view.findViewById(R.id.hierarchy_spinner_item_device_image);
            imageView.setVisibility(View.VISIBLE);
            return view;
        }

        @Override
        public boolean onNavigationItemSelected(int itemPosition, long itemId) {
            getFragmentManager().popBackStack(getItem(itemPosition), 0);
            return true;
        }
    }

    @Override
    public void onPageLoaded(final ResponseUnifiedThread thread) {
        if (getTargetFragment() != null) {
            final Intent intent = new Intent();
            intent.putExtra("thread", thread);
            getTargetFragment()
                    .onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
    }

    private class EventHandler {

        @Subscribe
        public void onThreadSubscriptionToggled(final ThreadSubscriptionChangedEvent event) {
            mUnifiedThread.setSubscribedFlag(event.isNowSubscribed);
            getActivity().supportInvalidateOptionsMenu();

            if (event.isNowSubscribed) {
                Toast.makeText(getActivity(), R.string.thread_subscription_subscribed,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), R.string.thread_subscription_unsubscribed,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Subscribe
        public void onThreadSubscriptionToggleFailed(final ThreadSubscriptionChangingFailedEvent
                event) {
            Toast.makeText(getActivity(), R.string.thread_subscription_toggle_failed,
                    Toast.LENGTH_LONG).show();
        }
    }

    private class PostPageClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View view) {
            collapse(mPageRecyclerView);

            final int position = mPageRecyclerView.getChildPosition(view);
            mViewPager.setCurrentItem(position);
        }
    }

    private class TopBarClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final int visibility = mPageRecyclerView.getVisibility();
            if (visibility == View.VISIBLE) {
                collapse(mPageRecyclerView);
            } else {
                expand(mPageRecyclerView);
            }
        }
    }
}