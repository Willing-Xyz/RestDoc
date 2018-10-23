package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.ISwaggerTypeInspector;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class PrimitiveSwaggerTypeInspector implements ISwaggerTypeInspector {

    private Map<Type, Item> _classes;
    private Item[] _items = new Item[]{
            new Item(Integer.class, true, "integer", "int32"),
            new Item(int.class, true, "integer", "int32"),
            new Item(Long.class, true, "integer", "int64"),
            new Item(long.class, true, "integer", "int64"),
            new Item(Float.class, true, "number", "float"),
            new Item(float.class, true, "number", "float"),
            new Item(Double.class, true, "number", "double"),
            new Item(double.class, true, "number", "double"),
            new Item(String.class, true, "string", null),
            new Item(byte.class, true, "string","byte"),
            new Item(Byte.class, true, "string","byte"),
            new Item(byte[].class, true, "string","binary"),
            new Item(Byte[].class, true, "string","binary"),
            new Item(boolean.class, true, "boolean",null),
            new Item(Boolean.class, true, "boolean",null),
            new Item(Enum.class, true, "object",null),
            new Item(List.class, false, "array", null),
            new Item(Object[].class, false, "array", null),
            // todo date/time
    };

    public PrimitiveSwaggerTypeInspector()
    {
        _classes = Arrays.stream(_items).collect(Collectors.toMap(o -> o.getType(), p -> p));
    }

    @Override
    public boolean isSimpleType(Type type) {
        return _classes.get(type).isSimpleType();
    }

    @Override
    public String toSwaggerType(Type type) {
        return _classes.get(type).getSwaggerType();
    }

    @Override
    public String toSwaggerFormat(Type type) {
        return _classes.get(type).getSwaggerFormat();
    }

    @Override
    public boolean isSupport(Type type) {
        return _classes.get(type) != null;
    }

    @Data
    private static class Item
    {
        public Item(Type type, boolean isSimpleType, String swaggerType, String swaggerFormat) {
            _type = type;
            _isSimpleType = isSimpleType;
            _swaggerType = swaggerType;
            _swaggerFormat = swaggerFormat;
        }

        private Type _type;
        private boolean _isSimpleType;
        private String _swaggerType;
        private String _swaggerFormat;
    }
}
