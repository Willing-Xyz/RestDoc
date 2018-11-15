package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.ITypeNameParser;
import lombok.var;

import java.lang.reflect.Type;
import java.util.Stack;

public class TypeNameParser implements ITypeNameParser {
    @Override
    public String parse(Type type) {
        System.out.println(type);
        var stack = new Stack<Character>();
        StringBuilder tmp = null;
        for (int i = type.getTypeName().length() - 1; i >=0 ; i--)
        {
            var ch = type.getTypeName().charAt(i);
            if (ch == '>') {
                stack.push(ch);
                tmp = new StringBuilder();
            }
            else if (ch == '<') {
                System.out.println(tmp.reverse());
                stack.pop();
                tmp = new StringBuilder();
            }
            else
                tmp.append(ch);
        }

        return null;
    }
}
