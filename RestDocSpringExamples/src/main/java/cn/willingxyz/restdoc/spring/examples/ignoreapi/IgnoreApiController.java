package cn.willingxyz.restdoc.spring.examples.ignoreapi;

import cn.willingxyz.restdoc.core.annotations.IgnoreApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ignoreapi")
public class IgnoreApiController {

    @IgnoreApi
    @GetMapping("/ignore")
    public void ignore()
    {
    }

}
