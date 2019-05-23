package cn.willingxyz.restdoc.spring.parameter.parser;

import cn.willingxyz.restdoc.core.models.ParameterModel;
import cn.willingxyz.restdoc.core.parse.RestDocParseConfig;
import cn.willingxyz.restdoc.core.parse.impl.AbstractMethodParameterParser;

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
