package com.willing.springswagger.parse.utils;

import com.github.therapi.runtimejavadoc.Comment;
import com.github.therapi.runtimejavadoc.CommentFormatter;

public class FormatUtils {
    private static CommentFormatter _formatter = new CommentFormatter();
    public static String format(Comment comment)
    {
        return _formatter.format(comment);
    }
}
