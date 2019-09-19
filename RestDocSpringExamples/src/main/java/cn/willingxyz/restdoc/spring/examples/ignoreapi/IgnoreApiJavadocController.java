package cn.willingxyz.restdoc.spring.examples.ignoreapi;

import cn.willingxyz.restdoc.core.annotations.IgnoreApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ignoreapi/javadoc")
public class IgnoreApiJavadocController {

    /**
     * @ignoreApi
     */
    @GetMapping("/ignore")
    public void ignore()
    {
    }

    /**
     * @IgnoreApi
     */
    @GetMapping("/ignore2")
    public void ignore2()
    {
    }
}
