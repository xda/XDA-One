package com.xda.one.model.augmented.container;

import com.xda.one.R;
import com.xda.one.api.model.interfaces.Mention;
import com.xda.one.api.model.interfaces.container.MentionContainer;
import com.xda.one.model.augmented.AugmentedMention;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AugmentedMentionContainer implements MentionContainer {

    private final MentionContainer mMentionContainer;

    private final List<AugmentedMention> mMentions;

    public AugmentedMentionContainer(final MentionContainer container, final Context context) {
        mMentionContainer = container;
        mMentions = new ArrayList<>();

        final int primary = context.getResources().getColor(R.color.default_primary_text);
        final int secondary = context.getResources().getColor(R.color.default_secondary_text);
        for (final Mention quote : container.getMentions()) {
            mMentions.add(new AugmentedMention(quote, context, primary, secondary));
        }
    }

    @Override
    public List<AugmentedMention> getMentions() {
        return mMentions;
    }

    @Override
    public int getTotalPages() {
        return mMentionContainer.getTotalPages();
    }

    @Override
    public int getPerPage() {
        return mMentionContainer.getPerPage();
    }

    @Override
    public int getCurrentPage() {
        return mMentionContainer.getCurrentPage();
    }
}