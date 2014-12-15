package com.xda.one.constants;

import android.util.Base64;

public class XDAConstants {
    public static final String RECAPTCHA_PUBLIC_KEY = "6LcVyfcSAAAAAG1QaNlcl6o84brVL9sCVF707V8Q";

    public static final String XDA_FORUM_URL = "http://forum.xda-developers.com";

    public static final String XDA_API_URL = "https://api.xda-developers.com";

    public static final String ENDPOINT_URL = XDA_API_URL + "/v1";

    public static final String USERPASSWORD = "admin" + ":" + "bUnew47ra6reJuph";

    public static final String ENCODED_AUTHORIZATION = Base64
            .encodeToString(USERPASSWORD.getBytes(), Base64.NO_WRAP);

    public static final String XDA_NEWS_URL = "http://www.xda-developers.com/";
}