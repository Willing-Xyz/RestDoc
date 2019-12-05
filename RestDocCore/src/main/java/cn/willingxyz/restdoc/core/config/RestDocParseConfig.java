package cn.willingxyz.restdoc.core.config;

import cn.willingxyz.restdoc.core.parse.*;
import cn.willingxyz.restdoc.core.parse.impl.*;
import cn.willingxyz.restdoc.core.parse.postprocessor.IParameterPostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.IResponsePostProcessor;
import cn.willingxyz.restdoc.core.parse.postprocessor.impl.*;
import cn.willingxyz.restdoc.core.utils.ServiceLoaders;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

import static cn.willingxyz.restdoc.core.utils.ServiceLoaders.loadServices;

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

    private IRestDocGenerator _restDocGenerator;

    public RestDocParseConfig()
    {
        _controllerResolvers = loadServices(IControllerResolver.class, this);
        _controllerFilters = loadServices(IControllerFilter.class, this);
        _controllerParsers = loadServices(IControllerParser.class, this);

        _methodParsers = loadServices(IMethodParser.class, this);
        _methodFilters = loadServices(IMethodFilter.class, this);

        _methodParameterFilters = loadServices(IMethodParameterFilter.class, this);
        _methodParameterParsers = loadServices(IMethodParameterParser.class, this);

        _returnParsers = loadServices(IMethodReturnParser.class, this);

        _propertyPostProcessor = new ComposePropertyPostProcessor();
        _parameterPostProcessor = new ComposeParameterPostProcessor();
        _responsePostProcessor = new ComposeResponsePostProcessor();
        _typeInspector = new ComposeTypeInspector();

        loadServices(IPropertyPostProcessor.class, this).forEach(o -> _propertyPostProcessor.add(o));
        loadServices(IParameterPostProcessor.class, this).forEach(o -> _parameterPostProcessor.add(o));
        loadServices(IResponsePostProcessor.class, this).forEach(o -> _responsePostProcessor.add(o));
        loadServices(ITypeInspector.class, this).forEach(o -> _typeInspector.add(o));

        _propertyResolver = new PropertyResolver(this);
        _propertyParser = new PropertyParser(this, _propertyResolver);
        _typeParser = new TypeParser(_propertyResolver, _propertyParser, _propertyPostProcessor);
    }


}
