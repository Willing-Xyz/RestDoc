package com.willing.restdoc.core.parse.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlEncodeUtils {
    public static String encode(String src)
    {
//        try {
//            return URLEncoder.encode(src, "UTF-8");
            return src;
//             todo
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }
    }
}
