package com.xda.one.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dd.xda.CircularProgressButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.model.response.ResponseAttachment;
import com.xda.one.auth.XDAAccount;
import com.xda.one.model.augmented.AugmentedPost;
import com.xda.one.parser.TextDataStructure;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.SectionUtils;
import com.xda.one.util.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final Context mContext;

    private final GoToQuoteListener mQuoteListener;

    private final View.OnClickListener mMultiQuoteClickListener;

    private final ActionModeHelper mModeHelper;

    private final LayoutInflater mLayoutInflater;

    private final View.OnClickListener mDownloadClickListener;

    private final View.OnClickListener mImageClickListener;

    private final View.OnClickListener mAvatarClickListener;

    private final View.OnClickListener mThanksClickListener;

    private final View.OnClickListener mQuoteClickListener;

    private List<AugmentedPost> mPosts;

    public PostAdapter(final Context context, final ActionModeHelper modeHelper,
            final View.OnClickListener downloadClickListener,
            final View.OnClickListener imageClickListener,
            final View.OnClickListener avatarClickListener,
            final View.OnClickListener thanksClickListener,
            final View.OnClickListener quoteClickListener,
            final View.OnClickListener multiQuoteClickListener,
            final GoToQuoteListener quoteListener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);

        mModeHelper = modeHelper;
        mDownloadClickListener = downloadClickListener;
        mImageClickListener = imageClickListener;
        mAvatarClickListener = avatarClickListener;
        mThanksClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final CircularProgressButton button = (CircularProgressButton) v;
                button.setProgress(50);
                thanksClickListener.onClick(v);
            }
        };
        mQuoteClickListener = quoteClickListener;
        mMultiQuoteClickListener = multiQuoteClickListener;
        mQuoteListener = quoteListener;

        mPosts = new ArrayList<>();
    }

    @Override
    public PostViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = mLayoutInflater.inflate(R.layout.post_list_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        final AugmentedPost post = getPost(position);

        holder.itemView.setOnClickListener(mModeHelper);
        holder.itemView.setOnLongClickListener(mModeHelper);
        mModeHelper.updateActivatedState(holder.itemView, position);

        holder.userNameView.setText(post.getUserName());

        // TODO - make this more efficient
        holder.postLayout.removeAllViews();
        final TextDataStructure structure = post.getTextDataStructure();
        SectionUtils.setupSections(mContext, mLayoutInflater, holder.postLayout, structure,
                mQuoteListener);

        // Load the avatar into the image view
        Picasso.with(mContext)
                .load(post.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(holder.avatarView);
        holder.avatarView.setOnClickListener(mAvatarClickListener);
        holder.avatarView.setTag(post.getUserId());

        holder.attachments.removeAllViews();
        if (post.getAttachments() != null) {
            for (final ResponseAttachment responseAttachment : post.getAttachments()) {
                if (responseAttachment.hasThumbnail()) {
                    attachImagesThumbnail(holder, responseAttachment);
                } else {
                    attachFiles(holder, responseAttachment);
                }
            }
        }

        holder.dateView.setText(Utils.getRelativeDate(mContext, post.getDateline()));

        final XDAAccount account = AccountUtils.getAccount(mContext);
        if (account == null) {
            holder.actionsLayout.setVisibility(View.GONE);
        } else {
            holder.quoteButton.setOnClickListener(mQuoteClickListener);
            holder.quoteButton.setTag(position);

            holder.multiQuoteButton.setOnClickListener(mMultiQuoteClickListener);
            holder.multiQuoteButton.setTag(holder.itemView);

            if (account.getUserName().equals(post.getUserName())) {
                //holder.thanksButton.setVisibility(View.GONE);
                //holder.thanksCount.setVisibility(View.GONE);
                holder.thanksButton.setVisibility(View.VISIBLE);
                holder.thanksCount.setVisibility(View.VISIBLE);

                holder.thanksButton.setImageResource(R.drawable.ic_thumb_up_dark_outline);
                holder.thanksCount.setText(String.valueOf(post.getThanksCount()));
            } else {
                holder.thanksButton.setVisibility(View.VISIBLE);
                holder.thanksButton.setOnClickListener(mThanksClickListener);

                holder.thanksButton.setProgress(0);

                final int drawableRes = post.isThanked()
                        ? R.drawable.ic_thumb_up_dark_outline
                        : R.drawable.ic_thumb_up_dark;
                holder.thanksButton.setImageResource(drawableRes);
                holder.thanksButton.setTag(position);

                holder.thanksCount.setVisibility(View.VISIBLE);
                holder.thanksCount.setText(String.valueOf(post.getThanksCount()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void attachImagesThumbnail(final PostViewHolder holder,
            final ResponseAttachment responseAttachment) {
        final ViewGroup group = (ViewGroup) mLayoutInflater.inflate(R.layout.attachment_image_view,
                holder.attachments, false);
        holder.attachments.addView(group);

        final ImageView imageView = (ImageView) group.findViewById(R.id.img);
        imageView.setOnClickListener(mImageClickListener);

        Picasso.with(mContext)
                .load(responseAttachment.getAttachmentUrl())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        group.getChildAt(1).setVisibility(View.GONE);
                        imageView.setOnClickListener(mImageClickListener);
                    }

                    @Override
                    public void onError() {
                        group.getChildAt(1).setVisibility(View.GONE);
                    }
                });
    }

    public AugmentedPost getPost(final int position) {
        return mPosts.get(position);
    }

    public void addAll(final List<AugmentedPost> forums) {
        if (Utils.isCollectionEmpty(forums)) {
            return;
        }

        final int count = mPosts.size();
        mPosts.addAll(forums);
        notifyItemRangeInserted(count, forums.size());
    }

    public void remove(final int position) {
        if (position < 0 || position >= mPosts.size()) {
            // TODO - maybe throw an exception?
            return;
        }

        mPosts.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        final int count = mPosts.size();
        mPosts.clear();
        notifyItemRangeRemoved(0, count);
    }

    private void attachFiles(PostViewHolder holder, ResponseAttachment a) {
        View group = mLayoutInflater.inflate(R.layout.attachment_file_view, holder.attachments,
                false);
        holder.attachments.addView(group);

        final TextView nameView = (TextView) group.findViewById(R.id.attachment_name);
        final TextView sizeView = (TextView) group.findViewById(R.id.attachment_size);

        LinearLayout attachmentContainer = (LinearLayout) group
                .findViewById(R.id.attachment_container);
        attachmentContainer.setOnClickListener(mDownloadClickListener);
        attachmentContainer.setTag(a);

        nameView.setText(a.getFileName());
        sizeView.setText(a.getFileSize() + " Kb");
    }

    public List<AugmentedPost> getPosts() {
        return Collections.unmodifiableList(mPosts);
    }

    public AugmentedPost[] getPosts(final Collection<Integer> positions) {
        final List<Integer> positionList = new ArrayList<>(positions);
        Collections.sort(positionList);

        final AugmentedPost[] posts = new AugmentedPost[positionList.size()];
        for (int i = 0, positionListSize = positionList.size(); i < positionListSize; i++) {
            int position = positionList.get(i);
            posts[i] = getPost(position);
        }
        return posts;
    }

    public static interface GoToQuoteListener {

        public void onClick(final String postId);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public final ImageView avatarView;

        public final TextView userNameView;

        public final TextView dateView;

        public final LinearLayout postLayout;

        public final ViewGroup attachments;

        public final View actionsLayout;

        public final CircularProgressButton thanksButton;

        public final TextView thanksCount;

        private final ImageView quoteButton;

        private final ImageView multiQuoteButton;

        public PostViewHolder(final View itemView) {
            super(itemView);

            avatarView = (ImageView) itemView.findViewById(R.id.avatar);

            userNameView = (TextView) itemView.findViewById(R.id.user_name);
            dateView = (TextView) itemView.findViewById(R.id.date);
            attachments = (ViewGroup) itemView.findViewById(R.id.attachments);
            postLayout = (LinearLayout) itemView.findViewById(R.id.post_layout);

            actionsLayout = itemView.findViewById(R.id.post_list_item_actions_bar);

            quoteButton = (ImageView) itemView.findViewById(R.id.post_list_item_quote);
            multiQuoteButton = (ImageView) itemView.findViewById(R.id.post_list_item_multi_quote);

            thanksButton = (CircularProgressButton) itemView
                    .findViewById(R.id.post_list_item_thanks);
            thanksCount = (TextView) itemView.findViewById(R.id.post_list_item_thanks_count);
        }
    }
}