package cn.willingxyz.restdoc.core.parse.postprocessor.impl;

import cn.willingxyz.restdoc.core.models.ParameterModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IParameterPostProcessor;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;

public class ComposeParameterPostProcessor implements IParameterPostProcessor {
    private ArrayList<IParameterPostProcessor> _processors;

    public ComposeParameterPostProcessor(IParameterPostProcessor... processors)
    {
        _processors = new ArrayList<>(Arrays.asList(processors));
    }
    public void add(IParameterPostProcessor processor)
    {
        _processors.add(processor);
    }

    @Override
    public ParameterModel postProcess(ParameterModel model, Parameter parameter) {
        for (IParameterPostProcessor postProcessor : _processors)
        {
            model = postProcessor.postProcess(model, parameter);
            if (model == null)
                return null;
        }
        return model;
    }
}
