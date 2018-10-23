package com.willing.springswagger.parse.utils;

import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.willing.springswagger.models.PropertyModel;
import com.willing.springswagger.parse.DocParseConfiguration;
import lombok.var;

import java.lang.reflect.*;
import java.util.*;

import static com.willing.springswagger.parse.utils.ReflectUtils.getPropertyItems;

public class ClassUtils {

    private static Set<Class> _ignoreClasses = new HashSet<Class>(){{
        add(Class.class);
        add(ParameterizedType.class);
        add(ClassLoader.class);
        add(Enum.class);
    }};
    private static List<PropertyModel> parseTypeProperty(DocParseConfiguration configuration, Type fromType, Type toType, GraphChecker<Type> graph)
    {
        var propertyModels = new ArrayList<PropertyModel>();

        if (shouldIgnoreType(toType))
            return propertyModels;

        if (graph.add(fromType, toType))
            return propertyModels;

        if (ReflectUtils.isArray(toType))
        {
            var propertyModel = new PropertyModel();

            propertyModel.setName("array");
            propertyModel.setArray(true);
            propertyModel.setPropertyType(toType);

            propertyModel.setChildren(parseTypeProperty(configuration, toType, ReflectUtils.getArrayComponentType(toType), graph));

            propertyModels.add(propertyModel);
        }
        else if (toType instanceof ParameterizedType)
        {
            parseParamerizedTypeProperty(configuration, (ParameterizedType) toType, propertyModels, graph);
        }
        else if (toType instanceof Class)
        {
            return parseClassProperty(configuration, (Class)toType, graph);
        }

        return propertyModels;
    }

    public static List<PropertyModel> parseTypeProperty(DocParseConfiguration configuration, Type type)
    {
        return parseTypeProperty(configuration, null, type, new GraphChecker<Type>());
    }

    private static void parseParamerizedTypeProperty(DocParseConfiguration configuration, ParameterizedType parameterizedType, ArrayList<PropertyModel> propertyModels, GraphChecker<Type> map) {
        System.out.println("parse paramerizedType:" + parameterizedType.getTypeName());
        var rawType = (Class)parameterizedType.getRawType();

        var typeArguments = parameterizedType.getActualTypeArguments();
        var typeParameters = rawType.getTypeParameters();

        for (var propertyItem : getPropertyItems(configuration, rawType))
        {
            Type propType = getPropertyType(propertyItem);
            for (int x = 0; x < typeParameters.length; ++x)
            {
                if (typeParameters[x].getTypeName().equals(propType.getTypeName()))
                {
                    propType = typeArguments[x];
                }
            }

            var propertyModel = new PropertyModel();
            propertyModel.setName(propertyItem.getPropertyName());
            propertyModel.setPropertyType(propType);



            setPropertyDescription(propertyItem, propertyItem, propertyModel);
            if (ReflectUtils.isArray(propType))
            {
                propertyModel.setArray(true);
                propertyModel.setChildren(parseTypeProperty(configuration, parameterizedType, ReflectUtils.getArrayComponentType(propType), map));
            }
            else {
                propertyModel.setChildren(parseTypeProperty(configuration, parameterizedType, propType, map));
            }
            propertyModels.add(propertyModel);
        }
    }

    private static boolean shouldIgnoreType(Type type) {
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

    private static List<PropertyModel> parseClassProperty(DocParseConfiguration configuration, Class<?> clazz, GraphChecker<Type> map) {
        // todo 循环引用问题
        System.out.println("parse class:" + clazz.getTypeName());
        var propertyModels = new ArrayList<PropertyModel>();
        if (shouldIgnoreType(clazz))
            return propertyModels;

        if (!configuration.getTypeInspector().isSimpleType(clazz))
        {
            for (var item : getPropertyItems(configuration, clazz))
            {
                Type propType = getPropertyType(item);

                var propertyModel = new PropertyModel();
                propertyModel.setName(item.getPropertyName());
                propertyModel.setPropertyType(propType);


                setPropertyDescription(item, item, propertyModel);
                if (ReflectUtils.isArray(propType))
                {
                    propertyModel.setArray(true);
                    propertyModel.setChildren(parseTypeProperty(configuration, clazz, ReflectUtils.getArrayComponentType(propType), map));
                }
                else {
                    propertyModel.setChildren(parseTypeProperty(configuration, clazz, propType, map));
                }
                propertyModels.add(propertyModel);
            }
        }

        return propertyModels;
    }

    private static Type getPropertyType(ReflectUtils.PropertyItem item) {
        Type propType = null;
        if (item.getField() != null) {
            propType = item.getField().getGenericType();
        } else if (item.getGetMethod() != null) {
            propType = item.getGetMethod().getGenericReturnType();
        } else if (item.getSetMethod() != null) {
            propType = item.getSetMethod().getGenericParameterTypes()[0];
        }
        return propType;
    }

    private static void setPropertyDescription(ReflectUtils.PropertyItem item, ReflectUtils.PropertyItem propertyItem, PropertyModel propertyModel) {
        Class declaringClass = null;
        if (item.getField() != null) {
            declaringClass = item.getField().getDeclaringClass();
        }
        else if (item.getGetMethod() != null) {
            declaringClass = item.getGetMethod().getDeclaringClass();
        }
        else if (item.getSetMethod() != null) {
            declaringClass = item.getSetMethod().getDeclaringClass();
        }
        var classdoc = RuntimeJavadoc.getJavadoc(declaringClass);
        if (propertyItem.getField() != null) {
            var fieldDoc = classdoc.getFields().stream().filter(o -> o.getName().equals(propertyItem.getField().getName())).findFirst();
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
    }
}
