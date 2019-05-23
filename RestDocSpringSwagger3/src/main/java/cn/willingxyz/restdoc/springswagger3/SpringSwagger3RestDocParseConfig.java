package cn.willingxyz.restdoc.springswagger3;

import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import cn.willingxyz.restdoc.spring.parameter.parser.*;
import cn.willingxyz.restdoc.spring.parameter.resolver.SpringAnnotationParameterResolver;
import cn.willingxyz.restdoc.spring.parameter.resolver.SpringClassParameterResolver;
import cn.willingxyz.restdoc.spring.SpringMethodParser;
import cn.willingxyz.restdoc.spring.SpringMethodResolver;
import cn.willingxyz.restdoc.spring.response.SpringResponseBodyReturnParser;

public class SpringSwagger3RestDocParseConfig extends RestDocParseConfig {

    public SpringSwagger3RestDocParseConfig()
    {
        getMethodParsers().add(new SpringMethodParser());
//        getMethodParameterParsers().add(new SpringMethodParameterParser(this));
        getMethodParameterParsers().add(new SpringMultipartParameterParser(this));
        getMethodParameterParsers().add(new SpringRequestBodyParameterParser(this));
        getMethodParameterParsers().add(new SpringPathVariableParameterParser(this));
        getMethodParameterParsers().add(new SpringRequestHeaderParameterParser(this));
        getMethodParameterParsers().add(new SpringRequestParamParameterParser(this));
        getMethodParameterParsers().add(new SpringPrimitiveParameterParser(this));

        getMethodResolvers().add(new SpringMethodResolver());
        getMethodParameterResolvers().add(new SpringClassParameterResolver());
        getMethodParameterResolvers().add(new SpringAnnotationParameterResolver());

        getReturnParsers().add(new SpringResponseBodyReturnParser(this));

//        getControllerResolvers().add(new SpringControllerResolver(Arrays.asList("doc")));
    }
}
