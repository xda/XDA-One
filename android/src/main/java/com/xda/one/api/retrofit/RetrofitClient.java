package com.xda.one.api.retrofit;

import android.content.Context;
import android.net.http.HttpResponseCache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xda.one.constants.XDAConstants;

import java.io.File;
import java.io.IOException;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class RetrofitClient {

    public static final String FORCE_RELOAD = "force_reload";

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final JacksonConverter JACKSON_CONVERTER
            = new JacksonConverter(OBJECT_MAPPER);

    private static String sAuthToken;

    private static boolean sResponseCache = false;

    public static RestAdapter.Builder getRestBuilder(final Context context, final String url) {
        if (!sResponseCache) {
            try {
                final File httpCacheDir = new File(context.getCacheDir(), "http");
                final long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
                HttpResponseCache.install(httpCacheDir, httpCacheSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sResponseCache = true;
        }

        return new RestAdapter.Builder()
                .setEndpoint(url)
                .setConverter(JACKSON_CONVERTER)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization",
                                "Basic " + XDAConstants.ENCODED_AUTHORIZATION);
                    }
                });
    }

    public static String getAuthToken() {
        return sAuthToken;
    }

    public static void setAuthToken(final String authToken) {
        sAuthToken = authToken;
    }
}
