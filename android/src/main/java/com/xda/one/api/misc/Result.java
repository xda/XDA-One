package com.xda.one.api.misc;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import retrofit.client.Response;

import static com.xda.one.api.retrofit.RetrofitClient.OBJECT_MAPPER;

public class Result {

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private boolean isFromGoogle;
    private boolean mSuccess;

    private String mMessage;

    public Result(final boolean success) {
        mSuccess = success;
    }

    public Result(final String message) {
        mMessage = message;
    }

    public static Result parseResultFromResponse(final Response response) {
        InputStream inputStream = null;
        try {
            inputStream = response.getBody().in();
            final String output = IOUtils.toString(inputStream);
            return Result.parseResultFromString(output);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    public static Result parseResultFromString(final String line) {
        if (line == null) {
            return null;
        }

        final Result result;
        try {
            final JsonNode error = OBJECT_MAPPER.readTree(line).get(ERROR);

            if (error == null) {
                result = new Result(true);
            } else {
                result = new Result(error.get(MESSAGE).asText());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isSuccess(Result result) {
        return result != null && result.isSuccess();
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMessage() {
        return mMessage;
    }


    public boolean isFromGoogle() {
        return isFromGoogle;
    }

    public void setFromGoogle(boolean isFromGoogle) {
        this.isFromGoogle = isFromGoogle;
    }
}