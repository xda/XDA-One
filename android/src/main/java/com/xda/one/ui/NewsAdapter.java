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
    public NewsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = mLayoutInflater.inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) {
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
        return mNews.size();
    }

    public void clear() {
        final int count = mNews.size();
        mNews.clear();
        notifyItemRangeRemoved(0, count - 1);
    }

    public void addAll(final List<ResponseNews> news) {
        if (Utils.isCollectionEmpty(news)) {
            return;
        }

        final int count = mNews.size();
        mNews.addAll(news);
        notifyItemRangeInserted(count, news.size());
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
}