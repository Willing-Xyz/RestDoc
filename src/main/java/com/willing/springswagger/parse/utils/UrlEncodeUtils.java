package com.willing.springswagger.parse.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlEncodeUtils {
    public static String encode(String src)
    {
        try {
            return URLEncoder.encode(src, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
