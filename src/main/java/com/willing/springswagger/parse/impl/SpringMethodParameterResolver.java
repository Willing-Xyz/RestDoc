package com.willing.springswagger.parse.impl;

import com.willing.springswagger.parse.IMethodParameterResolver;
import lombok.var;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.PushBuilder;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Parameter;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

public class SpringMethodParameterResolver implements IMethodParameterResolver {
    public static Class[] _classes = new Class[]{
            HttpServletRequest.class,
            ServletResponse.class,
            HttpServletResponse.class,
            OutputStream.class,
            Writer.class,
            WebRequest.class,
            ServletRequest.class,
            MultipartRequest.class,
            HttpSession.class,
            PushBuilder.class,
            Principal.class,
            InputStream.class,
            Reader.class,
            HttpMethod.class,
            Locale.class,
            TimeZone.class,
            java.time.ZoneId.class
    };

    public SpringMethodParameterResolver()
    {
    }
    @Override
    public boolean isSupport(Parameter parameter) {
        for (var clazz : _classes)
        {
            if (clazz.isAssignableFrom(parameter.getType()))
                return false;
        }
        return true;
    }
}
