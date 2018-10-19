package com.willing.springswagger.parse.utils;

import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.willing.springswagger.models.PropertyModel;
import com.willing.springswagger.parse.DocParseConfiguration;
import lombok.Data;
import lombok.var;

import java.lang.reflect.*;
import java.util.*;

public class ClassUtils {

    private static Set<Class> _ignoreClasses = new HashSet<Class>(){{
        add(Class.class);
        add(ParameterizedType.class);
        add(ClassLoader.class);
        add(Enum.class);
    }};

    public static List<PropertyModel> parseProperty(DocParseConfiguration configuration, Type type, int i)
    {
        System.out.println(i + "................." + type);
        var propertyModels = new ArrayList<PropertyModel>();

        if (ignoreClass(type))
            return propertyModels;

        if (type instanceof ParameterizedType)
        {
            var parameterizedType = (ParameterizedType)type;
            var rawType = (Class)parameterizedType.getRawType();

            var typeArguments = parameterizedType.getActualTypeArguments();
            var typeParameters = rawType.getTypeParameters();

            for (var propertyItem : getPropertyItems(configuration, rawType))
            {
                Type propertyType = null;
                Class declaringClass = null;
                if (propertyItem.getField() != null) {
                    propertyType = propertyItem.getField().getGenericType();
                    declaringClass = propertyItem.getField().getDeclaringClass();
                }
                else if (propertyItem.getGetMethod() != null) {
                    propertyType = propertyItem.getGetMethod().getGenericReturnType();
                    declaringClass = propertyItem.getGetMethod().getDeclaringClass();
                }
                else if (propertyItem.getSetMethod() != null) {
                    propertyType = propertyItem.getSetMethod().getGenericParameterTypes()[0];
                    declaringClass = propertyItem.getSetMethod().getDeclaringClass();
                }
                for (int x = 0; x < typeParameters.length; ++x)
                {
                    if (typeParameters[x].getTypeName().equals(propertyType.getTypeName()))
                    {
                        propertyType = typeArguments[x];
                    }
                }



                var classdoc = RuntimeJavadoc.getJavadoc(declaringClass);

                var field = propertyItem.getField();
                var propertyModel = new PropertyModel();
                propertyModel.setName(propertyItem.getPropertyName());
//                propertyModel.setPropertyClass(propType);
                if (field != null) {
                    var fieldDoc = classdoc.getFields().stream().filter(o -> o.getName().equals(field.getName())).findFirst();
                    if (fieldDoc.isPresent()) {
                        propertyModel.setDescription(FormatUtils.format(fieldDoc.get().getComment()));
                    }
                }
                if (propertyModel.getDescription() == null || propertyModel.getDescription().length() == 0)
                {
                    if (propertyItem.getGetMethod() != null) {
                        var getMethodDoc = classdoc.getMethods().stream().filter(o -> o.getName().equals(propertyItem.getGetMethod().getName())).findFirst();
                        if (getMethodDoc.isPresent())
                            propertyModel.setDescription(FormatUtils.format(getMethodDoc.get().getComment()));
                    }

                }
                if (propertyModel.getDescription() == null || propertyModel.getDescription().length() == 0)
                {
                    if (propertyItem.getSetMethod() != null) {
                        var setMethodDoc = classdoc.getMethods().stream().filter(o -> o.getName().equals(propertyItem.getSetMethod().getName())).findFirst();
                        if (setMethodDoc.isPresent())
                            propertyModel.setDescription(FormatUtils.format(setMethodDoc.get().getComment()));
                    }
                }

                propertyModel.setChildren(parseProperty(configuration, propertyType, i + 1));
                propertyModels.add(propertyModel);
            }
        }
        else if (type instanceof Class)
        {
            return parseProperty(configuration, (Class)type, 0);
        }

        return propertyModels;
    }

    private static boolean ignoreClass(Type type) {
        if (type instanceof ParameterizedType)
        {
            var parameterizedType = (ParameterizedType)type;
            type = parameterizedType.getRawType();
        }
            for (var clazz : _ignoreClasses)
            {
                if (type instanceof Class) {
                    if (clazz.isAssignableFrom((Class) type))
                        return true;
                }
            }
        return false;
    }

    public static List<PropertyModel> parseProperty(DocParseConfiguration configuration, Class<?> parameter, int i) {
        // todo 循环引用问题
        var propertyModels = new ArrayList<PropertyModel>();
        if (ignoreClass(parameter))
            return propertyModels;

        if (!configuration.getTypeInspector().isSimpleType(parameter))
        {
            for (var item : getPropertyItems(configuration, parameter))
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
        name = item.getName();
        return name;
    }

    public static PropertyItem[] getPropertyItems(DocParseConfiguration configuration, Class clazz)
    {
        if (clazz == Class.class || clazz == ClassLoader.class)
            return new PropertyItem[]{};
        var fields = getFieldMap(configuration, clazz);
        var items = new HashMap<String, PropertyItem>();
        for (var method: clazz.getMethods())
        {
            if (method.getName().equals("getClass"))
                continue;
            if (method.getName().length() > 3 && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()))
            {
                if (method.getName().startsWith("get") || (method.getName().startsWith("set") && method.getParameters().length == 1)) {
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
        }
        return items.values().toArray(new PropertyItem[]{});
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

    @Data
    private static class PropertyItem
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
