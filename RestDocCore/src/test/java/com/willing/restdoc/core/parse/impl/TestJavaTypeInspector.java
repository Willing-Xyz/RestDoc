package com.willing.restdoc.core.parse.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.var;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestJavaTypeInspector {
    private JavaTypeInspector _typeInspector;

    @Data
    public static class TestA
    {
        private String _uName;
        private String _XName;
        private String _heheNameHello;
        private String _URL;
        private Boolean _Hehe;


    }

    @Test
    public void test() throws JsonProcessingException {
        var obj = new TestA();
        var str = new ObjectMapper().writeValueAsString(obj);
        System.out.println(str);
    }

    @Before
    public void before()
    {
        _typeInspector = new JavaTypeInspector();
    }

    @Test
    public void testIsSimpleType()
    {
        assertTrue(_typeInspector.isSimpleType(Boolean.class));
        assertTrue(_typeInspector.isSimpleType(Byte.class));
        assertTrue(_typeInspector.isSimpleType(Short.class));
        assertTrue(_typeInspector.isSimpleType(Integer.class));
        assertTrue(_typeInspector.isSimpleType(Long.class));
        assertTrue(_typeInspector.isSimpleType(Double.class));
        assertTrue(_typeInspector.isSimpleType(Float.class));
        assertTrue(_typeInspector.isSimpleType(Character.class));

        assertTrue(_typeInspector.isSimpleType(boolean.class));
        assertTrue(_typeInspector.isSimpleType(byte.class));
        assertTrue(_typeInspector.isSimpleType(short.class));
        assertTrue(_typeInspector.isSimpleType(int.class));
        assertTrue(_typeInspector.isSimpleType(long.class));
        assertTrue(_typeInspector.isSimpleType(double.class));
        assertTrue(_typeInspector.isSimpleType(float.class));
        assertTrue(_typeInspector.isSimpleType(char.class));

        assertTrue(_typeInspector.isSimpleType(String.class));
        assertTrue(_typeInspector.isSimpleType(Date.class));

        assertTrue(_typeInspector.isSimpleType(Enum.class));
    }
    
    @Test
    public void testIsCollection()
    {
        assertTrue(_typeInspector.isCollection(List.class));
        assertTrue(_typeInspector.isCollection(ArrayList.class));

        assertTrue(_typeInspector.isCollection(int[].class));
        assertTrue(_typeInspector.isCollection(Integer[].class));

        assertTrue(_typeInspector.isCollection(Class[].class));
    }

    @Test
    public void testIsCollection_ParameterizedType() throws NoSuchMethodException {
        var type = TestJavaTypeInspector.class.getMethod("test1").getGenericReturnType();
        assertTrue(_typeInspector.isCollection(type));
    }

    @Test
    public void testGetCollectionComponentType() throws NoSuchMethodException {
        assertEquals(int.class, _typeInspector.getCollectionComponentType(int[].class));


        var type = TestJavaTypeInspector.class.getMethod("test1").getGenericReturnType();
        assertEquals(String.class, _typeInspector.getCollectionComponentType(type));


        var type2 = TestJavaTypeInspector.class.getMethod("test2").getGenericReturnType();
        assertEquals(type, _typeInspector.getCollectionComponentType(type2));
    }

    public List<String> test1(){return null;}
    public List<List<String>> test2(){return null;}
}
