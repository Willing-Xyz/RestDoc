package com.willing.restdoc.swagger3;

import lombok.Data;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class PrimitiveSwaggerTypeInspector implements ISwaggerTypeInspector {

    private Map<Type, Item> _classes;
    private Item[] _items = new Item[]{
            new Item(Integer.class, "integer", "int32"),
            new Item(int.class, "integer", "int32"),
            new Item(Long.class, "integer", "int64"),
            new Item(long.class, "integer", "int64"),
            new Item(Float.class, "number", "float"),
            new Item(float.class, "number", "float"),
            new Item(Double.class, "number", "double"),
            new Item(double.class, "number", "double"),
            new Item(String.class, "string", null),
            new Item(String[].class, "array", null),
            new Item(byte.class, "string","byte"),
            new Item(Byte.class, "string","byte"),
            new Item(byte[].class, "string","binary"),
            new Item(Byte[].class, "string","binary"),
            new Item(boolean.class, "boolean",null),
            new Item(Boolean.class, "boolean",null),
            new Item(Enum.class, "object",null),
            new Item(List.class,  "array", null),
            new Item(Object[].class,  "array", null),
            // todo date/time
    };

    public PrimitiveSwaggerTypeInspector()
    {
        _classes = Arrays.stream(_items).collect(Collectors.toMap(o -> o.getType(), p -> p));
    }

    @Override
    public String toSwaggerType(Type type) {
        if (!_classes.containsKey(type))
            return "object";
        return _classes.get(type).getSwaggerType();
    }

    @Override
    public String toSwaggerFormat(Type type) {
        if (!_classes.containsKey(type))
            return null;
        return _classes.get(type).getSwaggerFormat();
    }

    @Data
    private static class Item
    {
        public Item(Type type, String swaggerType, String swaggerFormat) {
            _type = type;
            _swaggerType = swaggerType;
            _swaggerFormat = swaggerFormat;
        }

        private Type _type;
        private String _swaggerType;
        private String _swaggerFormat;
    }
}
