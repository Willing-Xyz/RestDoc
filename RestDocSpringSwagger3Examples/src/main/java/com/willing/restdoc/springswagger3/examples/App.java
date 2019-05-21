package com.willing.restdoc.springswagger3.examples;

import com.willing.restdoc.springswagger3.EnableSwagger3;
import com.willing.restdoc.springswagger3.RestDocConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

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
                .packages(Arrays.asList("com.willing.restdoc.springswagger3.examples"))
                .build();
    }

    // todo 打包源码

}
