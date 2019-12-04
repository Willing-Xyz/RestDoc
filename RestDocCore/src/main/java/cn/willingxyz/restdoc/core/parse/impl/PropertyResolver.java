package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.models.PropertyItem;
import cn.willingxyz.restdoc.core.parse.IPropertyResolver;
import cn.willingxyz.restdoc.core.config.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.utils.MyParameterizedType;
import cn.willingxyz.restdoc.core.parse.utils.ReflectUtils;
import lombok.var;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertyResolver implements IPropertyResolver {
    private final RestDocParseConfig _config;

    public PropertyResolver(RestDocParseConfig config) {
        this._config = config;
    }

    @Override
    public List<PropertyItem> resolve(Type type) {
        if (shouldIgnoreType(this._config, type))
            return new ArrayList<>();
        if (type instanceof Class) {
            return getPropertyItems(this._config, (Class) type);
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            var rawType = (Class) parameterizedType.getRawType();

            var typeArguments = parameterizedType.getActualTypeArguments();
            var typeParameters = rawType.getTypeParameters();

            List<PropertyItem> propertyItems = getPropertyItems(this._config, rawType);
            for (var propertyItem : propertyItems) {
                Type propType = propertyItem.getPropertyType();
                if (propType instanceof TypeVariable) {
                    propType = getTypeVariable(typeArguments, typeParameters, propType);
                } else if (propType instanceof ParameterizedType) {
                    ParameterizedType propParameterizedType = (ParameterizedType) propType;
                    List<Type> convertedActualArgs = new ArrayList<>();
                    for (var actualArg : propParameterizedType.getActualTypeArguments()) {
                        if (actualArg instanceof TypeVariable) {
                            convertedActualArgs.add(getTypeVariable(typeArguments, typeParameters, actualArg));
                        } else {
                            convertedActualArgs.add(actualArg);
                        }
                    }
                    propType = new MyParameterizedType(propParameterizedType.getRawType(), convertedActualArgs.toArray(new Type[]{}), propParameterizedType.getOwnerType());
                }
                propertyItem.setPropertyType(propType);
            }
            return propertyItems;
        }
        return new ArrayList<>();
    }

    private Type getTypeVariable(Type[] typeArguments, TypeVariable[] typeParameters, Type propType) {
        for (int x = 0; x < typeParameters.length; ++x) {
            if (typeParameters[x].getTypeName().equals(propType.getTypeName())) {
                propType = typeArguments[x];
            }
        }
        return propType;
    }

    public List<PropertyItem> getPropertyItems(RestDocParseConfig configuration, Class clazz) {
        var fields = ReflectUtils.getAllFields(clazz);
        List<PropertyItem> items = new ArrayList<PropertyItem>();
        for (var method : clazz.getMethods()) // public方法
        {
            if (method.getName().equals("getClass"))
                continue;
            if (isPropertyMethod(method)) {
                String propName = getPropertyNameByMethod(method);
                PropertyItem propertyItem = items.stream().filter(o -> o.getPropertyName().equals(propName)).findFirst().orElse(null);
                if (propertyItem == null) {
                    propertyItem = new PropertyItem();
                    items.add(propertyItem);
                }
                propertyItem.setPropertyName(propName);
                var field = getFieldByPropertyName(configuration, fields, propName, configuration.getFieldPrefix());
                if (field != null) {
                    propertyItem.setField(field);
                }
                if (method.getName().startsWith("get") || method.getName().startsWith("is"))
                    propertyItem.setGetMethod(method);
                else
                    propertyItem.setSetMethod(method);
                propertyItem.setPropertyType(getPropertyType(propertyItem));
            }
        }
        items = sortByField(items, fields);
        return items;
    }

    public Type getPropertyType(PropertyItem item) {
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

    private List<PropertyItem> sortByField(List<PropertyItem> items, List<Field> fields) {
        List<PropertyItem> sortedItems = new ArrayList<>();
        for (Field field : fields) {
            PropertyItem item = items.stream().filter(o -> o.getField() != null && o.getField() == field).findFirst().orElse(null);
            if (item != null) {
                sortedItems.add(item);
            }
        }
        for (PropertyItem item : items) {
            if (!sortedItems.contains(item)) {
                sortedItems.add(item);
            }
        }
        return sortedItems;
    }


    private Field getFieldByPropertyName(RestDocParseConfig configuration, List<Field> fields, String propName, String prefix) {
        for (var field : fields) {
            String fieldName = getFieldNameByFiled(configuration, field);
            if (prefix != null && fieldName.startsWith(prefix)) {
                var name = fieldName.substring(prefix.length());
                if (name.equals(propName))
                    return field;
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                if (name.equals(propName))
                    return field;
            }
            if (fieldName.equals(propName))
                return field;
        }
        for (var field : fields) {
            String fieldName = getFieldNameByFiled(configuration, field);
            // 如果字段是isStudent，生成的get方法是 isStudent，因此，此处的propName为student
            if (field.getType() == boolean.class && fieldName.startsWith("is") && fieldName.length() > 2) {
                var name = fieldName.substring(2);
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                if (name.equals(propName))
                    return field;
            }
        }
        return null;
    }

    private boolean isPropertyMethod(Method method) {
        return Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())
                &&
                (
                        (method.getName().startsWith("get") && method.getName().length() > 3 && method.getParameterCount() == 0 && method.getReturnType() != void.class && method.getReturnType() != Void.class) //
                                || (method.getName().startsWith("set") && method.getName().length() > 3 && method.getParameterCount() == 1 && (method.getReturnType() == void.class || method.getReturnType() == Void.class))
                                || (method.getName().startsWith("is") && method.getReturnType() == boolean.class && method.getParameterCount() == 0 && method.getName().length() > 2));
    }

    private String getPropertyNameByMethod(Method method) {
        String propertyName = null;
        if (method.getName().startsWith("is")) // boolean
        {
            propertyName = method.getName().substring(2);
            propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
        } else {
            propertyName = method.getName().substring(3);
            if (propertyName.length() >= 2 && Character.isUpperCase(propertyName.charAt(1))) {
                propertyName = propertyName; // 如果属性的第二个字母大写，那么大小写不变
            } else if (propertyName.length() >= 2 && Character.isUpperCase(propertyName.charAt(0)) && Character.isUpperCase(propertyName.charAt(1))) {
                propertyName = propertyName;
            } else {
                propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
            }
        }
        return propertyName;

    }

    private String getFieldNameByFiled(RestDocParseConfig configuration, Field item) {
        return item.getName();
    }

    /**
     * 忽略某些类型，不解析它的属性
     * 对于简单类型，没有属性；
     * 对于某些类型，如Class，不需要解析它的属性
     *
     * @return
     */
    private boolean shouldIgnoreType(RestDocParseConfig config, Type type) {
        if (type instanceof ParameterizedType) {
            var parameterizedType = (ParameterizedType) type;
            type = parameterizedType.getRawType();
        }
        for (var clazz : _ignoreClasses) {
            if (type instanceof Class) {
                if (clazz.isAssignableFrom((Class) type))
                    return true;
            }
        }
        if (config.getTypeInspector().isSimpleType(type))
            return true;
        return false;
    }

    private static Set<Class> _ignoreClasses = new HashSet<Class>() {{
        add(Class.class);
        add(ParameterizedType.class);
        add(ClassLoader.class);
        add(Enum.class);
    }};

}
