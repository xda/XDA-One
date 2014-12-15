package com.xda.one.auth;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ReCaptcha {

    private static final String CHALLENGE_URL
            = "http://www.googleplus.com/recaptcha/api/challenge?k=%s";

    private static final String RECAPTCHA_OBJECT_TOKEN_URL
            = "http://www.googleplus.com/recaptcha/api/reload?c=%s&k=%s&type=%s";

    private static final String IMAGE_URL = "http://www.googleplus.com/recaptcha/api/image?c=%s";

    private final String mPublicKey;

    private String mChallengeKey;

    private String mImageToken;

    public ReCaptcha(final String publicKey) {
        mPublicKey = publicKey;
    }

    private static String substringBetweenStrings(final String str, String open, String close) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(open) || TextUtils.isEmpty(close)) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    private static String getImageToken(final String challenge, final String publicKey)
            throws IOException {
        final HttpClient httpClient = createHttpClient();
        final String imageTokenUrl = String.format(ReCaptcha.RECAPTCHA_OBJECT_TOKEN_URL,
                challenge, publicKey, "image");
        final String imageTokenResponse = httpClient.execute(new HttpGet(imageTokenUrl),
                new BasicResponseHandler());
        return substringBetweenStrings(imageTokenResponse, "('", "',");

    }

    private static HttpClient createHttpClient() {
        return new DefaultHttpClient();
    }

    public final void showImageChallenge(final ImageView imageView) {
        mImageToken = null;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(final Void... voids) {
                try {
                    return downloadImage();
                } catch (final IOException | JSONException e) {
                    Log.e("ReCaptcha", "An error has occurred", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(final String s) {
                final String imageUrl = String.format(ReCaptcha.IMAGE_URL, mImageToken);
                Picasso.with(imageView.getContext()).load(imageUrl).into(imageView);
            }
        }.execute();
    }

    private String downloadImage() throws IOException, JSONException {
        final String challenge = getChallenge();
        if (challenge == null) {
            throw new IOException("ReCaptcha challenge not found");
        }

        mImageToken = getImageToken(challenge, mPublicKey);
        if (mImageToken == null) {
            throw new IOException("Image token not found");
        }
        return mImageToken;
    }

    private String getChallenge() throws IOException, JSONException {
        if (TextUtils.isEmpty(mChallengeKey)) {
            final HttpClient httpClient = createHttpClient();
            final String url = String.format(CHALLENGE_URL, mPublicKey);
            final String challenegeResponse = httpClient.execute(new HttpGet(url),
                    new BasicResponseHandler());
            final String recaptchaStateString = substringBetweenStrings(challenegeResponse,
                    "RecaptchaState = ", "}") + "}";

            final JSONObject node = new JSONObject(recaptchaStateString);
            mChallengeKey = node.getString("challenge");

            return mChallengeKey;
        }
        return mChallengeKey;
    }

    public String getImageToken() {
        return mImageToken;
    }
}