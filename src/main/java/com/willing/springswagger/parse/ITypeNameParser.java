package com.willing.springswagger.parse;

import java.lang.reflect.Type;

public interface ITypeNameParser {
    String parse(Type type);
}
