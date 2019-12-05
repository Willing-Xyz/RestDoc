package cn.willingxyz.restdoc.spring.parameter.parser;

import cn.willingxyz.restdoc.core.config.ExtOrder;
import cn.willingxyz.restdoc.core.models.ParameterModel;
import cn.willingxyz.restdoc.core.parse.IMethodParameterParser;
import cn.willingxyz.restdoc.core.parse.impl.AbstractMethodParameterParser;
import com.google.auto.service.AutoService;
import lombok.var;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@AutoService(IMethodParameterParser.class)
@ExtOrder(200)
public class SpringRequestBodyParameterParser extends AbstractMethodParameterParser {

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
