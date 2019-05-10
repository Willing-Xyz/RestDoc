package com.willing.restdoc.spring.parameter.parser;

import com.willing.restdoc.core.models.ParameterModel;
import com.willing.restdoc.core.parse.RestDocParseConfig;
import com.willing.restdoc.core.parse.impl.AbstractMethodParameterParser;
import lombok.var;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class SpringRequestBodyParameterParser extends AbstractMethodParameterParser {

    public SpringRequestBodyParameterParser(RestDocParseConfig configuration) {
        super(configuration);
    }

    @Override
    protected ParameterModel.ParameterLocation getParameterLocation(Parameter parameter, Type actualParamType) {
        return ParameterModel.ParameterLocation.BODY;
    }

    @Override
    protected boolean isRequired(Parameter parameter, Type actualParamType) {
        var requestBodyAnno = AnnotatedElementUtils.getMergedAnnotation(parameter, RequestBody.class);
        if (requestBodyAnno != null)
        {
            return requestBodyAnno.required();
        }
        return true;
    }

    @Override
    public boolean isSupport(Parameter parameter) {
        return AnnotatedElementUtils.hasAnnotation(parameter, RequestBody.class);
    }
}
