package cn.willingxyz.restdoc.spring.examples.parameter;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 参数
 */
@RestController
@RequestMapping("/parameter")
public class ParameterController {
    @GetMapping("/queryString")
    public Map<String, String> queryString(Map<String, String> map) {
        System.out.println(map);
         return null;
    }
//    /**
//     * 查询字符串
//     */
//    @GetMapping("/queryString")
//    public void queryString(@RequestParam int param1, @RequestParam(required = false) String param2)
//    {
//    }
//
//    /**
//     * 查询参数数组
//     * @param array 字符串参数数组
//     * @param parameterAS 参数A数组
//     */
//    @GetMapping("/queryString/array")
//    public void queryStringArray(String[] array, ParameterA[] parameterAS)
//    {
//    }
//
//    /**
//     * 查询字符串 复杂参数
//     */
//    @GetMapping("/queryString/complex")
//    public void queryStringComplex(ParameterA parameterA) // todo
//    {
//    }
//
//    /**
//     * 表单post
//     */
//    @PostMapping("/form/complex")
//    public void formComplex(ParameterA parameterA)
//    {
//    }
//
//    /**
//     * body 中的简单数组
//     */
//    @PostMapping("/body/array/simple")
//    public void simpleArray(@RequestBody String[] arr)
//    {
//    }
//
//    /**
//     * body 中的复杂数组
//     */
//    @PostMapping("/body/array/complex")
//    public void complexArray(@RequestBody ParameterA[] arr)
//    {
//    }
//
//    /**
//     * body 里的复杂对象
//     */
//    @PostMapping("/body/complex")
//    public void complex(@RequestBody ParameterA obj)
//    {
//    }
//
//    /**
//     * path 变量
//     * @param parameterA  参数A
//     * @param parameterB  参数B
//     */
//    @PostMapping("/path/{parameterA}/{parameterB}")
//    public void path(@PathVariable String parameterA, @PathVariable String parameterB)
//    {
//    }
//
//    /**
//     * 泛型复杂参数
//     */
//    @PostMapping("/body/generic/complex")
//    public void bodyGenericComplex(@RequestBody GenericParameter<ParameterA> parameterAGenericParameter)
//    {
//    }
//
//    /**
//     * 泛型简单参数
//     */
//    @PostMapping("/body/generic/simple")
//    public void bodyGenericSimple(@RequestBody GenericParameter<Integer> parameter)
//    {
//    }
//
//    /**
//     * 双重泛型参数
//     */
//    @PostMapping("/body/generic/multi")
//    public void bodyGenericMulti(@RequestBody MultipartGenericParameter<Integer,ParameterA> parameter)
//    {
//    }

}
