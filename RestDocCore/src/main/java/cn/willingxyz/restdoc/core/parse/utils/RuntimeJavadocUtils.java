package cn.willingxyz.restdoc.core.parse.utils;

import com.github.therapi.runtimejavadoc.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RuntimeJavadocUtils {
    public static List<MethodJavadoc> getAllMethodJavadoc(Class clazz)
    {
        List<MethodJavadoc> methods = new ArrayList<>();

        while (clazz != Object.class)
        {
            ClassJavadoc classJavadoc = getClassJavadoc(clazz);

            methods.addAll(classJavadoc.getMethods());

            clazz = clazz.getSuperclass();
        }

        return methods;
    }

    public static ClassJavadoc getClassJavadoc(Class clazz)
    {
        return RuntimeJavadoc.getJavadoc(clazz);
    }

    public static OtherJavadoc getTag(Class clazz, String tagName)
    {
        ClassJavadoc classJavadoc = RuntimeJavadoc.getJavadoc(clazz);
        if (classJavadoc.getOther() != null)
        {
            OtherJavadoc otherJavadoc = classJavadoc.getOther().stream()
                    .filter(o -> o.getName().trim().equals(tagName))
                    .findFirst().orElse(null);
            if (otherJavadoc != null)
                return otherJavadoc;
        }
        return null;
    }

    public static OtherJavadoc getTag(Method method, String tagName)
    {
        MethodJavadoc methodJavadoc = RuntimeJavadoc.getJavadoc(method);
        if (methodJavadoc.getOther() != null)
        {
            OtherJavadoc otherJavadoc = methodJavadoc.getOther().stream()
                    .filter(o -> o.getName().trim().equals(tagName))
                    .findFirst().orElse(null);
            if (otherJavadoc != null)
                return otherJavadoc;
        }
        return null;
    }

    public static OtherJavadoc getTag(Field field, String tagName)
    {
        FieldJavadoc fieldJavadoc = RuntimeJavadoc.getJavadoc(field);
        if (fieldJavadoc.getOther() != null)
        {
            OtherJavadoc otherJavadoc = fieldJavadoc.getOther().stream()
                    .filter(o -> o.getName().trim().equals(tagName))
                    .findFirst().orElse(null);
            if (otherJavadoc != null)
                return otherJavadoc;
        }
        return null;
    }

    public static String getTagComment(Method method, String tagName, String paramName)
    {
        MethodJavadoc methodJavadoc = RuntimeJavadoc.getJavadoc(method);
        if (methodJavadoc.getOther() != null)
        {
            return methodJavadoc.getOther().stream()
                    .filter(o -> o.getName().trim().equals(tagName))
                    .map(o -> FormatUtils.format(o.getComment()).trim())
                    .filter(o -> o.startsWith(paramName + " "))
                    .map(o -> o.substring(paramName.length() + 1))
                    .findFirst().orElse(null);
        }
        return null;
    }

    public static String getTagComment(Field field, String tagName)
    {
        OtherJavadoc otherJavadoc = getTag(field, tagName);
        if (otherJavadoc == null)
            return null;
        return FormatUtils.format(otherJavadoc.getComment());
    }
    public static String getTagComment(Method method, String tagName)
    {
        OtherJavadoc otherJavadoc = getTag(method, tagName);
        if (otherJavadoc == null)
            return null;
        return FormatUtils.format(otherJavadoc.getComment());
    }
}
