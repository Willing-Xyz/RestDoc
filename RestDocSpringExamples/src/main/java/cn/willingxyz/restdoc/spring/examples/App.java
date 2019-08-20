package cn.willingxyz.restdoc.spring.examples;

import cn.willingxyz.restdoc.core.config.RestDocConfig;
import cn.willingxyz.restdoc.springswagger2.EnableSwagger2;
import cn.willingxyz.restdoc.springswagger3.EnableSwagger3;
import cn.willingxyz.restdoc.swagger.common.SwaggerUIConfiguration;
import lombok.var;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@EnableSwagger2
@EnableSwagger3
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

    @Bean
    RestDocConfig _swaggerConfig()
    {
        return RestDocConfig.builder()
                .apiTitle("rest doc title")
                .apiDescription("rest doc desc")
                .apiVersion("api version")
                .fieldPrefix("_")
                .tagDescriptionAsName(true)
                .hideEmptyController(true)
                .packages(Arrays.asList("cn.willingxyz.restdoc.spring.examples"))
                .build();
    }
    @Bean
    SwaggerUIConfiguration _swaggerUIConfiguration()
    {
        var uiConfig = new SwaggerUIConfiguration();
//        uiConfig.setDefaultModelRendering("model");
        uiConfig.setDefaultModelExpandDepth(0);
//        uiConfig.setDocExpansion("full");
        return uiConfig;
    }
}
