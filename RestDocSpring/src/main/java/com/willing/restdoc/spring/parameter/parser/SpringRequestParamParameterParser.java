package com.willing.restdoc.spring.parameter.parser;

import com.willing.restdoc.core.models.ParameterModel;
import com.willing.restdoc.core.parse.RestDocParseConfig;
import com.willing.restdoc.core.parse.impl.AbstractMethodParameterParser;
import lombok.var;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class SpringRequestParamParameterParser extends AbstractMethodParameterParser{
    public SpringRequestParamParameterParser(RestDocParseConfig configuration) {
        super(configuration);
    }

    @Override
    protected ParameterModel.ParameterLocation getParameterLocation(Parameter parameter, Type actualParamType) {
        return ParameterModel.ParameterLocation.QUERY;
    }

    @Override
    protected boolean isRequired(Parameter parameter, Type actualParamType) {
        var requestParamAnno = AnnotatedElementUtils.getMergedAnnotation(parameter, RequestParam.class);
        if (requestParamAnno != null)
        {
            return requestParamAnno.required();
        }
        return true;
    }

    @Override
    public boolean isSupport(Parameter parameter) {
        return AnnotatedElementUtils.hasAnnotation(parameter, RequestParam.class);
    }

    @Override
    protected String getParameterName(Parameter parameter) {
        var paramName = super.getParameterName(parameter);

        var requestParamAnno = AnnotatedElementUtils.getMergedAnnotation(parameter, RequestParam.class);
        if (requestParamAnno != null)
        {
            return requestParamAnno.name();
        }
        return paramName;
    }
}
