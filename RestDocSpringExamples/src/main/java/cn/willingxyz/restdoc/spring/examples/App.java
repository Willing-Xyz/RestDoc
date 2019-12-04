package cn.willingxyz.restdoc.spring.examples;

import cn.willingxyz.restdoc.beanvalidation.*;
import cn.willingxyz.restdoc.core.config.RestDocConfig;
import cn.willingxyz.restdoc.spring.examples.ext.BearerOpenAPIFilter;
import cn.willingxyz.restdoc.spring.examples.ext.TestOpenAPIFilter;
import cn.willingxyz.restdoc.spring.examples.ext.TestSwaggerFilter;
import cn.willingxyz.restdoc.springswagger2.EnableSwagger2;
import cn.willingxyz.restdoc.springswagger3.EnableSwagger3;
import cn.willingxyz.restdoc.swagger.common.SwaggerUIConfiguration;
import cn.willingxyz.restdoc.swagger2.RestDocConfigSwagger2Ext;
import cn.willingxyz.restdoc.swagger3.IOpenAPIFilter;
import cn.willingxyz.restdoc.swagger3.RestDocConfigSwagger3Ext;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                .resolveJavaDocAsTypeName(false)
//                .httpBasicAuth(new RestDocConfig.HttpBasicAuth("restdoc","restdoc"))
                .packages(Arrays.asList("cn.willingxyz.restdoc.spring.examples"))
                .servers(Arrays.asList(RestDocConfig.Server.builder().description("url desc").url("localhost:8080").build()))
//                .propertyPostProcessors(Arrays.asList(
//                        new NotNullPostProcessor(),
//                        new AssertFalsePostProcessor(),
//                        new AssertTruePostProcessor(),
//                        new DecimalMaxPostProcessor(),
//                        new DecimalMinPostProcessor(),
//                        new EmailPostProcessor(),
//                        new MaxPostProcessor(),
//                        new MinPostProcessor(),
//                        new NegativeOrZeroPostProcessor(),
//                        new NegativePostProcessor(),
//                        new NotBlankPostProcessor(),
//                        new NotEmptyPostProcessor(),
//                        new NotNullPostProcessor(),
//                        new NullPostProcessor(),
//                        new PositiveOrZeroPostProcessor(),
//                        new PositivePostProcessor(),
//                        new SizePostProcessor()
//                        ))
                // todo 使用某种自动发现机制
                .build();
    }
    @Bean
    RestDocConfigSwagger3Ext restDocConfigSwagger3Ext()
    {
        return RestDocConfigSwagger3Ext.builder()
                .openAPIFilters(Arrays.asList(new TestOpenAPIFilter(), new BearerOpenAPIFilter()))
                .build();
    }
    @Bean
    RestDocConfigSwagger2Ext restDocConfigSwagger2Ext()
    {
        return RestDocConfigSwagger2Ext.builder()
                .swaggerFilters(Arrays.asList(new TestSwaggerFilter()))
                .build();
    }
    @Bean
    SwaggerUIConfiguration _swaggerUIConfiguration()
    {
        var uiConfig = new SwaggerUIConfiguration();
//        uiConfig.setDefaultModelRendering("model");
        uiConfig.setDefaultModelExpandDepth(100);
//        uiConfig.setDocExpansion("full");
        return uiConfig;
    }




}
