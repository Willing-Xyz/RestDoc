package cn.willingxyz.restdoc.spring.examples.other;

import lombok.Data;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 其他
 */
@RestController
@RequestMapping("/other")
public class OtherController {

    /**
     * 通过@RequestMapping指定method属性
     */
    @RequestMapping(method = RequestMethod.GET, path = "method")
    public void requestMappingMethod()
    {}

    /**
     * 参数传递HttpServletRequest和HttpServletResponse
     */
    @GetMapping("/param")
    public void param(ServletRequest servletRequest, ServletResponse servletResponse, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {}

    /**
     * post
     */
    @PostMapping("/param")
    public void paramPost()
    {}

    @RequestMapping("/param")
    public void paramRequest(@RequestBody A a) {}

    @Data
    public static class A {
        private Set<String> _test;
        private Map<String, String> _test2;
        private Map<Map<String, String>, String> _test21;
    }
}
