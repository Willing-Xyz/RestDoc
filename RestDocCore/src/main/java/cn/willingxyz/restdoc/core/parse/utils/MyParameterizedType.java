package cn.willingxyz.restdoc.core.parse.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class MyParameterizedType implements ParameterizedType {
    private final Type _ownerType;
    private final Type[] _actualTypeArguments;
    private final Type _rawType;

    public MyParameterizedType(Type rawType, Type[] actualTypeArguments, Type ownerType) {
        _rawType = rawType;
        _actualTypeArguments = actualTypeArguments;
        _ownerType = ownerType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return _actualTypeArguments;
    }

    @Override
    public Type getRawType() {
        return _rawType;
    }

    @Override
    public Type getOwnerType() {
        return _ownerType;
    }

    // copy from ParameterizedTypeImpl
    @Override
    public String getTypeName() {
        StringBuilder var1 = new StringBuilder();
        if (_ownerType != null) {
            if (_ownerType instanceof Class) {
                var1.append(((Class)_ownerType).getName());
            } else {
                var1.append(_ownerType.toString());
            }

            var1.append("$");
            if (_ownerType instanceof ParameterizedType) {
                var1.append(_rawType.getTypeName().replace(((MyParameterizedType)_ownerType)._rawType.getTypeName() + "$", ""));
            } else {
                var1.append(_rawType.getTypeName());
            }
        } else {
            var1.append(_rawType.getTypeName());
        }

        if (_actualTypeArguments != null && _actualTypeArguments.length > 0) {
            var1.append("<");
            boolean var2 = true;
            Type[] var3 = _actualTypeArguments;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Type var6 = var3[var5];
                if (!var2) {
                    var1.append(", ");
                }

                var1.append(var6.getTypeName());
                var2 = false;
            }

            var1.append(">");
        }

        return var1.toString();
    }
}
