package com.xda.one.ui;

import com.xda.one.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PostPageAdapter extends RecyclerView.Adapter<PostPageAdapter.PageViewHolder> {

    private final LayoutInflater mLayoutInflater;

    private final int mCount;

    private final View.OnClickListener mOnClickListener;

    public PostPageAdapter(final Context context, final int count,
            final View.OnClickListener clickListener) {
        mCount = count;
        mOnClickListener = clickListener;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public PageViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = mLayoutInflater.inflate(R.layout.post_page_dropdown, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PageViewHolder holder, final int position) {
        final int page = position + 1;
        holder.textView.setText("Page " + page);

        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public PageViewHolder(final View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.page_top_textview);
        }
    }
}