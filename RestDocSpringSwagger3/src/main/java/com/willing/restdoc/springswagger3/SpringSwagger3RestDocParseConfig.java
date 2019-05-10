package com.willing.restdoc.springswagger3;

import com.willing.restdoc.core.parse.RestDocParseConfig;
import com.willing.restdoc.spring.parameter.parser.*;
import com.willing.restdoc.spring.parameter.resolver.SpringAnnotationParameterResolver;
import com.willing.restdoc.spring.parameter.resolver.SpringClassParameterResolver;
import com.willing.restdoc.spring.SpringMethodParser;
import com.willing.restdoc.spring.SpringMethodResolver;
import com.willing.restdoc.spring.response.SpringResponseBodyReturnParser;

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
