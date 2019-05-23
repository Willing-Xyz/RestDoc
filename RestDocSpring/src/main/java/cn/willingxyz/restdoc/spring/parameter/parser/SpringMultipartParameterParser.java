package cn.willingxyz.restdoc.spring.parameter.parser;

import cn.willingxyz.restdoc.core.models.ParameterModel;
import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.impl.AbstractMethodParameterParser;
import lombok.var;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class SpringMultipartParameterParser extends AbstractMethodParameterParser {
    public SpringMultipartParameterParser(RestDocParseConfig configuration) {
        super(configuration);
    }

    @Override
    protected ParameterModel.ParameterLocation getParameterLocation(Parameter parameter, Type actualParamType) {
        return ParameterModel.ParameterLocation.FILE;
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
        if (MultipartFile.class.isAssignableFrom(parameter.getType())
            || Part.class.isAssignableFrom(parameter.getType()))
        {
            return true;
        }
        return false;
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
