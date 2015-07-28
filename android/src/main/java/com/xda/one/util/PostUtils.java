package com.xda.one.util;

import android.text.TextUtils;
import android.util.Pair;

import com.xda.one.model.augmented.AugmentedPost;
import com.xda.one.parser.TextDataStructure;

public class PostUtils {

    public static String quotePost(final AugmentedPost augmentedPost) {
        return String.format("[QUOTE=%s;%s]%s[/QUOTE]\n\n",
                augmentedPost.getUserName(), augmentedPost.getUserId(),
                augmentedPost.getCreatedText());
    }

    public static String getCreatedText(final TextDataStructure textDataStructure,
                                        final int maxLimit) {
        final StringBuilder builder = new StringBuilder();
        for (final TextDataStructure.Section section : textDataStructure.getSections()) {
            if (section.getType() != TextDataStructure.SectionType.NORMAL) {
                continue;
            }
            for (TextDataStructure.Item item : section.getItems()) {
                if (item.getType() != TextDataStructure.ItemType.TEXT) {
                    continue;
                }
                builder.append(item.getId());
                if (builder.length() >= maxLimit) {
                    return StringUtils.trimCharSequence(builder.toString(), maxLimit);
                }
            }
        }
        return builder.toString();
    }

    public static Pair<String, String> parseQuoteUsernamePostid(final String usernamePostId) {
        if (TextUtils.isEmpty(usernamePostId)) {
            return null;
        }
        final String[] split = usernamePostId.split(";");
        if (split.length == 0) {
            return null;
        } else if (split.length == 1) {
            return new Pair<>(split[0], null);
        }
        return new Pair<>(split[0], split[1]);
    }
}