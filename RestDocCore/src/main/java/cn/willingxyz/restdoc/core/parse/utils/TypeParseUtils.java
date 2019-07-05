package cn.willingxyz.restdoc.core.parse.utils;

import cn.willingxyz.restdoc.core.models.PropertyModel;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import lombok.var;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.*;
import java.util.*;

import static cn.willingxyz.restdoc.core.parse.utils.ReflectUtils.getPropertyItems;

public class TypeParseUtils {

    private static Set<Class> _ignoreClasses = new HashSet<Class>(){{
        add(Class.class);
        add(ParameterizedType.class);
        add(ClassLoader.class);
        add(Enum.class);
    }};

    /**
     * 解析类型的属性
     */
    public static List<PropertyModel> parseTypeProperty(RestDocParseConfig configuration, Type type)
    {
        return parseTypeProperty(configuration, null, type, new GraphChecker<>());
    }

    private static List<PropertyModel> parseTypeProperty(RestDocParseConfig configuration, Type fromType, Type toType, GraphChecker<Type> graph)
    {
        var propertyModels = new ArrayList<PropertyModel>();

        if (shouldIgnoreType(configuration, toType))
            return propertyModels;

        if (graph.add(fromType, toType))
            return propertyModels;

        // 如果是集合类型，解析组件类型
        if (configuration.getTypeInspector().isCollection(toType))
        {
            var propertyModel = new PropertyModel();

            propertyModel.setName("array");
            propertyModel.setArray(true);
            propertyModel.setPropertyType(toType);

            var children = parseTypeProperty(configuration, toType, configuration.getTypeInspector().getCollectionComponentType(toType), graph);
            propertyModel.setChildren(children);
            propertyModels.add(propertyModel);
        }
        else if (toType instanceof ParameterizedType) // 参数化类型
        {
            parseParameterizedTypeProperty(configuration, (ParameterizedType) toType, propertyModels, graph);
        }
        else if (toType instanceof Class)
        {
            return parseClassProperty(configuration, (Class)toType, graph);
        }

        return propertyModels;
    }

    private static void parseParameterizedTypeProperty(RestDocParseConfig configuration, ParameterizedType parameterizedType, ArrayList<PropertyModel> propertyModels, GraphChecker<Type> map) {
        var rawType = (Class)parameterizedType.getRawType();

        var typeArguments = parameterizedType.getActualTypeArguments();
        var typeParameters = rawType.getTypeParameters();

        for (var propertyItem : getPropertyItems(configuration, rawType))
        {
            Type propType = propertyItem.getPropertyType();
            if (propType instanceof TypeVariable) {
                propType = getTypeVariable(typeArguments, typeParameters, propType);
            }
            else if (propType instanceof ParameterizedType)
            {
                ParameterizedType propParameterizedType = (ParameterizedType)propType;
                List<Type> convertedActualArgs = new ArrayList<>();
                for (var actualArg : propParameterizedType.getActualTypeArguments())
                {
                    if (actualArg instanceof TypeVariable)
                    {
                        convertedActualArgs.add(getTypeVariable(typeArguments, typeParameters, actualArg));
                    }
                    else
                    {
                        convertedActualArgs.add(actualArg);
                    }
                }
                propType = new MyParameterizedType(propParameterizedType.getRawType(), convertedActualArgs.toArray(new Type[]{}), propParameterizedType.getOwnerType());
            }

            parseProperty(configuration, parameterizedType, propertyModels, map, propertyItem, propType);
        }
    }

    private static Type getTypeVariable(Type[] typeArguments, TypeVariable[] typeParameters, Type propType) {
        for (int x = 0; x < typeParameters.length; ++x) {
            if (typeParameters[x].getTypeName().equals(propType.getTypeName())) {
                propType = typeArguments[x];
            }
        }
        return propType;
    }

    private static void parseProperty(RestDocParseConfig configuration, Type fromType, ArrayList<PropertyModel> propertyModels, GraphChecker<Type> map, ReflectUtils.PropertyItem propertyItem, Type propType) {
        var propertyModel = new PropertyModel();
        propertyModel.setName(propertyItem.getPropertyName());
        propertyModel.setPropertyType(propType);
        setPropertyDescription(propertyItem, propertyItem, propertyModel);

        if (configuration.getTypeInspector().isCollection(propType))
        {
            propertyModel.setArray(true);
            propertyModel.setChildren(parseTypeProperty(configuration, fromType, configuration.getTypeInspector().getCollectionComponentType(propType), map));
        }
        else {
            propertyModel.setChildren(parseTypeProperty(configuration, fromType, propType, map));
        }
        propertyModels.add(propertyModel);
    }

    /**
     * 忽略某些类型，不解析它的属性
     * 对于简单类型，没有属性；
     * 对于某些类型，如Class，不需要解析它的属性
     * @return
     */
    private static boolean shouldIgnoreType(RestDocParseConfig config, Type type) {
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
        if (config.getTypeInspector().isSimpleType(type))
            return true;
        return false;
    }

    private static List<PropertyModel> parseClassProperty(RestDocParseConfig configuration, Class<?> clazz, GraphChecker<Type> map) {
        var propertyModels = new ArrayList<PropertyModel>();

        for (var item : getPropertyItems(configuration, clazz))
        {
            Type propType = item.getPropertyType();

            parseProperty(configuration, clazz, propertyModels, map, item, propType);
        }

        return propertyModels;
    }


    private static void setPropertyDescription(ReflectUtils.PropertyItem item, ReflectUtils.PropertyItem propertyItem, PropertyModel propertyModel) {
        var declaringClass = item.getDeclaringClass();
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
