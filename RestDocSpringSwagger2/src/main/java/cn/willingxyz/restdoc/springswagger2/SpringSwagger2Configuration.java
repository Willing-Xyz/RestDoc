package cn.willingxyz.restdoc.springswagger2;

import cn.willingxyz.restdoc.core.config.RestDocConfig;
import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import cn.willingxyz.restdoc.core.parse.impl.JavaTypeInspector;
import cn.willingxyz.restdoc.core.parse.impl.RestDocParser;
import cn.willingxyz.restdoc.core.parse.impl.TypeNameParser;
import cn.willingxyz.restdoc.spring.SpringControllerResolver;
import cn.willingxyz.restdoc.spring.SpringRestDocParseConfig;
import cn.willingxyz.restdoc.swagger.common.PrimitiveSwaggerTypeInspector;
import cn.willingxyz.restdoc.swagger.common.SwaggerGeneratorConfig;
import cn.willingxyz.restdoc.swagger.common.SwaggerUIConfiguration;
import cn.willingxyz.restdoc.swagger2.Swagger2GeneratorConfig;
import cn.willingxyz.restdoc.swagger2.RestDocConfigSwagger2Ext;
import cn.willingxyz.restdoc.swagger2.Swagger2RestDocGenerator;
import lombok.var;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SpringSwagger2Configuration {

    @Bean("swagger2")
    IRestDocParser _docParser(@Autowired(required = false) RestDocConfig restDocConfig, @Autowired(required = false) RestDocConfigSwagger2Ext ext) {
        if (restDocConfig == null) {
            restDocConfig = RestDocConfig.builder()
                    .apiDescription("API descritpion")
                    .apiTitle("API title")
                    .apiVersion("1.0-SNAPSHOT")
                    .packages(Arrays.asList(""))
//                    .servers(Arrays.asList(RestDocConfig.Server.builder().url("localhost:8080").description("local").build()))
                    .build();
        }
        var config = new SpringRestDocParseConfig();

        // todo 从spring容器中获取实例
        var docConfig = new Swagger2GeneratorConfig();
        docConfig.setDescription(restDocConfig.getApiDescription());
        docConfig.setTitle(restDocConfig.getApiTitle());
        docConfig.setVersion(restDocConfig.getApiVersion());
        docConfig.setServers(convertServers(restDocConfig.getServers()));
        docConfig.setSwaggerTypeInspector(new PrimitiveSwaggerTypeInspector());
        docConfig.setTagDescriptionAsName(restDocConfig.isTagDescriptionAsName());
        docConfig.setTypeInspector(new JavaTypeInspector());
        docConfig.setTypeNameParser(new TypeNameParser());
        docConfig.setHideEmptyController(restDocConfig.isHideEmptyController());
        docConfig.setSwaggerFilters(ext.getSwaggerFilters());

        config.getControllerResolvers().add(new SpringControllerResolver(restDocConfig.getPackages()));
        config.setRestDocGenerator(new Swagger2RestDocGenerator(docConfig));
        config.setFieldPrefix(restDocConfig.getFieldPrefix());
        return new RestDocParser(config);
    }

    private List<SwaggerGeneratorConfig.ServerInfo> convertServers(List<RestDocConfig.Server> servers) {
        if (servers == null || servers.size() <= 0) {
            return Collections.singletonList(SwaggerGeneratorConfig.ServerInfo.builder().description("server").url("/").build());
        }
        List<SwaggerGeneratorConfig.ServerInfo> serverInfos = new ArrayList<>();
        for (RestDocConfig.Server server : servers) {
            SwaggerGeneratorConfig.ServerInfo serverInfo =
                    SwaggerGeneratorConfig.ServerInfo.builder().description(server.getDescription()).url(server.getUrl())
                            .build();

            serverInfos.add(serverInfo);
        }
        return serverInfos;
    }

    @Bean
    SpringSwagger2Controller _springSwagger2Controller(@Qualifier("swagger2") IRestDocParser docParser) {
        SwaggerUIConfiguration uiConfiguration;
        try {
            uiConfiguration = _applicationContext.getBean(SwaggerUIConfiguration.class);
        } catch (NoSuchBeanDefinitionException e) {
            uiConfiguration = new SwaggerUIConfiguration();
        }
        var controller = new SpringSwagger2Controller(docParser, uiConfiguration);
//        controller.setUiConfiguration(uiConfiguration);
        return controller;
    }

    @Autowired
    ApplicationContext _applicationContext;
}
