package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.parse.impl.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

@Data
public  class RestDocParseConfig {
    private List<IControllerResolver> _controllerResolvers = new ArrayList<>();
    private List<IControllerFilter> _controllerFilters = new ArrayList<>();
    private List<IMethodResolver> _methodResolvers = new ArrayList<>();
    private List<IMethodParameterResolver> _methodParameterResolvers = new ArrayList<>();

    private List<IControllerParser> _controllerParsers = new ArrayList<>();
    private List<IMethodParser> _methodParsers = new ArrayList<>();
    private List<IMethodParameterParser> _methodParameterParsers = new ArrayList<>();
    private List<IMethodReturnParser> _returnParsers = new ArrayList<>();

    private IPropertyResolver _propertyResolver;
    private IPropertyParser _propertyParser;
    private ComposePropertyPostProcessor _propertyPostProcessor;
    private ITypeParser _typeParser;

    private ITypeInspector _typeInspector;

    private String _fieldPrefix;

    private IRestDocGenerator _restDocGenerator;

    public RestDocParseConfig()
    {
        _controllerParsers.add(new JavadocControllerParser());
        _methodParsers.add(new JavadocMethodParser());
//        _returnParsers.add(new MethodReturnParser(this));
        _typeInspector = new JavaTypeInspector();

        _propertyResolver = new PropertyResolver(this);
        _propertyParser = new PropertyParser(this, _propertyResolver);
        _propertyPostProcessor = new ComposePropertyPostProcessor();
        _propertyPostProcessor.add(new RequiredPropertyPostProcessor(_typeInspector));

        loadPropertyPostProcessors().forEach(o -> _propertyPostProcessor.add(o));

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
}
