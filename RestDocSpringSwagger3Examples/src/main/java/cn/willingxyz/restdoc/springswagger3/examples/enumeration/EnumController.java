package cn.willingxyz.restdoc.springswagger3.examples.enumeration;

import org.springframework.web.bind.annotation.*;

/**
 * 枚举
 */
@RestController
@RequestMapping("/enum")
public class EnumController {
    /**
     * 单参数枚举
     */
    @GetMapping("/parameter/single")
    public void singleParameterEnum(EnumA en)
    {
    }

    /**
     * 枚举在类型里面
     */
    @PostMapping("/parameter/in")
    public void inParameterEnum(@RequestBody EnumContainer enumContainer)
    {
    }

    /**
     * 返回枚举
     */
    @GetMapping("/response/single")
    public EnumA singleResponse()
    {
        return null;
    }

    /**
     * 返回的枚举在类型里面
     */
    @GetMapping("/response/in")
    public EnumContainer inResponse()
    {
        return null;
    }
}
