package com.willing.springswagger.parse;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.CommentFormatter;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import lombok.var;

/**
 * class 呵呵呵呵 < 10 >  &&&&&&&&&&&&&&&
 */
public class DocTest {
    /**
     * 方法1111111111
     * @param str 参数1111111111111
     * @return 返回值。。。。。。。。
     */
    public int method1(String str)
    {
        return 1;
    }

    public static void main(String[] args) {
        ClassJavadoc classDoc = RuntimeJavadoc.getJavadoc("com.willing.springswagger.parse.DocTest");
        var formatter = new CommentFormatter();
        System.out.println(formatter.format(classDoc.getComment()));
    }
}
