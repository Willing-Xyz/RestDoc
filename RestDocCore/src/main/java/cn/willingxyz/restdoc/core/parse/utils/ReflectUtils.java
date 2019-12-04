package cn.willingxyz.restdoc.core.parse.utils;

import lombok.var;
import org.springframework.util.StringUtils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

public class ReflectUtils {
    public static boolean isEnum(Type type)
    {
        return type instanceof Class && ((Class)type).isEnum();
    }

    /**
     * 为对象的field进行赋值
     *
     * @param instance    实例对象
     * @param field       field对象
     * @param targetValue field进行的赋值
     */
    public static void setFieldValue(Object instance, Field field, Object targetValue) {
        if (instance == null || field == null) return;
        try {
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(instance.getClass()).getPropertyDescriptors()) {
                Method writeMethod = descriptor.getWriteMethod();
                if (descriptor.getName().equalsIgnoreCase(field.getName()) && writeMethod != null) {
                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                        writeMethod.setAccessible(true);
                    }
                    writeMethod.invoke(instance, targetValue);
                    return;
                }
            }
            field.setAccessible(true);
            field.set(instance, targetValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 为对象的field进行赋值
     *
     * @param instance    实例对象
     * @param fieldName   field名称
     * @param targetValue field进行的赋值
     */
    public static void setFieldValue(Object instance, String fieldName, Object targetValue) {
        if (instance == null) return;
        setFieldValue(instance, getField(instance.getClass(), fieldName), targetValue);
    }

    /**
     * 根据fieldName获取class对象的field
     *
     * @param clazz     class对象
     * @param fieldName 要获取的fieldName
     * @return 返回已经设置为accessible的field
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        if (clazz == null || StringUtils.isEmpty(fieldName)) return null;

        for (Field field : getAllFields(clazz)) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(fieldName)) return field;
        }

        return null;
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
