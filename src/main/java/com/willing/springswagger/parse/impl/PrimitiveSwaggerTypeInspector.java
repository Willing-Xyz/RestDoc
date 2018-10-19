package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.ISwaggerTypeInspector;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

public class PrimitiveSwaggerTypeInspector implements ISwaggerTypeInspector {

    private Map<Class, Item> _classes;
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
            // todo date/time
    };

    public PrimitiveSwaggerTypeInspector()
    {
        _classes = Arrays.stream(_items).collect(Collectors.toMap(o -> o.getClazz(), p -> p));
    }

    @Override
    public boolean isSimpleType(Class clazz) {
        return _classes.get(clazz).isSimpleType();
    }

    @Override
    public String toSwaggerType(Class clazz) {
        return _classes.get(clazz).getSwaggerType();
    }

    @Override
    public String toSwaggerFormat(Class clazz) {
        return _classes.get(clazz).getSwaggerFormat();
    }

    @Override
    public boolean isSupport(Class clazz) {
        return _classes.get(clazz) != null;
    }

    @Data
    private static class Item
    {
        public Item(Class clazz, boolean isSimpleType, String swaggerType, String swaggerFormat) {
            _clazz = clazz;
            _isSimpleType = isSimpleType;
            _swaggerType = swaggerType;
            _swaggerFormat = swaggerFormat;
        }

        private Class _clazz;
        private boolean _isSimpleType;
        private String _swaggerType;
        private String _swaggerFormat;
    }
}
