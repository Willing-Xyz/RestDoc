package cn.willingxyz.restdoc.core.parse.utils;

public class TextUtils {
    public static String getFirstLine(String content)
    {
        if (content != null) {
            content = content.trim();
            int index = content.indexOf('\n');
            if (index > 0) {
                return content.substring(0, index);
            }
            else
            {
                return content;
            }
        }
        return "";
    }


}
