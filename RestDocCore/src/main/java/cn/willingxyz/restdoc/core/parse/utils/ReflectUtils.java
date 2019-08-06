package cn.willingxyz.restdoc.core.parse.utils;

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
    private static Map<String, Field> getFieldMap(RestDocParseConfig configuration, Class clazz)
    {
        var list = getAllFields(clazz);
        var map = new HashMap<String, Field>();
        for (var item : list)
        {
            String name = getFileNameByFiled(configuration, item);
            map.put(name, item);
        }
        return map;
    }

    private static String getFileNameByFiled(RestDocParseConfig configuration, Field item) {
        return item.getName();
    }

    public static PropertyItem[] getPropertyItems(RestDocParseConfig configuration, Class clazz)
    {
        var fields = getFieldMap(configuration, clazz);
        var items = new HashMap<String, PropertyItem>();
        for (var method: clazz.getMethods()) // public方法
        {
            if (method.getName().equals("getClass"))
                continue;
            if (isPropertyMethod(method))
            {
                String propName = getPropertyNameByMethod(method);
                PropertyItem propertyItem = items.get(propName);
                if (propertyItem == null)
                {
                    propertyItem = new PropertyItem();
                    items.put(propName, propertyItem);
                }
                propertyItem.setPropertyName(propName);
                var field = getFieldByPropertyName(fields, propName, configuration.getFieldPrefix());
                if (field != null)
                {
                    propertyItem.setField(field);
                }
                if (method.getName().startsWith("get") || method.getName().startsWith("is"))
                    propertyItem.setGetMethod(method);
                else
                    propertyItem.setSetMethod(method);
            }
        }
        return items.values().toArray(new PropertyItem[]{});
    }

    private static Field getFieldByPropertyName(Map<String, Field> fields, String propName, String prefix) {
        for (var fieldName : fields.keySet())
        {
            if (prefix != null && fieldName.startsWith(prefix))
            {
                var name = fieldName.substring(prefix.length());
                if (name.equals(propName))
                    return fields.get(fieldName);
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                if (name.equals(propName))
                    if (name.equals(propName));
            }
            if (fieldName.equals(propName))
                return fields.get(fieldName);
        }
        for (var fieldName :fields.keySet())
        {
            // 如果字段是isStudent，生成的get方法是 isStudent，因此，此处的propName为student
            if (fields.get(fieldName).getType() == boolean.class && fieldName.startsWith("is") && fieldName.length() > 2)
            {
                var name = fieldName.substring(2);
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                if (name.equals(propName))
                    return fields.get(fieldName);
            }
        }
        return null;
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

    private static boolean isPropertyMethod(Method method) {
        return method.getName().length() > 3 && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())
            &&
                (method.getName().startsWith("get")
                || (method.getName().startsWith("set") && method.getParameters().length == 1)
                || (method.getName().startsWith("is") && method.getReturnType() == boolean.class));
    }

    private static String getPropertyNameByMethod(Method method) {
        String propertyName = null;
        if (method.getName().startsWith("is")) // boolean
        {
            propertyName = method.getName().substring(2);
        }
        else
        {
            propertyName = method.getName().substring(3);
            if (propertyName.length() >= 2 && Character.isUpperCase(propertyName.charAt(1)))
            {
                propertyName = propertyName; // 如果属性的第二个字母大写，那么大小写不变
            }
            else if (propertyName.length() >= 2 && Character.isUpperCase(propertyName.charAt(0)) && Character.isUpperCase(propertyName.charAt(1)))
            {
                propertyName = propertyName;
            }
            else
            {
                propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
            }
        }
        return propertyName;

    }
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

        public Type getPropertyType() {
            Type propType = null;
            if (this.getField() != null) {
                propType = this.getField().getGenericType();
            } else if (this.getGetMethod() != null) {
                propType = this.getGetMethod().getGenericReturnType();
            } else if (this.getSetMethod() != null) {
                propType = this.getSetMethod().getGenericParameterTypes()[0];
            }
            return propType;
        }
        public Class getDeclaringClass()
        {
            Class declaringClass = null;
            if (this.getField() != null) {
                declaringClass = this.getField().getDeclaringClass();
            }
            else if (this.getGetMethod() != null) {
                declaringClass = this.getGetMethod().getDeclaringClass();
            }
            else if (this.getSetMethod() != null) {
                declaringClass = this.getSetMethod().getDeclaringClass();
            }
            return declaringClass;
        }
    }

}
