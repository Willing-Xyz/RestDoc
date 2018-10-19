package com.willing.springswagger.parse.utils;

import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.willing.springswagger.models.PropertyModel;
import com.willing.springswagger.parse.DocParseConfiguration;
import com.willing.springswagger.parse.DocTest;
import lombok.Data;
import lombok.var;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ClassUtils {
    public static List<PropertyModel> parseProperty(DocParseConfiguration configuration, Class<?> parameter, int i) {
        // todo 循环引用问题
        var propertyModels = new ArrayList<PropertyModel>();


        System.out.println(i + "    class:" + parameter.getCanonicalName());
        if (!configuration.getTypeInspector().isSimpleType(parameter))
        {
            for (var item : getItems(configuration, parameter))
            {
                Class propType = null;
                Class declaringClass = null;
                if (item.getField() != null) {
                    propType = item.getField().getType();
                    declaringClass = item.getField().getDeclaringClass();
                }
                else if (item.getGetMethod() != null) {
                    propType = item.getGetMethod().getReturnType();
                    declaringClass = item.getGetMethod().getDeclaringClass();
                }
                else if (item.getSetMethod() != null) {
                    propType = item.getSetMethod().getParameterTypes()[0];
                    declaringClass = item.getSetMethod().getDeclaringClass();
                }

                var classdoc = RuntimeJavadoc.getJavadoc(declaringClass);

                var field = item.getField();
                var propertyModel = new PropertyModel();
                propertyModel.setName(item.getPropertyName());
                propertyModel.setPropertyClass(propType);
                if (field != null) {
                    var fieldDoc = classdoc.getFields().stream().filter(o -> o.getName().equals(field.getName())).findFirst();
                    if (fieldDoc.isPresent()) {
                        propertyModel.setDescription(FormatUtils.format(fieldDoc.get().getComment()));
                    }
                }
                if (propertyModel.getDescription() == null || propertyModel.getDescription().length() == 0)
                {
                    if (item.getGetMethod() != null) {
                        var getMethodDoc = classdoc.getMethods().stream().filter(o -> o.getName().equals(item.getGetMethod().getName())).findFirst();
                        if (getMethodDoc.isPresent())
                            propertyModel.setDescription(FormatUtils.format(getMethodDoc.get().getComment()));
                    }

                }
                if (propertyModel.getDescription() == null || propertyModel.getDescription().length() == 0)
                {
                    if (item.getSetMethod() != null) {
                        var setMethodDoc = classdoc.getMethods().stream().filter(o -> o.getName().equals(item.getSetMethod().getName())).findFirst();
                        if (setMethodDoc.isPresent())
                            propertyModel.setDescription(FormatUtils.format(setMethodDoc.get().getComment()));
                    }
                }

                System.out.println(propType.getCanonicalName());
                propertyModel.setChildren(parseProperty(configuration, propType, i + 1));
                propertyModels.add(propertyModel);
            }
        }

        return propertyModels;
    }

    private static List<Field> getPrivateFields(Class clazz)
    {
        var fields = new ArrayList<Field>();
        do {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }
        while ((clazz = clazz.getSuperclass()) != null);
        return fields;
    }

    private static Map<String, Field> getFieldMap(DocParseConfiguration configuration, Class clazz)
    {
        var list = getPrivateFields(clazz);
        var map = new HashMap<String, Field>();
        for (var item : list)
        {
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
            name = item.getName();
            map.put(name, item);
        }
        return map;
    }
    private static Map<String, Method> getPublicMethods(Class clazz)
    {
        Arrays.stream(clazz.getMethods()).collect(Collectors.toMap(o -> {
            if (o.getName().startsWith("get") && Modifier.isPublic(o.getModifiers()) && !Modifier.isStatic(o.getModifiers()))
            {
                return o.getName().substring(3);
            }
            return o.getName();
        }, p -> p));
        return null;
    }

    private static Item[] getItems(DocParseConfiguration configuration, Class clazz)
    {
        if (clazz == Class.class || clazz == ClassLoader.class)
            return new Item[]{};
        var fields = getFieldMap(configuration, clazz);
        var items = new HashMap<String, Item>();
        for (var method: clazz.getMethods())
        {
            if (method.getName().length() > 3 && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()))
            {
                if (method.getName().equals("getClass"))
                    continue;
                if (method.getName().startsWith("get") || (method.getName().startsWith("set") && method.getParameters().length == 1)) {
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

                    Item item = items.get(propName);
                    if (item == null)
                    {
                        item = new Item();
                        items.put(propName, item);
                    }
                    item.setPropertyName(propName);
                    var field = fields.get(propName);
                    if (field != null)
                    {
                        item.setField(field);
                    }
                    if (method.getName().startsWith("get"))
                        item.setGetMethod(method);
                    else
                        item.setSetMethod(method);
                }
            }
        }
        return items.values().toArray(new Item[]{});
    }

    @Data
    private static class Item
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

    public static void main(String[] args) {
        var list = parseProperty(new DocParseConfiguration(Arrays.asList("."), "_"), TestEnum.class.getClassLoader().getClass(), 0);
        System.out.println(list);
    }
    public enum TestEnum
    {
        HAHA
    }
}
