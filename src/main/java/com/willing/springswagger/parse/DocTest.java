package com.willing.springswagger.parse;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.CommentFormatter;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.txws.common.webapi.response.Response;
import com.willing.springswagger.parse.impl.DocParser;
import com.willing.springswagger.parse.utils.ClassUtils;
import lombok.Data;
import lombok.var;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

/**
 * 类级别注释
 */
@RestController
@RequestMapping("/test")
public class DocTest {
//    /**
//     * 方法1111111111
//     * @param str 参数1111111111111
//     * @return 返回值。。。。。。。。
//     */
//    @GetMapping(path = "/method1")
//    public int method1(String str)
//    {
//        return 1;
//    }
//
//    /**
//     * post方法
//     * @param abc 参数abc
//     * @return 返回abc
//     */
//    @PostMapping("/method-post1")
//    public Abc post1(@RequestBody Abc abc)
//    {
//        return null;
//    }
//
//    @PostMapping("/method-post3")
//    public <T> T post3(T t)
//    {
//        return null;
//    }

    /**
     * post方法2
     * @param hehe 参数hehe doc
     * @return 返回int111
     */
    @PostMapping("/method-post2")
    public Response<Abc> post2(Abc hehe)
    {
        return null;
    }

//    /**
//     * post40-==
//     * @param dada dada参数
//     * @return post4返回值
//     */
//    @PostMapping("/method-post4")
//    public Response post4(@RequestBody Response<Response<Abc>> dada)
//    {
//        return null;
//    }

    public static void main(String[] args) throws NoSuchMethodException {


//        var method = DocTest.class.getMethod("post2", List.class);
//        var type = method.getGenericReturnType();
//        if (type instanceof ParameterizedType)
//        {
//            var parameterizedType = (ParameterizedType)type;
//            var typeArgument = parameterizedType.getActualTypeArguments()[0];
//            var rawType = (Class)parameterizedType.getRawType();
//            var fields = rawType.getDeclaredFields();
//            for (var field : fields)
//            {
//
//                System.out.println(field);
//            }
//            var config = new DocParseConfiguration(Arrays.asList("."), "_");
//            var props = ClassUtils.parseTypeProperty(config, type, 0);
//            System.out.println(props);
////               ClassUtils.getPropertyItems(new DocParseConfiguration(Arrays.asList("."), "_"), rawType);
//            System.out.println(parameterizedType.getActualTypeArguments()[0]);
//        }
//        else {
//            System.out.println(type.getTypeName());
//        }



        var conf = new DocParseConfiguration(Arrays.asList("com.txws"), "_");
        conf.getClassResolvers().add(new TestClassResolver());

        var parser = new DocParser(conf);
        var json = parser.parse();

        System.out.println(json);
    }
    // todo enum
    public static class TestClassResolver implements IClassResolver
    {
        @Override
        public List<Class> getClasses() {
            return Arrays.asList(DocTest.class);
        }
    }

    /**
     * Abc-class
     */
    @Data
    public static class Abc
    {
        /**
         * Abc-def
         */
        private List<Def> _defs;
        private Def _def;
//        private Enum1 _enum1;
    }

    /**
     * Def-class
     */
    @Data
    public static class Def
    {
        /**
         * Def-hehe
         */
        private String _hehe;
        /**
         * Def-nini
         */
        private Integer _nini;
    }

    /**
     * Enum11111
     */
    public enum Enum1
    {
        A, B, C, D
    }
}
