package cn.willingxyz.restdoc.core.models;

import lombok.Data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Data
public class PropertyItem
{
    private String _propertyName;
    private Type _propertyType;
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