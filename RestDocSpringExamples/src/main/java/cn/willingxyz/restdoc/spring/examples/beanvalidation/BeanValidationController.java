package cn.willingxyz.restdoc.spring.examples.beanvalidation;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/test")
    public BeanValidated test(BeanValidated value)
    {
        return null;
    }

}
