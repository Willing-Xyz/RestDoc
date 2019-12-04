package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.parse.ITypeInspector;
import lombok.var;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ComposeTypeInspector implements ITypeInspector {
    private List<ITypeInspector> _typeInspectors;

    public ComposeTypeInspector(List<ITypeInspector> typeInspectors)
    {
        _typeInspectors = typeInspectors;
    }

    public ComposeTypeInspector() {
        _typeInspectors = new ArrayList<>();
    }

    public void add(ITypeInspector inspector)
    {
        _typeInspectors.add(inspector);
    }

    @Override
    public boolean isSimpleType(Type type) {
        return getTypeInspector(type).isSimpleType(type);
    }

    @Override
    public boolean isCollection(Type type) {
        return getTypeInspector(type).isCollection(type);
    }

    @Override
    public Type getCollectionComponentType(Type type) {
        return getTypeInspector(type).getCollectionComponentType(type);
    }

    @Override
    public boolean isSupport(Type type) {
        return getTypeInspector(type).isSupport(type);
    }

    private ITypeInspector getTypeInspector(Type type)
    {
        for (var typeInspector : _typeInspectors)
        {
            if (typeInspector.isSupport(type))
                return typeInspector;
        }
        throw new RuntimeException("unknown type inspector:" + type.getTypeName());
    }
}
