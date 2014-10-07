package com.xda.one.util;

import com.xda.one.auth.XDAAccount;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class AccountUtils {

    private static final String SELECTED_ACCOUNT_USERNAME = "selected_account_username";

    private static final String SELECTED_ACCOUNT_USERID = "selected_account_userid";

    private static final String SELECTED_ACCOUNT_EMAIL = "selected_account_email";

    private static final String SELECTED_ACCOUNT_AVATAR = "selected_account_avatar";

    private static final String SELECTED_ACCOUNT_PM_COUNT = "selected_account_pm_count";

    private static final String SELECTED_ACCOUNT_QUOTE_COUNT = "selected_account_quote_count";

    private static final String SELECTED_ACCOUNT_MENTION_COUNT = "selected_accont_mention_count";

    private static final String SELECTED_ACCOUNT_TOKEN = "selected_account_token";

    public static boolean isAccountAvailable(final Context context) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        return sharedPreferences.getString(SELECTED_ACCOUNT_USERNAME, null) != null;
    }

    public static XDAAccount getAccount(final Context context) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        final String username = sharedPreferences.getString(SELECTED_ACCOUNT_USERNAME, null);
        final String userId = sharedPreferences.getString(SELECTED_ACCOUNT_USERID, null);
        final String email = sharedPreferences.getString(SELECTED_ACCOUNT_EMAIL, null);
        final String avatar = sharedPreferences.getString(SELECTED_ACCOUNT_AVATAR, null);
        final int pmCount = sharedPreferences.getInt(SELECTED_ACCOUNT_PM_COUNT, 0);
        final int quoteCount = sharedPreferences.getInt(SELECTED_ACCOUNT_QUOTE_COUNT, 0);
        final int mentionCount = sharedPreferences.getInt(SELECTED_ACCOUNT_MENTION_COUNT, 0);
        final String token = sharedPreferences.getString(SELECTED_ACCOUNT_TOKEN, null);

        return TextUtils.isEmpty(username) ? null : new XDAAccount(username, userId, email,
                avatar, pmCount, quoteCount, mentionCount, token);
    }

    public static void storeAccount(final Context context, final XDAAccount account) {
        final SharedPreferences sharedPreferences = getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if (account == null) {
            editor.remove(SELECTED_ACCOUNT_USERNAME).remove(SELECTED_ACCOUNT_USERID)
                    .remove(SELECTED_ACCOUNT_EMAIL).remove(SELECTED_ACCOUNT_AVATAR)
                    .remove(SELECTED_ACCOUNT_PM_COUNT).remove(SELECTED_ACCOUNT_QUOTE_COUNT)
                    .remove(SELECTED_ACCOUNT_MENTION_COUNT).remove(SELECTED_ACCOUNT_TOKEN);
        } else {
            editor.putString(SELECTED_ACCOUNT_USERNAME, account.getUserName())
                    .putString(SELECTED_ACCOUNT_USERID, account.getUserId())
                    .putString(SELECTED_ACCOUNT_EMAIL, account.getEmail())
                    .putString(SELECTED_ACCOUNT_AVATAR, account.getAvatarUrl())
                    .putInt(SELECTED_ACCOUNT_PM_COUNT, account.getPmCount())
                    .putInt(SELECTED_ACCOUNT_QUOTE_COUNT, account.getQuoteCount())
                    .putInt(SELECTED_ACCOUNT_MENTION_COUNT, account.getMentionCount())
                    .putString(SELECTED_ACCOUNT_TOKEN, account.getAuthToken());
        }

        editor.apply();
    }
}