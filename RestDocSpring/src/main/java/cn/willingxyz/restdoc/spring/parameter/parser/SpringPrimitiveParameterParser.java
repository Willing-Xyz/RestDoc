package cn.willingxyz.restdoc.spring.parameter.parser;

import cn.willingxyz.restdoc.core.models.ParameterModel;
import cn.willingxyz.restdoc.core.parse.IMethodParameterParser;
import cn.willingxyz.restdoc.core.parse.impl.AbstractMethodParameterParser;
import com.google.auto.service.AutoService;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

@AutoService(IMethodParameterParser.class)
public class SpringPrimitiveParameterParser extends AbstractMethodParameterParser {

    @Override
    protected ParameterModel.ParameterLocation getParameterLocation(Parameter parameter, Type actualParamType) {
        return ParameterModel.ParameterLocation.QUERY;
    }

    @Override
    protected boolean isRequired(Parameter parameter, Type actualParamType) {
        return false;
    }

    @Override
    public boolean isSupport(Parameter parameter) {
        return true;
    }
}
