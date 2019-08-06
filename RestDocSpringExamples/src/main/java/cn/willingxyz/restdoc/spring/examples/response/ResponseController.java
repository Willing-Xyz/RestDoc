package cn.willingxyz.restdoc.spring.examples.response;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 返回类型
 */
@RestController
@RequestMapping("/response")
public class ResponseController {
    /**
     * 返回简单数组类型
     */
    @GetMapping("/array/simple")
    public String[] arraySimple()
    {
        return null;
    }

    /**
     * 返回复杂数组类型
     */
    @GetMapping("/array/complex")
    public ResponseA[] arrayComplex()
    {
        return null;
    }

    /**
     * 返回List复杂类型
     */
    @GetMapping("/list/complex")
    public List<ResponseA> listComplex()
    {
        return null;
    }

    /**
     * 返回List复杂类型
     */
    @GetMapping("/list/simple")
    public List<String> listSimple()
    {
        return null;
    }

    /**
     * 返回复杂类型
     */
    @GetMapping("/complex")
    public ResponseA complex()
    {
        return null;
    }

    /**
     * 返回简单类型
     */
    @GetMapping("/simple")
    public Integer simple()
    {
        // todo 返回Integer，不会再swagger-ui上显示，但返回String，会在swagger-ui上显示
        // 这是swagger-ui的一个bug
        return null;
    }

    /**
     * 泛型复杂返回类型
     */
    @GetMapping("/generic/complex")
    public GenericResponse<ResponseA> genericComplex()
    {
        return null;
    }
    /**
     * 泛型简单返回类型
     */
    @GetMapping("/generic/simple")
    public GenericResponse<Integer> genericSimple()
    {
        return null;
    }

    /**
     * 泛型嵌套
     */
    @GetMapping("/generic/nested")
    public GenericResponse<GenericResponse.Page<ResponseA>> genericNested()
    {
        return null;
    }

    /**
     * 泛型嵌套（多层嵌套）
     */
    @GetMapping("/generic/nested/multi")
    public GenericResponse<GenericResponse.Page<GenericResponse.Page<GenericResponse.Page<ResponseA>>>> genericNestedMulti()
    {
        return null;
    }
}
