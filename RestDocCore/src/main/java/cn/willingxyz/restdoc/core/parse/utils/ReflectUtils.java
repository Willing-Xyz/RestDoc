package cn.willingxyz.restdoc.core.parse.utils;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import lombok.Data;
import lombok.var;

import java.lang.reflect.*;
import java.util.*;

public class ReflectUtils {

    public static boolean isEnum(Type type)
    {
        return type instanceof Class && ((Class)type).isEnum();
    }

    /**
     * 获取所有的Fields，包括继承的field和私有的filed
     */
    public static List<Field> getAllFields(Class clazz)
    {
        List<Class> classes = new ArrayList<>();
        do {
            if (clazz != Object.class)
                classes.add(clazz);
        }
        while ((clazz = clazz.getSuperclass()) != null);
        // 从最顶层的类开始获取field
        Collections.reverse(classes);

        var fields = new ArrayList<Field>();
        for (var cla: classes) {
            fields.addAll(Arrays.asList(cla.getDeclaredFields()));
        }
        return fields;
    }

    /**
     * 获取类的所有方法（包括非public方法），包括从父类继承的方法。
     */
    public static List<Method> getAllMethods(Class clazz)
    {
        var methods = new ArrayList<Method>();
        do {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }
        while ((clazz = clazz.getSuperclass()) != null);
        return methods;
    }
}
