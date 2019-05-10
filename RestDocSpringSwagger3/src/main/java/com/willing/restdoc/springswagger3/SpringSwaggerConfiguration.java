package com.willing.restdoc.springswagger3;

import com.willing.restdoc.core.parse.IRestDocParser;
import com.willing.restdoc.core.parse.impl.JavaTypeInspector;
import com.willing.restdoc.core.parse.impl.RestDocParser;
import com.willing.restdoc.core.parse.impl.TypeNameParser;
import com.willing.restdoc.spring.SpringControllerResolver;
import com.willing.restdoc.swagger3.PrimitiveSwaggerTypeInspector;
import com.willing.restdoc.swagger3.Swagger3GeneratorConfig;
import com.willing.restdoc.swagger3.Swagger3RestDocGenerator;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SpringSwaggerConfiguration {

    @Bean
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
        var config = new SpringSwagger3RestDocParseConfig();

        // todo 从spring容器中获取实例
        var docConfig =  Swagger3GeneratorConfig.builder().description(restDocConfig.getApiDescription()).title(restDocConfig.getApiTitle())
                .version(restDocConfig.getApiVersion())
                .servers(Arrays.asList(Swagger3GeneratorConfig.ServerInfo.builder().description("server").url("/")
                        .build()))
                .swaggerTypeInspector(new PrimitiveSwaggerTypeInspector())
                .typeInspector(new JavaTypeInspector())
                .typeNameParser(new TypeNameParser())
                .build();
        config.getControllerResolvers().add(new SpringControllerResolver(restDocConfig.getPackages()));
        config.setRestDocGenerator(new Swagger3RestDocGenerator(docConfig));
        return new RestDocParser(config);
    }

    @Bean
    SpringSwaggerController _springSwaggerController(IRestDocParser docParser)
    {
        return new SpringSwaggerController(docParser);
    }
}
