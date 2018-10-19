package com.willing.springswagger.parse;

import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.CommentFormatter;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.txws.common.webapi.response.Response;
import com.willing.springswagger.parse.impl.DocParser;
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
    /**
     * post方法
     * @param abc 参数abc
     * @return 返回abc
     */
    @PostMapping("/method-post1")
    public Abc post1(@RequestBody Abc abc)
    {
        return null;
    }

    /**
     * post方法2
     * @param hehe 参数hehe doc
     * @return 返回int111
     */
    @PostMapping("/method-post2")
    public Response<Abc> post2(@RequestBody List<Integer> hehe)
    {
        return null;
    }

    public static void main(String[] args) throws NoSuchMethodException {

        var method = DocTest.class.getMethod("post1", Abc.class);
        var paramTypes = method.getGenericParameterTypes();
        for (var type : paramTypes) {
            if (type instanceof ParameterizedType)
            {
                var x = (ParameterizedType)type;
                System.out.println(x.getActualTypeArguments()[0]);
            }
            else {
                System.out.println(type.getTypeName());
            }
        }



//        var conf = new DocParseConfiguration(Arrays.asList("com.txws"));
//        conf.getClassResolvers().add(new TestClassResolver());
//
//        var parser = new DocParser(conf);
//        var json = parser.parse();
//
//        System.out.println(json);
    }
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
    public static class Abc extends Def
    {
        /**
         * Abc-str
         */
        private String _str;
        /**
         * Abc-int
         */
        private int _int;
        /**
         * Abc-def
         */
        private Def _def;
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
}
