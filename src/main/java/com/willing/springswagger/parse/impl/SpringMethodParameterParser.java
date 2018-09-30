package com.willing.springswagger.parse.impl;

import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.willing.springswagger.models.ParameterModel;
import com.willing.springswagger.models.ParameterType;
import com.willing.springswagger.parse.utils.FormatUtils;
import com.willing.springswagger.parse.IMethodParameterParser;
import lombok.var;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Parameter;
import java.util.HashMap;

public class SpringMethodParameterParser implements IMethodParameterParser {
    @Override
    public ParameterModel parse(Parameter parameter, ParamJavadoc paramJavadoc, ParameterModel parameterModel) {
        if (paramJavadoc != null)
        {
            parameterModel.setName(paramJavadoc.getName());
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
        setParameterType(parameterModel, parameter);
        return parameterModel;
    }

    private void setParameterType(ParameterModel parameterModel, Parameter parameter) {
        // todo
        var parameterType = parameter.getType();
        var map = new HashMap<Class, ParameterType>();
        map.put(String.class, ParameterType.STRING);
        map.put(Boolean.class, ParameterType.BOOLEAN);
        map.put(boolean.class, ParameterType.BOOLEAN);
        map.put(Integer.class, ParameterType.INTEGER);
        map.put(int.class, ParameterType.INTEGER);
        map.put(long.class, ParameterType.INTEGER);
        map.put(Long.class, ParameterType.INTEGER);

        var mapValue = map.get(parameterType);
        if (mapValue == null)
            parameterModel.setType(ParameterType.OBJECT);
        else
            parameterModel.setType(mapValue);
    }
}
