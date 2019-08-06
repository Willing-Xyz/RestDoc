package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import cn.willingxyz.restdoc.core.parse.impl.JavaTypeInspector;
import cn.willingxyz.restdoc.core.parse.impl.RestDocParser;
import cn.willingxyz.restdoc.core.parse.impl.TypeNameParser;
import cn.willingxyz.restdoc.spring.SpringControllerResolver;
import cn.willingxyz.restdoc.swagger3.PrimitiveSwaggerTypeInspector;
import cn.willingxyz.restdoc.swagger3.Swagger3GeneratorConfig;
import cn.willingxyz.restdoc.swagger3.Swagger3RestDocGenerator;
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
        config.setFieldPrefix(restDocConfig.getFieldPrefix());
        return new RestDocParser(config);
    }

    @Bean
    SpringSwaggerController _springSwaggerController(IRestDocParser docParser)
    {
        return new SpringSwaggerController(docParser);
    }
}
