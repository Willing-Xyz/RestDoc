package cn.willingxyz.restdoc.swagger.common.utils;

public class StringUtils {
    public static String combineStr(String v1, String v2) {
        if (v1 == null || v1.isEmpty()) return v2;
        if (v2 == null || v2.isEmpty()) return v1;

        if (v1.equals(v2)) return v1;
        return v1 + "; " + v2;
    }
}
