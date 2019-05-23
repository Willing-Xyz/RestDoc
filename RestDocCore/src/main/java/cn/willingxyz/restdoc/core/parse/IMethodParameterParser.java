package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.models.ParameterModel;
import com.github.therapi.runtimejavadoc.ParamJavadoc;

import java.lang.reflect.Parameter;

public interface IMethodParameterParser {
    ParameterModel parse(Parameter parameter, ParamJavadoc paramJavadoc, ParameterModel parameterModel);
    boolean isSupport(Parameter parameter);
}
