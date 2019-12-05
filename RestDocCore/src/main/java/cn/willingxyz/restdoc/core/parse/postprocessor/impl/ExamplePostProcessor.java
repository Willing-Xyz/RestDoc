package cn.willingxyz.restdoc.core.parse.postprocessor.impl;

import cn.willingxyz.restdoc.core.annotations.Example;
import cn.willingxyz.restdoc.core.models.*;
import cn.willingxyz.restdoc.core.parse.postprocessor.IParameterPostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.IResponsePostProcessor;
import cn.willingxyz.restdoc.core.parse.utils.FormatUtils;
import cn.willingxyz.restdoc.core.parse.utils.RuntimeJavadocUtils;
import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.OtherJavadoc;
import com.google.auto.service.AutoService;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AutoService({IPropertyPostProcessor.class, IParameterPostProcessor.class, IResponsePostProcessor.class})
public class ExamplePostProcessor implements IPropertyPostProcessor, IParameterPostProcessor, IResponsePostProcessor {
    @Override
    public PropertyModel postProcess(PropertyModel propertyModel, TypeContext typeContext) {
        propertyModel.setExample(getPropertyExample(propertyModel.getPropertyItem()));
        return propertyModel;
    }

    @Override
    public ParameterModel postProcess(ParameterModel model, Parameter parameter) {
        model.setExample(getParameterExample(parameter));
        return model;
    }

    @Override
    public ResponseModel postProcess(ResponseModel model, Method method) {
        model.getReturnModel().setExample(getResponseExample(method));
        return model;
    }

    private String getPropertyExample(PropertyItem propertyItem)
    {
        String example = null;
        if (propertyItem.getField() != null)
        {
            example = RuntimeJavadocUtils.getTagComment(propertyItem.getField(), "example");
            example = getExampleFromAnnotation(propertyItem.getField(), example);
        }
        if (example == null || example.trim().isEmpty())
        {
            if (propertyItem.getGetMethod() != null) {
                example = RuntimeJavadocUtils.getTagComment(propertyItem.getGetMethod(), "returnExample");
                example = getExampleFromAnnotation(propertyItem.getGetMethod(), example);
            }
        }
        if (example == null || example.trim().isEmpty())
        {
            if (propertyItem.getSetMethod() != null) {
                example = RuntimeJavadocUtils.getTagComment(propertyItem.getSetMethod(), "paramExample");
                example = getExampleFromAnnotation(propertyItem.getSetMethod(), example);
            }
        }
        return example;
    }
    private String getParameterExample(Parameter parameter)
    {
        String example = RuntimeJavadocUtils.getTagComment((Method) parameter.getDeclaringExecutable(), "paramExample", parameter.getName());
        return getExampleFromAnnotation(parameter, example);
    }
    private String getResponseExample(Method method)
    {
        String example = RuntimeJavadocUtils.getTagComment(method, "returnExample");
        return getExampleFromAnnotation(method, example);
    }
    private String getExampleFromAnnotation(AnnotatedElement element, String example)
    {
        if (example == null || example.trim().isEmpty())
        {
            Example exampleAnno = element.getAnnotation(Example.class);
            if (exampleAnno != null)
                example = exampleAnno.value();
        }
        return example;
    }
}
