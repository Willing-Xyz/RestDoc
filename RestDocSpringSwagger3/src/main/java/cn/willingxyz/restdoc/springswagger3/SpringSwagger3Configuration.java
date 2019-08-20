package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.config.RestDocConfig;
import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import cn.willingxyz.restdoc.core.parse.impl.JavaTypeInspector;
import cn.willingxyz.restdoc.core.parse.impl.RestDocParser;
import cn.willingxyz.restdoc.core.parse.impl.TypeNameParser;
import cn.willingxyz.restdoc.spring.SpringControllerResolver;
import cn.willingxyz.restdoc.spring.SpringRestDocParseConfig;
import cn.willingxyz.restdoc.swagger.common.PrimitiveSwaggerTypeInspector;
import cn.willingxyz.restdoc.swagger.common.SwaggerGeneratorConfig;
import cn.willingxyz.restdoc.swagger3.Swagger3RestDocGenerator;
import lombok.var;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SpringSwagger3Configuration {

    @Bean("swagger3")
    IRestDocParser _docParser(@Autowired(required = false) RestDocConfig restDocConfig)
    {
        if (restDocConfig == null)
        {
            restDocConfig = RestDocConfig.builder()
                    .apiDescription("API descritpion")
                    .apiTitle("API title")
                    .apiVersion("1.0-SNAPSHOT")
                    .packages(Arrays.asList(""))
                    .build();
        }
        var config = new SpringRestDocParseConfig();

        // todo 从spring容器中获取实例
        var docConfig =  SwaggerGeneratorConfig.builder().description(restDocConfig.getApiDescription()).title(restDocConfig.getApiTitle())
                .version(restDocConfig.getApiVersion())
                .servers(Arrays.asList(SwaggerGeneratorConfig.ServerInfo.builder().description("server").url("/")
                        .build()))
                .swaggerTypeInspector(new PrimitiveSwaggerTypeInspector())
                .typeInspector(new JavaTypeInspector())
                .typeNameParser(new TypeNameParser())
                .tagDescriptionAsName(restDocConfig.isTagDescriptionAsName())
                .hideEmptyController(restDocConfig.isHideEmptyController())
                .build();
        config.getControllerResolvers().add(new SpringControllerResolver(restDocConfig.getPackages()));
        config.setRestDocGenerator(new Swagger3RestDocGenerator(docConfig));
        config.setFieldPrefix(restDocConfig.getFieldPrefix());
        return new RestDocParser(config);
    }

    @Bean
    SpringSwagger3Controller _springSwagger3Controller(@Qualifier("swagger3") IRestDocParser docParser)
    {
        SwaggerUIConfiguration uiConfiguration;
        try {
            uiConfiguration = _applicationContext.getBean(SwaggerUIConfiguration.class);
        }
        catch (NoSuchBeanDefinitionException e)
        {
            uiConfiguration = new SwaggerUIConfiguration();
        }
        var controller = new SpringSwagger3Controller(docParser, uiConfiguration);
//        controller.setUiConfiguration(uiConfiguration);
        return controller;
    }

    @Autowired
    ApplicationContext _applicationContext;
}
