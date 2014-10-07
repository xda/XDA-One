package com.xda.one.parser;

import com.xda.one.ui.UserProfileActivity;

import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.View;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class XDATagHandlers {

    public static class ImageHandler extends TagNodeHandler {

        @Override
        public void handleTagNode(final TagNode node, final SpannableStringBuilder builder,
                final int start, final int end, final SpanStack stack) {
            final String src = node.getAttributeByName("src");
            final int newStart = builder.length();
            builder.append("\uFFFC");
            stack.pushSpan(new ImageSpan(src), newStart, builder.length());
        }

        public static class ImageSpan {

            private final String mSrc;

            public ImageSpan(final String src) {
                mSrc = src;
            }

            public String getSrc() {
                return mSrc;
            }
        }
    }

    public static class MentionHandler extends TagNodeHandler {

        @Override
        public void handleTagNode(final TagNode node, final SpannableStringBuilder builder,
                final int start, final int end, final SpanStack spanStack) {
            final String userId = node.getAttributeByName("user");
            final MentionSpan mentionSpan = new MentionSpan(userId);
            builder.setSpan(mentionSpan, start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        public static class MentionSpan extends ClickableSpan {

            private final String mUserId;

            public MentionSpan(final String userId) {
                this.mUserId = userId;
            }

            @Override
            public void onClick(final View widget) {
                final Context context = widget.getContext();

                final Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra(UserProfileActivity.USER_ID_ARGUMENT, mUserId);
                context.startActivity(intent);
            }
        }
    }

    public static class QuoteTagHandler extends TagNodeHandler {

        @Override
        public void handleTagNode(final TagNode node, final SpannableStringBuilder builder,
                final int start, final int end, final SpanStack spanStack) {
            final String userId = node.getAttributeByName("user");
            builder.setSpan(new QuoteSpan(userId), start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        public static class QuoteSpan {

            private final String mUserId;

            public QuoteSpan(final String userId) {
                mUserId = userId;
            }

            public String getUserId() {
                return mUserId;
            }
        }
    }
}