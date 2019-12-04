package cn.willingxyz.restdoc.spring;

import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.impl.IgnoreApiControllerFilter;
import cn.willingxyz.restdoc.core.parse.impl.IgnoreApiMethodFilter;
import cn.willingxyz.restdoc.spring.parameter.parser.*;
import cn.willingxyz.restdoc.spring.parameter.resolver.SpringAnnotationParameterFilter;
import cn.willingxyz.restdoc.spring.parameter.resolver.SpringClassParameterFilter;
import cn.willingxyz.restdoc.spring.response.SpringResponseBodyReturnParser;

public class SpringRestDocParseConfig extends RestDocParseConfig {

    public SpringRestDocParseConfig()
    {
        getMethodParsers().add(new SpringMethodParser());
//        getMethodParameterParsers().add(new SpringMethodParameterParser(this));
        getMethodParameterParsers().add(new SpringMultipartParameterParser(this));
        getMethodParameterParsers().add(new SpringRequestBodyParameterParser(this));
        getMethodParameterParsers().add(new SpringPathVariableParameterParser(this));
        getMethodParameterParsers().add(new SpringRequestHeaderParameterParser(this));
        getMethodParameterParsers().add(new SpringRequestParamParameterParser(this));
        getMethodParameterParsers().add(new SpringPrimitiveParameterParser(this));

        getMethodFilters().add(new SpringMethodFilter());
        getMethodFilters().add(new IgnoreApiMethodFilter());
        getMethodParameterFilters().add(new SpringClassParameterFilter());
        getMethodParameterFilters().add(new SpringAnnotationParameterFilter());
        getControllerFilters().add(new IgnoreApiControllerFilter());

        getReturnParsers().add(new SpringResponseBodyReturnParser(this));

//        getControllerResolvers().add(new SpringControllerResolver(Arrays.asList("doc")));
    }
}
