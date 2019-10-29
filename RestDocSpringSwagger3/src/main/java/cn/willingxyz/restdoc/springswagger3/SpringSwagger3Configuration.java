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
import cn.willingxyz.restdoc.swagger.common.SwaggerUIConfiguration;
import cn.willingxyz.restdoc.swagger3.Swagger3RestDocGenerator;
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
        SwaggerGeneratorConfig docConfig =  SwaggerGeneratorConfig.builder().description(restDocConfig.getApiDescription()).title(restDocConfig.getApiTitle())
                .version(restDocConfig.getApiVersion())
                .servers(convertServers(restDocConfig.getServers()))
                .swaggerTypeInspector(new PrimitiveSwaggerTypeInspector())
                .typeInspector(new JavaTypeInspector())
                .typeNameParser(new TypeNameParser(restDocConfig.isResolveJavaDocAsTypeName()))
                .tagDescriptionAsName(restDocConfig.isTagDescriptionAsName())
                .resolveJavaDocAsTypeName(restDocConfig.isResolveJavaDocAsTypeName())
                .hideEmptyController(restDocConfig.isHideEmptyController())
                .build();
        config.getControllerResolvers().add(new SpringControllerResolver(restDocConfig.getPackages()));
        config.setRestDocGenerator(new Swagger3RestDocGenerator(docConfig));
        config.setFieldPrefix(restDocConfig.getFieldPrefix());
        return new RestDocParser(config);
    }

    private List<SwaggerGeneratorConfig.ServerInfo> convertServers(List<RestDocConfig.Server> servers) {
        if(servers==null || servers.size()<=0){
            return Collections.singletonList(SwaggerGeneratorConfig.ServerInfo.builder().description("server").url("/").build());
        }
        List<SwaggerGeneratorConfig.ServerInfo> serverInfos = new ArrayList<>();
        for (RestDocConfig.Server server :servers)
        {
            String url = server.getUrl();
            if (!url.startsWith("http"))
            {
                url = "http://" + url;
            }
            SwaggerGeneratorConfig.ServerInfo serverInfo =
                    SwaggerGeneratorConfig.ServerInfo.builder().description(server.getDescription()).url(url)
                            .build();

            serverInfos.add(serverInfo);
        }
        return serverInfos;
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
        return controller;
    }

    @Autowired
    ApplicationContext _applicationContext;
}
