package com.willing.springswagger.parse;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class DocParseConfiguration {
    private List<IClassResolver> _classResolvers = new ArrayList<>();
    private List<IClassParser> _classParsers = new ArrayList<>();
    private List<IMethodParser> _methodParsers = new ArrayList<>();
    private List<IMethodParameterParser> _methodParameterParsers = new ArrayList<>();
    private List<IReturnParser> _returnParsers = new ArrayList<>();
}
