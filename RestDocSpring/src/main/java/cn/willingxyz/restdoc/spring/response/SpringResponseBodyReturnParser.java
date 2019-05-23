package cn.willingxyz.restdoc.spring.response;

import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.impl.AbstractMethodReturnParser;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;

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
