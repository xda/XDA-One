package com.xda.one.util;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.parser.TextDataStructure;
import com.xda.one.ui.PostAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class SectionUtils {

    public static void setupSections(final Context context, final LayoutInflater inflater,
            final ViewGroup layout, final TextDataStructure structure,
            final PostAdapter.GoToQuoteListener quoteListener) {
        final List<TextDataStructure.Section> sections = structure.getSections();
        for (int i = 0, sectionsSize = sections.size(); i < sectionsSize; i++) {
            TextDataStructure.Section section = sections.get(i);
            switch (section.getType()) {
                case NORMAL:
                    setupNormalSection(context, inflater, layout, section);
                    break;
                case QUOTE:
                    final List<TextDataStructure.Section> skip = new ArrayList<>();
                    i = forwardUntilNonEmbed(i, sections, skip);
                    setupQuoteSection(context, inflater, layout, skip, section,
                            quoteListener);
                    break;
            }
        }
    }

    private static int forwardUntilNonEmbed(final int i,
            final List<TextDataStructure.Section> sections,
            final List<TextDataStructure.Section> skip) {
        final int size = sections.size();
        for (int j = i; j < size; j++) {
            final TextDataStructure.Section section = sections.get(j);
            if (!section.isEmbedded()) {
                return j;
            }
            skip.add(section);
        }
        throw new IllegalArgumentException("Last item cannot itself be embedded");
    }

    // TODO - this code could crash if there are more than 12 elements inside the view - fix that
    private static void setupNormalSection(final Context context, final LayoutInflater inflater,
            final ViewGroup postLayout, final TextDataStructure.Section section) {
        final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.post_list_section,
                postLayout, false);
        for (final TextDataStructure.Item item : section.getItems()) {
            // Ignore any empty sections
            if (TextUtils.isEmpty(item.getId())) {
                return;
            }

            switch (item.getType()) {
                case IMAGE:
                    view.addView(getImageView(context, item.getId().toString()));
                    break;
                case TEXT:
                    view.addView(getNormalPostView(context, item.getId()));
                    break;
            }
        }
        postLayout.addView(view);
    }

    // TODO - this code could crash if there are more than 12 elements inside the view - fix that
    private static void setupQuoteSection(final Context context,
            final LayoutInflater layoutInflater, final ViewGroup postLayout,
            final List<TextDataStructure.Section> skip,
            final TextDataStructure.Section section,
            final PostAdapter.GoToQuoteListener quoteListener) {
        final ViewGroup view = (ViewGroup) layoutInflater.inflate(R.layout.quote_post_list_section,
                postLayout, false);
        if (!skip.isEmpty()) {
            final TextDataStructure.Section removed = skip.remove(0);
            setupQuoteSection(context, layoutInflater, view, skip, removed, quoteListener);
        }

        final Pair<String, String> usernamePostIdPair = PostUtils.parseQuoteUsernamePostid(section
                .getUsernamePostId());

        final TextView textView = (TextView) view.findViewById(R.id.quote_list_item_header);
        final SpannableStringBuilder builder = new SpannableStringBuilder("Quote");
        if (usernamePostIdPair != null) {
            if (usernamePostIdPair.first != null) {
                final int prefixLength = builder.length();
                builder.append(" originally posted by ").append(usernamePostIdPair.first);
                builder.setSpan(new StyleSpan(Typeface.BOLD), prefixLength, builder.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (usernamePostIdPair.second != null) {
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        quoteListener.onClick(usernamePostIdPair.second);
                    }
                });
            }
        }
        builder.append(":");
        textView.setText(builder);

        for (final TextDataStructure.Item item : section.getItems()) {
            // Ignore any empty sections
            if (TextUtils.isEmpty(item.getId())) {
                return;
            }

            switch (item.getType()) {
                case IMAGE:
                    view.addView(getImageView(context, item.getId().toString()));
                    break;
                case TEXT:
                    view.addView(getQuotePostView(context, item.getId()));
                    break;
            }
        }
        postLayout.addView(view);
    }

    private static View getImageView(final Context context, final String source) {
        final ImageView imageView = new ImageView(context);
        Picasso.with(context)
                .load(source)
                .into(imageView);
        imageView.setClickable(true);
        return imageView;
    }

    private static View getQuotePostView(final Context context, final CharSequence charSequence) {
        final TextView view = new TextView(context);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(charSequence);
        view.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        view.setTextColor(Color.DKGRAY);
        // view.setTextSize(Dimension.UNIT_SP, 13);
        view.setPadding(16, 16, 16, 16);
        return view;
    }

    private static TextView getNormalPostView(final Context context,
            final CharSequence charSequence) {
        final TextView view = new TextView(context);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(charSequence);
        view.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        view.setTextColor(Color.BLACK);
        // view.setTextSize(Dimension.UNIT_SP, 13);
        return view;
    }
}
