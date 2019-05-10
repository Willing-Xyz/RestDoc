package com.willing.restdoc.spring.parameter.parser;

import com.willing.restdoc.core.models.ParameterModel;
import com.willing.restdoc.core.parse.RestDocParseConfig;
import com.willing.restdoc.core.parse.impl.AbstractMethodParameterParser;
import lombok.var;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class SpringRequestHeaderParameterParser extends AbstractMethodParameterParser {
    public SpringRequestHeaderParameterParser(RestDocParseConfig configuration) {
        super(configuration);
    }

    @Override
    protected ParameterModel.ParameterLocation getParameterLocation(Parameter parameter, Type actualParamType) {
        return ParameterModel.ParameterLocation.HEADER;
    }

    @Override
    protected boolean isRequired(Parameter parameter, Type actualParamType) {
        var requestHeaderAnno = AnnotatedElementUtils.getMergedAnnotation(parameter, RequestHeader.class);
        if (requestHeaderAnno != null)
        {
            return requestHeaderAnno.required();
        }
        return true;
    }

    @Override
    public boolean isSupport(Parameter parameter) {
        return AnnotatedElementUtils.hasAnnotation(parameter, RequestHeader.class);
    }
}
