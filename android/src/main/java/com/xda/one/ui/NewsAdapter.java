package com.xda.one.ui;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.model.response.ResponseNews;
import com.xda.one.util.StringUtils;
import com.xda.one.util.Utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewsAdapter
        extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private static final int NORMAL_VIEW_TYPE = 1;

    private static final int FOOTER_VIEW_TYPE = 2;

    private int mFooterItemCount = 0;

    protected final LayoutInflater mLayoutInflater;

    private final Context mContext;

    private final View.OnClickListener mOnClickListener;

    private final List<ResponseNews> mNews;

    public NewsAdapter(final Context context, final View.OnClickListener clickListener) {
        mContext = context;
        mOnClickListener = clickListener;
        mLayoutInflater = LayoutInflater.from(context);
        mNews = new ArrayList<>();
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == mNews.size()) {
            return FOOTER_VIEW_TYPE;
        }
        return NORMAL_VIEW_TYPE;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == FOOTER_VIEW_TYPE) {
            final View view = mLayoutInflater.inflate(R.layout.load_more_progress_bar_only,
                    parent, false);
            return new FooterViewType(view);
        }
        final View view = mLayoutInflater.inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) {
        if (getItemViewType(position) == FOOTER_VIEW_TYPE) {
            return;
        }

        final ResponseNews item = getItem(position);

        holder.itemView.setTag(item.getUrl());
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.titleView.setText(Html.fromHtml(item.getTitle()));

        final String content = item.getContent();
        final String text = StringUtils.trimCharSequence(Html.fromHtml(content).toString(), 200);
        holder.contentView.setText(text);

        Picasso.with(mContext)
                .load(item.getThumbnail())
                .placeholder(R.drawable.ic_account_circle_light)
                .error(R.drawable.ic_account_circle_light)
                .into(holder.imageView);
    }

    public ResponseNews getItem(int position) {
        return mNews.get(position);
    }

    @Override
    public int getItemCount() {
        return mNews.size() + mFooterItemCount;
    }

    public void clear() {
        if (isEmpty()) {
            return;
        }

        final int count = mNews.size();
        mNews.clear();
        notifyItemRangeRemoved(0, count + mFooterItemCount--);
    }

    public void addAll(final List<ResponseNews> news) {
        if (Utils.isCollectionEmpty(news)) {
            return;
        }

        final int count = mNews.size();
        mNews.addAll(news);

        if (count == 0) {
            // Add the footer in as well
            notifyItemRangeInserted(count, news.size() + ++mFooterItemCount);
        } else {
            notifyItemRangeInserted(count, news.size());
        }
    }

    public void removeFooter() {
        mFooterItemCount = 0;
        notifyItemRemoved(mNews.size());
    }

    public boolean isEmpty() {
        return mNews.isEmpty();
    }

    public List<ResponseNews> getNews() {
        return Collections.unmodifiableList(mNews);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleView;

        private final TextView contentView;

        private final ImageView imageView;

        public NewsViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.news_title);
            contentView = (TextView) itemView.findViewById(R.id.news_content);
            imageView = (ImageView) itemView.findViewById(R.id.avatar);
        }
    }

    private static class FooterViewType extends NewsViewHolder {

        public FooterViewType(final View itemView) {
            super(itemView);
        }
    }
}