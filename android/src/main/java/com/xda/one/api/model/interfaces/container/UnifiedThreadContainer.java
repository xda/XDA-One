package com.xda.one.api.model.interfaces.container;

import com.xda.one.api.model.interfaces.UnifiedThread;

import java.util.List;

public interface UnifiedThreadContainer {

    List<? extends UnifiedThread> getThreads();

    int getTotalPages();

    int getPerPage();

    int getCurrentPage();
}