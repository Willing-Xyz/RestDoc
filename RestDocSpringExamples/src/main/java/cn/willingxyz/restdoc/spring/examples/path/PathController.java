package cn.willingxyz.restdoc.spring.examples.path;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 路径测试接口
 * Controller如果没有RequestMapping注解则默认为路径为/
 * @author cweijan
 * @version 2019/10/29 11:02
 */
@RestController
public class PathController {

    @RequestMapping("/none_controller_path")
    public void noneControlerPath(){

    }


}
