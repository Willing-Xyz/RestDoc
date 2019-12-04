package cn.willingxyz.restdoc.core.config;

import cn.willingxyz.restdoc.core.parse.*;
import cn.willingxyz.restdoc.core.parse.impl.*;
import cn.willingxyz.restdoc.core.parse.postprocessor.IParameterPostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.IResponsePostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.impl.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@Data
public  class RestDocParseConfig {
    private List<IControllerResolver> _controllerResolvers;
    private List<IControllerFilter> _controllerFilters;
    private List<IControllerParser> _controllerParsers;

    private List<IMethodFilter> _methodFilters;
    private List<IMethodParser> _methodParsers;

    private List<IMethodParameterFilter> _methodParameterFilters;
    private List<IMethodParameterParser> _methodParameterParsers;

    private List<IMethodReturnParser> _returnParsers;

    private ITypeParser _typeParser;
    private IPropertyResolver _propertyResolver;
    private IPropertyParser _propertyParser;

    private ComposePropertyPostProcessor _propertyPostProcessor;
    private ComposeParameterPostProcessor _parameterPostProcessor;
    private ComposeResponsePostProcessor _responsePostProcessor;

    private ComposeTypeInspector _typeInspector;

    private String _fieldPrefix;
    private List<String> _packages = new ArrayList<>();

    private IRestDocGenerator _restDocGenerator;

    public RestDocParseConfig()
    {
        _controllerResolvers = loadServices(IControllerResolver.class);
        _controllerFilters = loadServices(IControllerFilter.class);
        _controllerParsers = loadServices(IControllerParser.class);

        _methodParsers = loadServices(IMethodParser.class);
        _methodFilters = loadServices(IMethodFilter.class);

        _methodParameterFilters = loadServices(IMethodParameterFilter.class);
        _methodParameterParsers = loadServices(IMethodParameterParser.class);

        _returnParsers = loadServices(IMethodReturnParser.class);

        _propertyPostProcessor = new ComposePropertyPostProcessor();
        _parameterPostProcessor = new ComposeParameterPostProcessor();
        _responsePostProcessor = new ComposeResponsePostProcessor();
        _typeInspector = new ComposeTypeInspector();

        loadServices(IPropertyPostProcessor.class).forEach(o -> _propertyPostProcessor.add(o));
        loadServices(IParameterPostProcessor.class).forEach(o -> _parameterPostProcessor.add(o));
        loadServices(IResponsePostProcessor.class).forEach(o -> _responsePostProcessor.add(o));
        loadServices(ITypeInspector.class).forEach(o -> _typeInspector.add(o));

        _propertyResolver = new PropertyResolver(this);
        _propertyParser = new PropertyParser(this, _propertyResolver);
        _typeParser = new TypeParser(_propertyResolver, _propertyParser, _propertyPostProcessor);
    }

    private <T> List<T> loadServices(Class<T> clazz)
    {
        List<T> processors = new ArrayList<>();
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        serviceLoader.forEach(o -> {
            if (o instanceof IRestDocParseConfigAware)
            {
                IRestDocParseConfigAware configAware = (IRestDocParseConfigAware) o;
                configAware.setRestDocParseConfig(this);
            }
            processors.add(o);
        });
        return processors;
    }
}
