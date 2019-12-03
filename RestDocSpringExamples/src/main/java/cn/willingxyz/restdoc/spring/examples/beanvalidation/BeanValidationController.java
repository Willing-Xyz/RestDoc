package cn.willingxyz.restdoc.spring.examples.beanvalidation;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * BeanValidation
 */
@RestController
@RequestMapping("/beanvalidation")
@Validated
public class BeanValidationController {
    /**
     * bean validation
     */
    @PostMapping("/test")
    public BeanValidated test(@RequestBody @Valid BeanValidated value)
    {
        return null;
    }
    /**
     * 不级联验证
     */
    @PostMapping("/test/no_cas")
    public BeanValidated test2(@RequestBody BeanValidatedChild value)
    {
        return null;
    }

}
