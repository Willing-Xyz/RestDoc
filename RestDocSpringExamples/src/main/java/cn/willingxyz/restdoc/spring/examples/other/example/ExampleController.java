package cn.willingxyz.restdoc.spring.examples.other.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * example
 */
@RestController
@RequestMapping("/example")
public class ExampleController {
    @GetMapping("/test")
    public ExampleBean test(ExampleBean bean)
    {
        return null;
    }

    /**
     * @paramExample value 🙂2019-12-03🙂
     * @paramExample intValue 12
     * @returnExample 😡returnValue😊
     */
    @GetMapping("/test2")
    public String test2(LocalDateTime value, int intValue)
    {
        return null;
    }

}
