package cn.willingxyz.restdoc.spring.examples.other;

import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public void paramRequest() {}
}
