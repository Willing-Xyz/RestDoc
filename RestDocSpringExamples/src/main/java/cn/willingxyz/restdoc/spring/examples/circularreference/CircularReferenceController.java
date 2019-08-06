package cn.willingxyz.restdoc.spring.examples.circularreference;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类型循环引用的情况
 * 如果检测到类型之间的循环引用，则在检测到的那一刻停止解析
 */
@RestController
@RequestMapping("/circular/reference")
public class CircularReferenceController {
    @PostMapping("/post")
    public void post(@RequestBody CircularA circularA)
    {
    }
}
