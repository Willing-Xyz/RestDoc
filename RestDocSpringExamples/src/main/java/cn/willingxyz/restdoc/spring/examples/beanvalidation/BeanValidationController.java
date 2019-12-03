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

}
