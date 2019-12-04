package cn.willingxyz.restdoc.core.parse.postprocessor.impl;

import cn.willingxyz.restdoc.core.models.ResponseModel;
import cn.willingxyz.restdoc.core.parse.postprocessor.IResponsePostProcessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class ComposeResponsePostProcessor implements IResponsePostProcessor {
    private ArrayList<IResponsePostProcessor> _processors;

    public ComposeResponsePostProcessor(IResponsePostProcessor... processors)
    {
        _processors = new ArrayList<>(Arrays.asList(processors));
    }
    public void add(IResponsePostProcessor processor)
    {
        _processors.add(processor);
    }

    @Override
    public ResponseModel postProcess(ResponseModel model, Method method) {
        for (IResponsePostProcessor postProcessor : _processors)
        {
            model = postProcessor.postProcess(model, method);
            if (model == null)
                return null;
        }
        return model;
    }
}
