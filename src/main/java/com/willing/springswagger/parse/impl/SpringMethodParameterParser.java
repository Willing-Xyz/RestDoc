package com.willing.springswagger.parse.impl;

import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.willing.springswagger.models.ParameterModel;
import com.willing.springswagger.parse.DocParseConfiguration;
import com.willing.springswagger.parse.utils.ClassUtils;
import com.willing.springswagger.parse.utils.FormatUtils;
import com.willing.springswagger.parse.IMethodParameterParser;
import com.willing.springswagger.parse.utils.ReflectUtils;
import lombok.var;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Parameter;

public class SpringMethodParameterParser implements IMethodParameterParser {
    private final DocParseConfiguration _configuration;

    public SpringMethodParameterParser(DocParseConfiguration configuration) {
        _configuration = configuration;
    }

    @Override
    public ParameterModel parse(Parameter parameter, ParamJavadoc paramJavadoc, ParameterModel parameterModel) {
        parameterModel.setName(parameter.getName());
        if (paramJavadoc != null)
        {
            parameterModel.setDescription(FormatUtils.format(paramJavadoc.getComment()));
        }
        var requestBodyAnno = parameter.getAnnotation(RequestBody.class);
        if (requestBodyAnno != null)
        {
            parameterModel.setLocation(ParameterModel.ParameterLocation.BODY);
        }
        else
        {
            var pathVariableAnno = parameter.getAnnotation(PathVariable.class);
            if (pathVariableAnno != null)
                parameterModel.setLocation(ParameterModel.ParameterLocation.PATH);
            else
                parameterModel.setLocation(ParameterModel.ParameterLocation.QUERY);
        }
        parameterModel.setParameterType(parameter.getParameterizedType());

        boolean isArray = ReflectUtils.isArray(parameter.getParameterizedType());
        parameterModel.setArray(isArray);

        // todo required
        if (!isArray) {
            parameterModel.setChildren(ClassUtils.parseTypeProperty(_configuration, parameter.getParameterizedType()));
        }
        else
        {
            parameterModel.setChildren(ClassUtils.parseTypeProperty(_configuration, ReflectUtils.getArrayComponentType(parameter.getParameterizedType())));
        }

        return parameterModel;
    }
}
