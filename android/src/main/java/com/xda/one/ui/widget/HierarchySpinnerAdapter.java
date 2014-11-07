package com.xda.one.ui.widget;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xda.one.R;

import java.util.List;

/**
 * Created by Shazi on 11/6/2014.
 */
public class HierarchySpinnerAdapter extends BaseAdapter
        implements ActionBar.OnNavigationListener {

    private LayoutInflater mLayouInflater = null;

    private List<String> mHierarchy;

    private FragmentManager fragmentManager;

    Context context;


    public HierarchySpinnerAdapter(Context context, LayoutInflater mLayoutInflater,
        List<String> mHierarchy,FragmentManager fragmentManager) {
        this.context = context;
        mLayouInflater = mLayoutInflater;
        this.mHierarchy = mHierarchy;
        this.fragmentManager = fragmentManager;
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
        /*getFragmentManager()*/fragmentManager.popBackStack(getItem(itemPosition), 0);
        return true;
    }
}
