package com.xda.one.api.model.interfaces.container;

import com.xda.one.api.model.interfaces.UnifiedThread;

import java.util.List;

public interface UnifiedThreadContainer {

    public List<? extends UnifiedThread> getThreads();

    public int getTotalPages();

    public int getPerPage();

    public int getCurrentPage();
}