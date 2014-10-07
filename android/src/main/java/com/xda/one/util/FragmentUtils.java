package com.xda.one.util;

import com.xda.one.R;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.ui.ForumFragment;
import com.xda.one.ui.PostPagerFragment;
import com.xda.one.ui.ThreadFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FragmentUtils {

    public static FragmentTransaction getDefaultTransaction(final FragmentManager fragmentManager) {
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right);
        return transaction;
    }

    public static void switchToForumContent(final FragmentManager parentManager,
            final Fragment parent, final List<String> hierarchyList, final String forumTitle,
            final Forum forum) {
        final ArrayList<String> hierarchy = new ArrayList<>(hierarchyList);
        hierarchy.add(forum.getTitle());

        // There are three possibilities - forums and threads in this forum,
        // just threads or just forums
        if (forum.canContainThreads() && forum.hasChildren()) {
            // TODO - this dual setup needs to be written
        } else if (forum.hasChildren()) {
            switchToForumList(parent, parentManager, hierarchy, forumTitle, forum);
        } else if (forum.canContainThreads()) {
            switchToThreadList(parentManager, forum, forumTitle, hierarchy);
        } else {
            // This is invalid and indicates a bug
            Log.e("XDA-One", "There's a bug here - forums should have either threads or forums or"
                    + " both but not nothing");
        }
    }

    private static void switchToForumList(final Fragment parent,
            final FragmentManager parentManager, final ArrayList<String> hierarchy,
            final String forumTitle, final Forum forum) {
        final FragmentManager fragmentManager = parent == null
                ? parentManager
                : parent.getFragmentManager();

        final FragmentTransaction transaction = getDefaultTransaction(fragmentManager);
        if (TextUtils.isEmpty(forumTitle)) {
            transaction.addToBackStack(null);
        } else {
            final String title = forum.getTitle();
            transaction.addToBackStack(title);
        }
        final Fragment fragment = ForumFragment.createInstance(forum, forumTitle, hierarchy);
        transaction.replace(R.id.content_frame, fragment).commit();
    }

    public static void switchToThreadList(final FragmentManager fragmentManager,
            final Forum forum, final String parentTitle, final ArrayList<String> hierarchy) {
        final FragmentTransaction transaction = getDefaultTransaction(fragmentManager);
        transaction.addToBackStack(forum.getTitle());
        final Fragment fragment = ThreadFragment.createInstance(forum.getForumId(),
                forum.getTitle(), parentTitle, hierarchy);
        transaction.replace(R.id.content_frame, fragment).commit();
    }

    public static Fragment switchToPostList(final AugmentedUnifiedThread unifiedThread,
            final ArrayList<String> hierarchy) {
        return switchToPostList(unifiedThread, hierarchy, null);
    }

    public static Fragment switchToPostList(final AugmentedUnifiedThread unifiedThread,
            final ArrayList<String> hierarchy, final ResponsePostContainer container) {
        int pageCount;
        if (container == null) {
            pageCount = unifiedThread.getTotalPosts() / 10;
            if (unifiedThread.getTotalPosts() % 10 != 0) {
                pageCount += 1;
            }
        } else {
            pageCount = container.getTotalPages();
        }
        hierarchy.add(unifiedThread.getTitle());
        return PostPagerFragment.getInstance(unifiedThread, container, pageCount, hierarchy);
    }
}