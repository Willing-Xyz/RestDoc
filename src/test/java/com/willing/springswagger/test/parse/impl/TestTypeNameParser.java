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
    }

    public List<List<Map<Object, Map<Object, List<Object>>>>> method()
    {
        return null;
    }
}
