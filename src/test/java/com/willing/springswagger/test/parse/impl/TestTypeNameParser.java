package com.willing.springswagger.test.parse.impl;

import com.willing.springswagger.parse.impl.TypeNameParser;
import lombok.var;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class TestTypeNameParser {
    private TypeNameParser _typeNameParser;

    @Before
    public void before()
    {
        _typeNameParser = new TypeNameParser();
    }

    @Test
    public void test() throws NoSuchMethodException {
        var type = TestTypeNameParser.class.getMethod("method").getGenericReturnType();
        var name = _typeNameParser.parse(type);
        System.out.println(name);

        var type2 = TestTypeNameParser.class.getMethod("method", int.class).getGenericReturnType();
        var name2 = _typeNameParser.parse(type2);
        System.out.println(name2);
    }

    public List<List<Map<Object, Map<Object, List<Object>>>>> method()
    {
        return null;
    }

    public com.willing.springswagger.test.parse.impl.List<List<Map<Object, Map<Object, List<Object>>>>> method(int a)
    {
        return null;
    }
}
