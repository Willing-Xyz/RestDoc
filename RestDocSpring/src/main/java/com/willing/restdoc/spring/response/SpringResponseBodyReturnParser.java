package com.willing.restdoc.spring.response;

import com.willing.restdoc.core.models.ResponseModel;
import com.willing.restdoc.core.parse.RestDocParseConfig;
import com.willing.restdoc.core.parse.impl.AbstractMethodParameterParser;
import com.willing.restdoc.core.parse.impl.AbstractMethodReturnParser;
import lombok.var;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class SpringResponseBodyReturnParser extends AbstractMethodReturnParser {
    public SpringResponseBodyReturnParser(RestDocParseConfig configuration) {
        super(configuration);
    }

    @Override
    protected int parseStatusCode(Method method) {
        ResponseStatus responseStatusAnno = AnnotatedElementUtils.getMergedAnnotation(method, ResponseStatus.class);
        if (responseStatusAnno != null)
        {
            responseStatusAnno = AnnotatedElementUtils.getMergedAnnotation(method.getDeclaringClass(), ResponseStatus.class);
        }
        if (responseStatusAnno != null)
        {
            return responseStatusAnno.code() != null ? responseStatusAnno.code().value() : responseStatusAnno.value().value();
        }
        return 200;
    }
}
