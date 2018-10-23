package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.ISwaggerTypeInspector;
import lombok.var;

import java.lang.reflect.Type;
import java.util.List;

public class ComposeSwaggerTypeInspector implements ISwaggerTypeInspector {

    private final List<ISwaggerTypeInspector> _typeInspectors;

    public ComposeSwaggerTypeInspector(List<ISwaggerTypeInspector> typeInspectors)
    {
        _typeInspectors = typeInspectors;
    }

    @Override
    public boolean isSimpleType(Type type) {
        var typeInspector = getSupportInspector(type);
        if (typeInspector == null)
            return false;
        return typeInspector.isSimpleType(type);
    }

    @Override
    public String toSwaggerType(Type type) {
        var typeInspector = getSupportInspector(type);
        if (typeInspector == null)
            return "object";
        return typeInspector.toSwaggerType(type);
    }

    @Override
    public String toSwaggerFormat(Type type) {
        var typeInspector = getSupportInspector(type);
        if (typeInspector == null)
            return null;
        return typeInspector.toSwaggerFormat(type);
    }

    @Override
    public boolean isSupport(Type type) {
        return true;
    }

    private ISwaggerTypeInspector getSupportInspector(Type type) {
        for (var typeInspector : _typeInspectors)
        {
            if (typeInspector.isSupport(type))
            {
                return typeInspector;
            }
        }
        return null;
    }
}
