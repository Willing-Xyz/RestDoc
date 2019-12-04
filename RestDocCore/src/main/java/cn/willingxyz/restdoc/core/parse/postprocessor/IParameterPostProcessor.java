package cn.willingxyz.restdoc.core.parse.postprocessor;

import cn.willingxyz.restdoc.core.models.ParameterModel;

import java.lang.reflect.Parameter;

public interface IParameterPostProcessor {
    ParameterModel postProcess(ParameterModel model, Parameter parameter);
}
