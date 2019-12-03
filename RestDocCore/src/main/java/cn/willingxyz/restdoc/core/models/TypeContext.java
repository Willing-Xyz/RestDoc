package cn.willingxyz.restdoc.core.models;

import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@Data
public class TypeContext {
    public TypeContext(Type _type, Parameter _parameter, Method _method) {
        this._type = _type;
        this._parameter = _parameter;
        this._method = _method;
    }

    private Type _type;
    private Parameter _parameter;
    private Method _method;
}
