package com.xda.one.model.augmented.container;

import android.content.Context;

import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.interfaces.container.UnifiedThreadContainer;
import com.xda.one.model.augmented.AugmentedUnifiedThread;

import java.util.ArrayList;
import java.util.List;

public class AugmentedUnifiedThreadContainer implements UnifiedThreadContainer {

    private final UnifiedThreadContainer mUnifiedThreadContainer;

    private final List<AugmentedUnifiedThread> mAugmentedThreads;

    public AugmentedUnifiedThreadContainer(final UnifiedThreadContainer container,
                                           final Context context) {
        mUnifiedThreadContainer = container;
        mAugmentedThreads = new ArrayList<>(mUnifiedThreadContainer.getThreads().size());

        // Augment threads
        for (final UnifiedThread thread : container.getThreads()) {
            mAugmentedThreads.add(new AugmentedUnifiedThread(thread, context));
        }
    }

    @Override
    public List<AugmentedUnifiedThread> getThreads() {
        return mAugmentedThreads;
    }

    @Override
    public int getTotalPages() {
        return mUnifiedThreadContainer.getTotalPages();
    }

    @Override
    public int getPerPage() {
        return mUnifiedThreadContainer.getPerPage();
    }

    @Override
    public int getCurrentPage() {
        return mUnifiedThreadContainer.getCurrentPage();
    }
}