package com.xda.one.ui.helper;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.xda.one.api.inteface.PostClient;
import com.xda.one.api.misc.Consumer;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.api.retrofit.RetrofitPostClient;
import com.xda.one.db.ForumDbHelper;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.ui.ForumFragment;
import com.xda.one.ui.MessagePagerFragment;
import com.xda.one.ui.ThreadFragment;
import com.xda.one.ui.UserProfileActivity;
import com.xda.one.util.FragmentUtils;
import com.xda.one.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UrlParseHelper {

    private static final Pattern PAGE_PATTERN = Pattern.compile("page(\\d+)");

    private static final Pattern THREAD_PATTERN = Pattern.compile("-t(\\d+)");

    public static void parseUrl(final Context context, final Uri uri,
                                final Consumer<Fragment> success, final Runnable failure) {
        final String path = uri.getPath();
        switch (path) {
            case "/showthread.php":
                parseThreadUrl(context, uri, success, failure);
                break;
            case "/private.php":
                parsePrivateUrl(success);
                break;
            case "/member.php":
                parseMemberUrl(context, uri, success);
                break;
            default:
                parsePossibleForumThreadPath(context, uri, success, failure);
                break;
        }
    }

    private static void parseMemberUrl(final Context context, final Uri uri,
                                       final Consumer<Fragment> success) {
        final String userId = uri.getQueryParameter("u");
        success.run(null);

        context.startActivity(UserProfileActivity.createIntent(context, userId));
    }

    private static void parsePossibleForumThreadPath(final Context context, final Uri uri,
                                                     final Consumer<Fragment> success, final Runnable failure) {
        final List<String> segments = uri.getPathSegments();
        if (Utils.isCollectionEmpty(segments)) {
            failure.run();
            return;
        }

        // First we check if the last segment matches a page
        final String lastSegment = segments.get(segments.size() - 1);
        final Matcher pageMatcher = PAGE_PATTERN.matcher(lastSegment);
        if (pageMatcher.matches()) {
            // If it matches a page, the end of the penultimate segment should contain the threadId
            final String penultimateSegment = segments.get(segments.size() - 2);
            final Matcher threadIdMatcher = THREAD_PATTERN.matcher(penultimateSegment);

            if (threadIdMatcher.find()) {
                // So we found the threadId - look up the thread and switch to it
                final String threadId = threadIdMatcher.group(1);
                final int threadPage = Integer.parseInt(pageMatcher.group(1));
                getPostFeed(context, success, failure, threadId, threadPage);
            } else {
                // If we have a page but not a thread, it's bad news
                failure.run();
            }
            return;
        }

        // Now we try to see if we can match the last segment to a threadId directly without a page
        final Matcher threadIdMatcher = THREAD_PATTERN.matcher(lastSegment);
        if (threadIdMatcher.find()) {
            // Since we found it, we can go ahead and load the thread
            final String threadId = threadIdMatcher.group(1);
            getPostFeed(context, success, failure, threadId, 1);
            return;
        }

        // The only other case we need to deal with the forum path case
        // TODO - there are more possibilities which are ignored
        final ForumDbHelper helper = ForumDbHelper.getInstance(context);

        Forum parent = null;
        Forum forum = null;
        int parentId = -1;
        for (final String segment : segments) {
            parent = forum;
            forum = helper.searchSlug(parentId, segment);
            if (forum == null) {
                // Fail if the forum is null at any point
                failure.run();
                return;
            }
            parentId = forum.getForumId();
        }
        if (forum == null) {
            failure.run();
        } else {
            if (forum.hasChildren()) {
                final ForumFragment instance = ForumFragment.createInstance(forum, null,
                        new ArrayList<String>());
                success.run(instance);
            } else {
                final String parentTitle = parent == null ? null : parent.getTitle();
                final ThreadFragment instance = ThreadFragment
                        .createDefault(forum.getForumId(),
                                forum.getTitle(), parentTitle, new ArrayList<String>());
                success.run(instance);
            }
        }
    }

    private static void parsePrivateUrl(final Consumer<Fragment> success) {
        success.run(new MessagePagerFragment());
    }

    private static void parseThreadUrl(final Context context, final Uri uri,
                                       final Consumer<Fragment> success, final Runnable failure) {
        final String threadId = uri.getQueryParameter("t");
        final String pageString = uri.getQueryParameter("page");
        final int page = pageString == null ? 1 : Integer.parseInt(pageString);

        getPostFeed(context, success, failure, threadId, page);
    }

    private static void getPostFeed(final Context context, final Consumer<Fragment> success,
                                    final Runnable failure, final String threadId, final int page) {
        final PostClient userClient = RetrofitPostClient.getClient(context);
        userClient.getPostsAsync(threadId, page, new Callback<ResponsePostContainer>() {
            @Override
            public void success(final ResponsePostContainer container, final Response response) {
                final AugmentedUnifiedThread thread = new AugmentedUnifiedThread(container
                        .getThread(), context);
                Fragment fragment = FragmentUtils.switchToPostList(thread, new ArrayList<String>(),
                        container);
                success.run(fragment);
            }

            @Override
            public void failure(final RetrofitError error) {
                failure.run();
            }
        });
    }
}