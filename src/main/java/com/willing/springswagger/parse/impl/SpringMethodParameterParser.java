package com.willing.springswagger.parse.impl;

import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import com.willing.springswagger.models.ParameterModel;
import com.willing.springswagger.models.PropertyModel;
import com.willing.springswagger.parse.DocParseConfiguration;
import com.willing.springswagger.parse.utils.ClassUtils;
import com.willing.springswagger.parse.utils.FormatUtils;
import com.willing.springswagger.parse.IMethodParameterParser;
import lombok.var;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

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
            parameterModel.setLocation(ParameterModel.ParameterLocation.QUERY);
        }
        parameterModel.setParameterClass(parameter.getType());

        // todo required
        parameterModel.setChildren(ClassUtils.parseProperty(_configuration, parameter.getType(), 0));

        return parameterModel;
    }


}
