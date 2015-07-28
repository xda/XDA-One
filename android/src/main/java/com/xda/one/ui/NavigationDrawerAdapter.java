package com.xda.one.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xda.one.R;
import com.xda.one.auth.XDAAccount;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerAdapter extends BaseAdapter {

    private final List<NavigationDrawerItem> NAVIGATION_DRAWER_ITEMS = new ArrayList<>();

    private final Context mContext;

    private final LayoutInflater mLayoutInflater;

    public NavigationDrawerAdapter(final Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return NAVIGATION_DRAWER_ITEMS.size();
    }

    @Override
    public NavigationDrawerItem getItem(int position) {
        return NAVIGATION_DRAWER_ITEMS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final NavigationDrawerItem item = getItem(position);

        final ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater
                    .inflate(R.layout.navigation_drawer_list_item, parent, false);

            holder = new ViewHolder();
            holder.iconView = (ImageView) convertView.findViewById(R.id
                    .navigation_drawer_list_item_icon);
            holder.titleView = (TextView) convertView.findViewById(R.id
                    .navigation_drawer_list_item_title);
            holder.countView = (TextView) convertView.findViewById(R.id
                    .navigation_drawer_list_item_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.iconView.setImageResource(item.getDrawableId());

        holder.titleView.setText(mContext.getString(item.getTitleId()));

        // Remove the count view for now
        if (TextUtils.isEmpty(item.getCountString())) {
            holder.countView.setVisibility(View.GONE);
        } else {
            holder.countView.setVisibility(View.VISIBLE);
            holder.countView.setText(item.getCountString());
        }

        return convertView;
    }

    public void onUserProfileChanged(final XDAAccount account) {
        NAVIGATION_DRAWER_ITEMS.clear();

        NAVIGATION_DRAWER_ITEMS.add(new NavigationDrawerItem(R.drawable.forum,
                R.string.forum_home_title, R.string.forums));
        NAVIGATION_DRAWER_ITEMS.add(new NavigationDrawerItem(R.drawable.ic_feed_icon,
                R.string.xda_news, R.string.forums));
        NAVIGATION_DRAWER_ITEMS.add(new NavigationDrawerItem(R.drawable.ic_search_dark,
                R.string.find_a_device, R.string.forums));

        if (account != null) {
            final String quoteMentionsCount = account.getQuoteCount() + "/" + account
                    .getMentionCount();
            NAVIGATION_DRAWER_ITEMS.add(new NavigationDrawerItem(R.drawable.account_circle,
                    R.string.quote_mentions, R.string.user, quoteMentionsCount));
            NAVIGATION_DRAWER_ITEMS.add(new NavigationDrawerItem(R.drawable.phone,
                    R.string.my_devices, R.string.user));

            NAVIGATION_DRAWER_ITEMS.add(new NavigationDrawerItem(R.drawable.message,
                    R.string.private_messages, R.string.user,
                    String.valueOf(account.getPmCount())));

            NAVIGATION_DRAWER_ITEMS.add(new NavigationDrawerItem(R.drawable.favorite,
                    R.string.subscribed, R.string.user));
            NAVIGATION_DRAWER_ITEMS.add(new NavigationDrawerItem(R.drawable.reply,
                    R.string.participated, R.string.user));
        }

        NAVIGATION_DRAWER_ITEMS.add(new NavigationDrawerItem(R.drawable.ic_search_dark,
                R.string.search, R.string.other));
    }

    public static class NavigationDrawerItem {

        private final int mDrawableId;

        private final int mTitleId;

        private final int mSectionId;

        private final String mCountString;

        public NavigationDrawerItem(final int drawableId, final int titleId, final int sectionId) {
            this(drawableId, titleId, sectionId, null);
        }

        public NavigationDrawerItem(final int drawableId,
                                    final int titleId, final int sectionId, final String countString) {
            mDrawableId = drawableId;
            mTitleId = titleId;
            mSectionId = sectionId;
            mCountString = countString;
        }

        public int getDrawableId() {
            return mDrawableId;
        }

        public int getTitleId() {
            return mTitleId;
        }

        public int getSectionId() {
            return mSectionId;
        }

        public String getCountString() {
            return mCountString;
        }
    }

    private static class ViewHolder {

        public ImageView iconView;

        public TextView titleView;

        public TextView countView;
    }
}