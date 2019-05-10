package com.willing.restdoc.spring;

import com.github.therapi.runtimejavadoc.ParamJavadoc;
import com.willing.restdoc.core.models.ParameterModel;
import com.willing.restdoc.core.parse.RestDocParseConfig;
import com.willing.restdoc.core.parse.utils.TypeParseUtils;
import com.willing.restdoc.core.parse.utils.FormatUtils;
import com.willing.restdoc.core.parse.IMethodParameterParser;
import com.willing.restdoc.core.parse.utils.ReflectUtils;
import lombok.var;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Parameter;

//public class SpringMethodParameterParser implements IMethodParameterParser {
//    private final RestDocParseConfig _configuration;
//
//    public SpringMethodParameterParser(RestDocParseConfig configuration) {
//        _configuration = configuration;
//    }
//
//    @Override
//    public ParameterModel parse(Parameter parameter, ParamJavadoc paramJavadoc, ParameterModel parameterModel) {
//        parameterModel.setName(parameter.getName());
//        if (paramJavadoc != null)
//        {
//            parameterModel.setDescription(FormatUtils.format(paramJavadoc.getComment()));
//        }
//        var requestBodyAnno = parameter.getAnnotation(RequestBody.class);
//        if (requestBodyAnno != null)
//        {
//            parameterModel.setLocation(ParameterModel.ParameterLocation.BODY);
//        }
//        else
//        {
//            var pathVariableAnno = parameter.getAnnotation(PathVariable.class);
//            if (pathVariableAnno != null)
//                parameterModel.setLocation(ParameterModel.ParameterLocation.PATH);
//            else
//                parameterModel.setLocation(ParameterModel.ParameterLocation.QUERY);
//        }
//        parameterModel.setParameterType(parameter.getParameterizedType());
//
//        boolean isArray = ReflectUtils.isArray(parameter.getParameterizedType());
//        parameterModel.setArray(isArray);
//
//        // todo required.
//        if (!isArray) {
//            parameterModel.setChildren(TypeParseUtils.parseTypeProperty(_configuration, parameter.getParameterizedType()));
//        }
//        else
//        {
//            parameterModel.setChildren(TypeParseUtils.parseTypeProperty(_configuration, ReflectUtils.getArrayComponentType(parameter.getParameterizedType())));
//        }
//
//        return parameterModel;
//    }
//
//    @Override
//    public boolean isSupport(Parameter parameter) {
//        return false;
//    }
//}
