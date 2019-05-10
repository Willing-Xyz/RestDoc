package com.willing.restdoc.spring.parameter.parser;

import com.willing.restdoc.core.models.ParameterModel;
import com.willing.restdoc.core.parse.RestDocParseConfig;
import com.willing.restdoc.core.parse.impl.AbstractMethodParameterParser;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class SpringPrimitiveParameterParser extends AbstractMethodParameterParser {
    public SpringPrimitiveParameterParser(RestDocParseConfig configuration) {
        super(configuration);
    }

    @Override
    protected ParameterModel.ParameterLocation getParameterLocation(Parameter parameter, Type actualParamType) {
        return ParameterModel.ParameterLocation.QUERY;
    }

    @Override
    protected boolean isRequired(Parameter parameter, Type actualParamType) {
        return true;
    }

    @Override
    public boolean isSupport(Parameter parameter) {
        return true;
    }
}
