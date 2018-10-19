package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.ISwaggerTypeInspector;
import lombok.var;

import java.util.List;

public class ComposeSwaggerTypeInspector implements ISwaggerTypeInspector {

    private final List<ISwaggerTypeInspector> _typeInspectors;

    public ComposeSwaggerTypeInspector(List<ISwaggerTypeInspector> typeInspectors)
    {
        _typeInspectors = typeInspectors;
    }

    @Override
    public boolean isSimpleType(Class clazz) {
        var typeInspector = getSupportInspector(clazz);
        if (typeInspector == null)
            return false;
        return typeInspector.isSimpleType(clazz);
    }

    @Override
    public String toSwaggerType(Class clazz) {
        var typeInspector = getSupportInspector(clazz);
        if (typeInspector == null)
            return "object";
        return typeInspector.toSwaggerType(clazz);
    }

    @Override
    public String toSwaggerFormat(Class clazz) {
        var typeInspector = getSupportInspector(clazz);
        if (typeInspector == null)
            return null;
        return typeInspector.toSwaggerFormat(clazz);
    }

    @Override
    public boolean isSupport(Class clazz) {
        return true;
    }

    private ISwaggerTypeInspector getSupportInspector(Class clazz) {
        for (var typeInspector : _typeInspectors)
        {
            if (typeInspector.isSupport(clazz))
            {
                return typeInspector;
            }
        }
        return null;
    }
}
