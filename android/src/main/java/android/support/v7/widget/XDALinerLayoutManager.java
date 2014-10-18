package android.support.v7.widget;/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific languag`e governing permissions and
 * limitations under the License.
 */

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