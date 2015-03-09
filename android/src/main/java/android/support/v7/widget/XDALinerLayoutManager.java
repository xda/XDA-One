package android.support.v7.widget;

import android.content.Context;

public class XDALinerLayoutManager extends LinearLayoutManager {

    private boolean mListEnd = true;

    public XDALinerLayoutManager(final Context context) {
        super(context);
    }

    public XDALinerLayoutManager(final Context context, final int orientation,
            final boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    int scrollBy(final int dy, final RecyclerView.Recycler recycler,
            final RecyclerView.State state) {
        int scrolled = super.scrollBy(dy, recycler, state);
        mListEnd = dy > 0 && dy > scrolled;
        return scrolled;
    }

    public boolean isListEnd() {
        return mListEnd;
    }
}