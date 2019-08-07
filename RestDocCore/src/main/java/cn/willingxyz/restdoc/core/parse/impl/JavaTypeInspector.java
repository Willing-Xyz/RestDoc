package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.parse.ITypeInspector;
import lombok.var;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 判断java基本库中的类型。
 * 该类应该放在最后一个做判断
 */
public class JavaTypeInspector implements ITypeInspector {
    private List<Class> _simpleClass = new ArrayList<Class>(){{
        add(Boolean.class);
        add(Byte.class);
        add(Short.class);
        add(Integer.class);
        add(Long.class);
        add(Double.class);
        add(Float.class);
        add(Character.class);
        add(CharSequence.class);
        add(Enum.class);
        add(Number.class);
        add(URI.class);
        add(URL.class);
        add(Locale.class);
        // 日期处理
        add(Date.class);
        add(LocalDateTime.class);
        add(LocalDate.class);
        add(LocalTime.class);
        add(Year.class);
        add(YearMonth.class);
        add(MonthDay.class);
        add(Instant.class);
    }};
    @Override
    public boolean isSimpleType(Type type) {
        if (type instanceof Class)
        {
            var clazz = (Class)type;
            for (var simpleClass : _simpleClass)
            {
                if (simpleClass.isAssignableFrom(clazz))
                    return true;
            }
            if (clazz.isPrimitive()) {
                return true;
            }
        }
        else if (type instanceof ParameterizedType)
        {
            return isSimpleType(((ParameterizedType) type).getRawType());
        }
        return false;
    }

    @Override
    public boolean isCollection(Type type) {
        if (type instanceof Class)
        {
            var clazz = (Class)type;

            if (clazz.isArray()) {
                return true;
            }
            if (List.class.isAssignableFrom(clazz)) {
                return true;
            }
        }
        else if (type instanceof ParameterizedType)
        {
            return isCollection(((ParameterizedType) type).getRawType());
        }

        return false;
    }

    @Override
    public Type getCollectionComponentType(Type type) {
        if (!isCollection(type))
            throw new RuntimeException(type.getTypeName() + " is not collection");
        if (type instanceof Class)
        {
            var clazz = (Class)type;
            if (clazz.isArray())
            {
                return clazz.getComponentType();
            }
            if (List.class.isAssignableFrom(clazz))
            {

            }
        }
        else if (type instanceof ParameterizedType)
        {
            var parameterizedType = (ParameterizedType)type;
            if (List.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                return parameterizedType.getActualTypeArguments()[0];
            }
        }
        return Object.class;
    }

    @Override
    public boolean isSupport(Type type) {
        return true;
    }
}
