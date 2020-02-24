package cn.willingxyz.restdoc.spring.examples.jackson;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * jackson
 */
@RestController
@RequestMapping("/jackson")
public class JacksonController {

    @GetMapping("/test")
    public JacksonBean test(JacksonBean value)
    {
        return null;
    }

    /**
     * jsonIgnoreProperties
     */
    @GetMapping("/jsonIgnoreProperties")
    public IgnorePropertiesBean jsonIgnoreProperties(IgnorePropertiesBean bean)
    {
        return null;
    }

}