package com.willing.restdoc.springswagger3.examples.parameter;

import org.springframework.web.bind.annotation.*;

/**
 * 参数
 */
@RestController
@RequestMapping("/parameter")
public class ParameterController {
    /**
     * 查询参数数组
     * @param array 字符串参数数组
     * @param parameterAS 参数A数组
     */
    @GetMapping("/queryString/array")
    public void queryStringArray(String[] array, ParameterA[] parameterAS)
    {
    }

    /**
     * body 中的简单数组
     */
    @PostMapping("/body/array/simple")
    public void simpleArray(@RequestBody String[] arr)
    {
    }

    /**
     * body 中的复杂数组
     */
    @PostMapping("/body/array/complex")
    public void complexArray(@RequestBody ParameterA[] arr)
    {
    }

    /**
     * body 里的复杂对象
     */
    @PostMapping("/body/complex")
    public void complex(@RequestBody ParameterA obj)
    {
    }

    /**
     * path 变量
     * @param parameterA  参数A
     * @param parameterB  参数B
     */
    @PostMapping("/path/{parameterA}/{parameterB}")
    public void path(@PathVariable String parameterA, String parameterB)
    {
    }

    /**
     * 泛型复杂参数
     */
    @PostMapping("/body/generic/complex")
    public void bodyGenericComplex(@RequestBody GenericParameter<ParameterA> parameterAGenericParameter)
    {
    }

    /**
     * 泛型简单参数
     */
    @PostMapping("/body/generic/simple")
    public void bodyGenericSimple(@RequestBody GenericParameter<Integer> parameter)
    {
    }

}
