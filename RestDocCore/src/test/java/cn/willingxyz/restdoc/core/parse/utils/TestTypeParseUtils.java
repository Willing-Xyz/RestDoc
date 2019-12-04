package cn.willingxyz.restdoc.core.parse.utils;

import cn.willingxyz.restdoc.core.config.RestDocParseConfig;
import lombok.Data;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTypeParseUtils {
    private RestDocParseConfig _config;

//
//    @Before
//    public void before()
//    {
//        _config = new RestDocParseConfig();
//        _config.setTypeInspector(new JavaTypeInspector());
//        _config.setFieldPrefix("_");
//    }
//    @Test
//    public void testParseTypeProperty_SimpleType()
//    {
//        var propertis = TypeParseUtils.parseTypeProperty(_config, Integer.class);
//        assertEquals(0, propertis.size());
//
//        propertis = TypeParseUtils.parseTypeProperty(_config, Date.class);
//        assertEquals(0, propertis.size());
//
//    }
//    @Test
//    public void testParseTypeProperty_ComplexType()
//    {
//        var propertis = TypeParseUtils.parseTypeProperty(_config, Student.class);
//        assertEquals(2, propertis.size());
//    }
//    @Test
//    public void testParseTypeProperty_ParameterizedType() throws NoSuchMethodException {
//        var type = TestTypeParseUtils.class.getMethod("test1").getGenericReturnType();
//        var properties = TypeParseUtils.parseTypeProperty(_config, type);
//        assertEquals(1, properties.size());
//
//        type = TestTypeParseUtils.class.getMethod("test2").getGenericReturnType();
//        properties = TypeParseUtils.parseTypeProperty(_config, type);
//        assertEquals(1, properties.size());
//    }
//
//    @Test
//    public void testParseTypeProperty_CircleReference()
//    {
//        var properties = TypeParseUtils.parseTypeProperty(_config, CircleReferenceA.class);
//        assertEquals(2, properties.size());
//        for (var prop : properties)
//        {
//            if (prop.getName().equals("nameA"))
//            {
//
//            }
//            else if (prop.getName().equals("circleReferenceB"))
//            {
//                var children = prop.getChildren();
//                Assert.assertEquals(2, children.size());
//                for (var child : children)
//                {
//                    if (child.getName().equals("nameB"))
//                    {}
//                    else if (child.getName().equals("circleReferenceA"))
//                    {
//                        Assert.assertEquals(0, child.getChildren().size());
//                    }
//                    else
//                    {
//                        assertTrue(false);
//                    }
//                }
//            }
//            else
//            {
//                assertTrue(false);
//            }
//        }
//    }

    @Data
    public static class Student
    {
        private String _name;
        private List<Course> _courses;
    }
    @Data
    public static class Course
    {
        private String _courseName;
        private Teacher _teacher;
        private List<Item> _items;
    }
    @Data
    public static class Teacher
    {
        private String _teacherName;
    }
    @Data
    public static class Item
    {
        private String _itemName;
    }
    @Data
    public static class CircleReferenceA
    {
        private CircleReferenceB _circleReferenceB;
        private String _nameA;
    }
    @Data
    public static class CircleReferenceB
    {
        private CircleReferenceA _circleReferenceA;
        private String _nameB;
    }

    public List<String> test1() {return null;}
    public List<Student> test2() {return null;}
}
