package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.config.RestDocConfig;
import cn.willingxyz.restdoc.core.parse.IRestDocParser;
import cn.willingxyz.restdoc.core.parse.impl.JavaTypeInspector;
import cn.willingxyz.restdoc.core.parse.impl.RestDocParser;
import cn.willingxyz.restdoc.swagger.common.TypeNameParser;
import cn.willingxyz.restdoc.spring.SpringControllerResolver;
import cn.willingxyz.restdoc.spring.SpringRestDocParseConfig;
import cn.willingxyz.restdoc.spring.filter.HttpBasicAuthFilter;
import cn.willingxyz.restdoc.swagger.common.PrimitiveSwaggerTypeInspector;
import cn.willingxyz.restdoc.swagger.common.SwaggerGeneratorConfig;
import cn.willingxyz.restdoc.swagger.common.SwaggerUIConfiguration;
import cn.willingxyz.restdoc.swagger3.*;
import lombok.var;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
    IRestDocParser _docParser(@Autowired(required = false) RestDocConfig restDocConfig, @Autowired(required = false) RestDocConfigSwagger3Ext ext) {
        if (restDocConfig == null) {
            restDocConfig = RestDocConfig.builder()
                    .apiDescription("API descritpion")
                    .apiTitle("API title")
                    .apiVersion("1.0-SNAPSHOT")
                    .packages(Arrays.asList(""))
                    .build();
        }
        var parseConfig = new SpringRestDocParseConfig();
        parseConfig.getControllerResolvers().add(new SpringControllerResolver(restDocConfig.getPackages()));



        // todo 从spring容器中获取实例
        Swagger3GeneratorConfig generatorConfig = new Swagger3GeneratorConfig(parseConfig);
        generatorConfig.setDescription(restDocConfig.getApiDescription());
        generatorConfig.setTitle(restDocConfig.getApiTitle());
        generatorConfig.setVersion(restDocConfig.getApiVersion());
        generatorConfig.setServers(convertServers(restDocConfig.getServers()));
        generatorConfig.setSwaggerTypeInspector(new PrimitiveSwaggerTypeInspector());
        generatorConfig.setTypeInspector(new JavaTypeInspector());
        generatorConfig.setTypeNameParser(new TypeNameParser(restDocConfig.isResolveJavaDocAsTypeName()));
        generatorConfig.setResolveJavaDocAsTypeName(restDocConfig.isResolveJavaDocAsTypeName());

        if (restDocConfig.isTagDescriptionAsName()) {
            List<IOpenAPIFilter> openAPIFilters = new ArrayList<>(ext.getOpenAPIFilters());
            openAPIFilters.add(new TagDescriptionAsNameOpenAPIFilter());
            ext.setOpenAPIFilters(openAPIFilters);
        }
        if (restDocConfig.isHideEmptyController()) {
            List<IOpenAPIFilter> openAPIFilters = new ArrayList<>(ext.getOpenAPIFilters());
            openAPIFilters.add(new HideEmptyControllerOpenAPIFilter());
            ext.setOpenAPIFilters(openAPIFilters);
        }
        if(ext!=null)
            generatorConfig.setOpenAPIFilters(ext.getOpenAPIFilters());

        parseConfig.setRestDocGenerator(new Swagger3RestDocGenerator(generatorConfig));
        parseConfig.setFieldPrefix(restDocConfig.getFieldPrefix());

        return new RestDocParser(parseConfig);
    }

    private List<SwaggerGeneratorConfig.ServerInfo> convertServers(List<RestDocConfig.Server> servers) {
        if (servers == null || servers.size() <= 0) {
            return Collections.singletonList(SwaggerGeneratorConfig.ServerInfo.builder().description("server").url("/").build());
        }
        List<SwaggerGeneratorConfig.ServerInfo> serverInfos = new ArrayList<>();
        for (RestDocConfig.Server server : servers) {
            String url = server.getUrl();
            if (!url.startsWith("http")) {
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
    SpringSwagger3Controller _springSwagger3Controller(@Qualifier("swagger3") IRestDocParser docParser) {
        SwaggerUIConfiguration uiConfiguration;
        try {
            uiConfiguration = _applicationContext.getBean(SwaggerUIConfiguration.class);
        } catch (NoSuchBeanDefinitionException e) {
            uiConfiguration = new SwaggerUIConfiguration();
        }
        var controller = new SpringSwagger3Controller(docParser, uiConfiguration);
        return controller;
    }

    @Bean
    @ConditionalOnClass(FilterRegistrationBean.class)
    @ConditionalOnMissingBean(RestDocConfig.HttpBasicAuth.class)
    public FilterRegistrationBean<HttpBasicAuthFilter> swagger3HttpFilter(@Autowired(required = false) RestDocConfig restDocConfig) {
        RestDocConfig.HttpBasicAuth httpBasicAuth;
        if (restDocConfig == null || (httpBasicAuth = restDocConfig.getHttpBasicAuth()) == null)
            httpBasicAuth = new RestDocConfig.HttpBasicAuth(null, null);

        FilterRegistrationBean<HttpBasicAuthFilter> filterBean = new FilterRegistrationBean<>();
        HttpBasicAuthFilter authFilter = new HttpBasicAuthFilter(httpBasicAuth.getUsername(), httpBasicAuth.getPassword());
        filterBean.addUrlPatterns("/swagger2-ui/**","/swagger2.json","/swagger-ui/*","/swagger.json");
        filterBean.setFilter(authFilter);
        return filterBean;
    }

    @Autowired
    ApplicationContext _applicationContext;
}
