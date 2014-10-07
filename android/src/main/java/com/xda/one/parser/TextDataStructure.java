package com.xda.one.parser;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextDataStructure {

    private final ArrayList<Section> mSections;

    public TextDataStructure(final Spanned spanned) {
        mSections = new ArrayList<>();

        if (TextUtils.isEmpty(spanned)) {
            return;
        }
        setupAllSections(spanned);
    }

    private void setupAllSections(final Spanned spanned) {
        final XDATagHandlers.QuoteTagHandler.QuoteSpan[] quoteSpans = spanned.getSpans(0,
                spanned.length(), XDATagHandlers.QuoteTagHandler.QuoteSpan.class);
        int position = 0;
        for (XDATagHandlers.QuoteTagHandler.QuoteSpan span : quoteSpans) {
            int start = spanned.getSpanStart(span);
            if (position < start) {
                setupNormalSection(new SpannableStringBuilder(spanned, position, start));
            } else if (position > start) {
                // In this case this item is the parent of the previous quote span
                final Section previous = mSections.get(mSections.size() - 1);
                previous.setEmbedded(true);
                start = position;
            }
            position = spanned.getSpanEnd(span);
            setupQuoteSection(new SpannableStringBuilder(spanned, start, position),
                    span.getUserId());
        }
        if (position < spanned.length()) {
            setupNormalSection(new SpannableStringBuilder(spanned, position, spanned.length()));
        }
    }

    private void setupNormalSection(final Spanned spanned) {
        final Section section = new Section(SectionType.NORMAL);
        setupImageSections(section, spanned, new Callback() {
            @Override
            public void setupOther(int start, int end) {
                final Text text = new Text(new SpannableStringBuilder(spanned, start, end));
                section.addItem(text);
            }
        });
        mSections.add(section);
    }

    private void setupQuoteSection(final Spanned spanned, final String userId) {
        final Section section = new Section(SectionType.QUOTE);
        section.setUserId(userId);
        setupImageSections(section, spanned, new Callback() {
            @Override
            public void setupOther(int start, int end) {
                final Text text = new Text(new SpannableStringBuilder(spanned, start, end));
                section.addItem(text);
            }
        });
        mSections.add(section);
    }

    private void setupImageSections(final Section section, final Spanned spanned,
            final Callback callback) {
        final XDATagHandlers.ImageHandler.ImageSpan[] imageSpans = spanned.getSpans(0,
                spanned.length(), XDATagHandlers.ImageHandler.ImageSpan.class);
        int position = 0;
        for (int i = imageSpans.length - 1; i >= 0; i--) {
            final XDATagHandlers.ImageHandler.ImageSpan span = imageSpans[i];
            final int start = spanned.getSpanStart(span);
            if (position < start) {
                callback.setupOther(position, start);
            }
            final Image image = new Image(span.getSrc());
            section.addItem(image);

            position = spanned.getSpanEnd(span);
        }
        if (position < spanned.length()) {
            callback.setupOther(position, spanned.length());
        }
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(mSections);
    }

    public enum ItemType {
        TEXT,
        IMAGE
    }

    public enum SectionType {
        NORMAL,
        QUOTE
    }

    public interface Item {

        public ItemType getType();

        public CharSequence getId();
    }

    private interface Callback {

        public void setupOther(int start, int end);
    }

    public class Section {

        private final SectionType mType;

        private final List<Item> mItems;

        private boolean mEmbedded;

        private String mUserId;

        public Section(final SectionType type) {
            mType = type;
            mItems = new ArrayList<>();
        }

        public SectionType getType() {
            return mType;
        }

        public List<Item> getItems() {
            return mItems;
        }

        private void addItem(final Item image) {
            mItems.add(image);
        }

        public boolean isEmbedded() {
            return mEmbedded;
        }

        public void setEmbedded(boolean embedded) {
            mEmbedded = embedded;
        }

        public void setUserId(final String userId) {
            mUserId = userId;
        }

        public String getUsernamePostId() {
            return mUserId;
        }
    }

    public class Text implements Item {

        private final Spanned mText;

        public Text(final Spanned text) {
            mText = text;
        }

        @Override
        public ItemType getType() {
            return ItemType.TEXT;
        }

        @Override
        public CharSequence getId() {
            return mText;
        }
    }

    public class Image implements Item {

        private final String mSource;

        public Image(final String source) {
            mSource = source;
        }

        @Override
        public ItemType getType() {
            return ItemType.IMAGE;
        }

        @Override
        public CharSequence getId() {
            return mSource;
        }
    }
}