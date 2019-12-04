package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.parse.impl.*;
import cn.willingxyz.restdoc.core.parse.postprocessor.IParameterPostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.IResponsePostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.impl.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

@Data
public  class RestDocParseConfig {
    private List<IControllerResolver> _controllerResolvers = new ArrayList<>();
    private List<IControllerFilter> _controllerFilters = new ArrayList<>();
    private List<IControllerParser> _controllerParsers = new ArrayList<>();

    private List<IMethodFilter> _methodFilters = new ArrayList<>();
    private List<IMethodParser> _methodParsers = new ArrayList<>();

    private List<IMethodParameterFilter> _methodParameterFilters = new ArrayList<>();
    private List<IMethodParameterParser> _methodParameterParsers = new ArrayList<>();

    private List<IMethodReturnParser> _returnParsers = new ArrayList<>();

    private ITypeParser _typeParser;
    private IPropertyResolver _propertyResolver;
    private IPropertyParser _propertyParser;

    private ComposePropertyPostProcessor _propertyPostProcessor;
    private ComposeParameterPostProcessor _parameterPostProcessor;
    private ComposeResponsePostProcessor _responsePostProcessor;

    private ComposeTypeInspector _typeInspector;

    private String _fieldPrefix;

    private IRestDocGenerator _restDocGenerator;

    public RestDocParseConfig()
    {
        _controllerParsers.add(new JavadocControllerParser());
        _methodParsers.add(new JavadocMethodParser());
//        _returnParsers.add(new MethodReturnParser(this));
        _typeInspector = new ComposeTypeInspector(Arrays.asList(new JavaTypeInspector()));

        _propertyResolver = new PropertyResolver(this);
        _propertyParser = new PropertyParser(this, _propertyResolver);

        _propertyPostProcessor = new ComposePropertyPostProcessor();
        _parameterPostProcessor = new ComposeParameterPostProcessor();
        _responsePostProcessor = new ComposeResponsePostProcessor();

        _propertyPostProcessor.add(new RequiredPropertyPostProcessor());

        _propertyPostProcessor.add(new ExamplePostProcessor());
        _parameterPostProcessor.add(new ExamplePostProcessor());
        _responsePostProcessor.add(new ExamplePostProcessor());

        loadPropertyPostProcessors().forEach(o -> _propertyPostProcessor.add(o));
        loadParameterPostProcessors().forEach(o -> _parameterPostProcessor.add(o));
        loadResponsePostProcessors().forEach(o -> _responsePostProcessor.add(o));

        _typeParser = new TypeParser(_propertyResolver, _propertyParser, _propertyPostProcessor);
    }

    private List<IPropertyPostProcessor> loadPropertyPostProcessors()
    {
        List<IPropertyPostProcessor> processors = new ArrayList<>();
        ServiceLoader<IPropertyPostProcessor> serviceLoader = ServiceLoader.load(IPropertyPostProcessor.class);
        serviceLoader.forEach(o -> {
            processors.add(o);
        });
        return processors;
    }

    private List<IParameterPostProcessor> loadParameterPostProcessors()
    {
        List<IParameterPostProcessor> processors = new ArrayList<>();
        ServiceLoader<IParameterPostProcessor> serviceLoader = ServiceLoader.load(IParameterPostProcessor.class);
        serviceLoader.forEach(o -> {
            processors.add(o);
        });
        return processors;
    }

    private List<IResponsePostProcessor> loadResponsePostProcessors()
    {
        List<IResponsePostProcessor> processors = new ArrayList<>();
        ServiceLoader<IResponsePostProcessor> serviceLoader = ServiceLoader.load(IResponsePostProcessor.class);
        serviceLoader.forEach(o -> {
            processors.add(o);
        });
        return processors;
    }
}
