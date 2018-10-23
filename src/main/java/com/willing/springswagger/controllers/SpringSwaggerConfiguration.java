package com.willing.springswagger.controllers;

import com.willing.springswagger.parse.DocParseConfiguration;
import com.willing.springswagger.parse.IDocParser;
import com.willing.springswagger.parse.Swagger3Configuration;
import com.willing.springswagger.parse.impl.ComposeSwaggerTypeInspector;
import com.willing.springswagger.parse.impl.DocParser;
import com.willing.springswagger.parse.impl.PrimitiveSwaggerTypeInspector;
import com.willing.springswagger.parse.impl.Swagger3DocGenerator;
import lombok.Builder;
import lombok.Data;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConditionalOnWebApplication
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
        var config = new DocParseConfiguration(swaggerConfig.getPackages(), swaggerConfig.getFieldPrefix());
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
