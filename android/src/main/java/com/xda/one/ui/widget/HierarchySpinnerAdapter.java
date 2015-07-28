package com.xda.one.ui.widget;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xda.one.R;

import java.util.List;

public class HierarchySpinnerAdapter extends BaseAdapter
        implements ActionBar.OnNavigationListener {

    Context context;
    private LayoutInflater mLayouInflater = null;
    private List<String> mHierarchy;
    private FragmentManager fragmentManager;


    public HierarchySpinnerAdapter(Context context, LayoutInflater mLayoutInflater,
                                   List<String> mHierarchy, FragmentManager fragmentManager) {
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
        return getView(position, convertView, parent);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        /*getFragmentManager()*/
        fragmentManager.popBackStack(getItem(itemPosition), 0);
        return true;
    }
}
