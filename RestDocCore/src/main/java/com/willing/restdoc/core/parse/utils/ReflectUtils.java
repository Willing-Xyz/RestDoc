package com.willing.restdoc.core.parse.utils;

import com.willing.restdoc.core.parse.DocParseConfiguration;
import lombok.Data;
import lombok.var;

import java.lang.reflect.*;
import java.util.*;

public class ReflectUtils {

    public static boolean isEnum(Type type)
    {
        return type instanceof Class && ((Class)type).isEnum();
    }

    public static Type getArrayComponentType(Type type)
    {
        if (type instanceof ParameterizedType)
        {
            var parameterizedType = (ParameterizedType)type;
            var clazz = (Class) parameterizedType.getRawType();
            if (List.class.isAssignableFrom(clazz))
                return parameterizedType.getActualTypeArguments()[0];
            else
                throw new RuntimeException("未知的数组类型：" + type.getTypeName());
        }
        else if (type instanceof Class)
        {
            var clazz = (Class) type;
            if (clazz.isArray())
                return clazz.getComponentType();
            else
                return Object.class;
        }
        else
            throw new RuntimeException("cannot get component type by " + type.getTypeName());
    }
    // todo 接口化
    public static boolean isArray(Type type)
    {
        Class clazz = null;
        if (type instanceof ParameterizedType)
        {
            var parameterizedType = (ParameterizedType)type;
            clazz = (Class)parameterizedType.getRawType();
        }
        else if (type instanceof Class)
        {
            clazz = (Class) type;
        }
        else
        {
            return false;
        }
        if (List.class.isAssignableFrom(clazz) || clazz.isArray())
            return true;
        return false;
    }

    /**
     * 获取所有的Fields，包括继承的field
     */
    private static List<Field> getAllFields(Class clazz)
    {
        var fields = new ArrayList<Field>();
        do {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        while ((clazz = clazz.getSuperclass()) != null);
        return fields;
    }

    /**
     * 获取属性和Field的对应关系
     */
    private static Map<String, Field> getFieldMap(DocParseConfiguration configuration, Class clazz)
    {
        var list = getAllFields(clazz);
        var map = new HashMap<String, Field>();
        for (var item : list)
        {
            String name = getPropertyNameByFieldName(configuration, item);
            map.put(name, item);
        }
        return map;
    }

    private static String getPropertyNameByFieldName(DocParseConfiguration configuration, Field item) {
        String name = null;
        if (configuration.getFieldPrefix() != null && item.getName().startsWith(configuration.getFieldPrefix()))
        {
            name = item.getName().substring(configuration.getFieldPrefix().length());
            if (name.length() > 1 && Character.isUpperCase(name.charAt(1)))
            {}
            else
            {
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }
        }
        return name;
    }

    public static PropertyItem[] getPropertyItems(DocParseConfiguration configuration, Class clazz)
    {
//        if (clazz == Class.class || clazz == ClassLoader.class)
//            return new PropertyItem[]{};
        var fields = getFieldMap(configuration, clazz);
        var items = new HashMap<String, PropertyItem>();
        for (var method: clazz.getMethods())
        {
            if (method.getName().equals("getClass"))
                continue;
            if (isPropertyMethod(method))
            {
                String propName = getPropertyNameByMethodName(method);
                PropertyItem propertyItem = items.get(propName);
                if (propertyItem == null)
                {
                    propertyItem = new PropertyItem();
                    items.put(propName, propertyItem);
                }
                propertyItem.setPropertyName(propName);
                var field = fields.get(propName);
                if (field != null)
                {
                    propertyItem.setField(field);
                }
                if (method.getName().startsWith("get"))
                    propertyItem.setGetMethod(method);
                else
                    propertyItem.setSetMethod(method);
            }
        }
        return items.values().toArray(new PropertyItem[]{});
    }

    public static List<Method> getAllMethods(Class clazz)
    {
        var methods = new ArrayList<Method>();
        do {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }
        while ((clazz = clazz.getSuperclass()) != null);
        return methods;
    }

    private static boolean isPropertyMethod(Method method) {
        return method.getName().length() > 3 && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())
            && (method.getName().startsWith("get") || (method.getName().startsWith("set") && method.getParameters().length == 1));
    }

    private static String getPropertyNameByMethodName(Method method) {
        var name = method.getName().substring(3);
        var propName = "";
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1)))
        {
            propName = name;
        }
        else
        {
            propName = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return propName;
    }
    // todo boolean类型 属性名
    @Data
    public static class PropertyItem
    {
        private String _propertyName;
        /**
         * 可能属性没有对应的field
         */
        private Field _field;
        /**
         * 属性可能没有对应的getter
         */
        private Method _getMethod;
        /**
         * 属性可能没有对应的setter
         */
        private Method _setMethod;
    }

}
