package com.xda.one.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("StringUtil cannot be instantiated");
    }

    public static String removeWhiteSpaces(String str) {
        return str.replaceAll("\\s+", " ");
    }

    public static String trimCharSequence(final String text, final int size) {
        return text.length() > size ? text.substring(0, size) : text;
    }

    public static Spanned trimCharSequence(final Spanned spanned, final int size) {
        return spanned.length() > size ? new SpannableStringBuilder(spanned, 0, size) : spanned;
    }
}