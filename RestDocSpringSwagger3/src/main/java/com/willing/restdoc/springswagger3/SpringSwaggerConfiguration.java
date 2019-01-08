package com.willing.restdoc.springswagger3;

import com.willing.restdoc.core.parse.DocParseConfiguration;
import com.willing.restdoc.core.parse.IDocParser;
import com.willing.restdoc.core.parse.impl.DocParser;
import com.willing.restdoc.swagger3.PrimitiveSwaggerTypeInspector;
import com.willing.restdoc.swagger3.ComposeSwaggerTypeInspector;
import com.willing.restdoc.swagger3.Swagger3Configuration;
import com.willing.restdoc.swagger3.Swagger3DocGenerator;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SpringSwaggerConfiguration {

    @Bean
    IDocParser _docParser(@Autowired(required = false) SwaggerConfig swaggerConfig)
    {
        if (swaggerConfig == null)
        {
            swaggerConfig = SwaggerConfig.builder()
                    .apiDescription("API descritpion")
                    .apiTitle("API title")
                    .apiVersion("1.0-SNAPSHOT")
                    .packages(Arrays.asList(""))
                    .build();
        }
        var config = new DocParseConfiguration();
        var docConfig =  Swagger3Configuration.builder().description(swaggerConfig.getApiDescription()).title(swaggerConfig.getApiTitle())
                .servers(Arrays.asList(Swagger3Configuration.ServerInfo.builder().description("server").url("/")
                        .build()))
                .typeInspector(new ComposeSwaggerTypeInspector(Arrays.asList(new PrimitiveSwaggerTypeInspector())))
                .build();
        config.setDocGenerator(new Swagger3DocGenerator(docConfig));
        return new DocParser(config);
    }

    @Bean
    SpringSwaggerController _springSwaggerController(IDocParser docParser)
    {
        return new SpringSwaggerController(docParser);
    }
}
