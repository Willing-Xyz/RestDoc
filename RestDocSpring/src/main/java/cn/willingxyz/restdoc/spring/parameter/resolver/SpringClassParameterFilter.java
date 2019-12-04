package cn.willingxyz.restdoc.spring.parameter.resolver;

import cn.willingxyz.restdoc.core.parse.IMethodParameterFilter;
import lombok.var;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Parameter;
import java.security.Principal;
import java.util.*;

public class SpringClassParameterFilter implements IMethodParameterFilter {
    public  Class[] _classes = new Class[]{
            HttpServletRequest.class,
            ServletResponse.class,
            HttpServletResponse.class,
            OutputStream.class,
            Writer.class,
            WebRequest.class,
            ServletRequest.class,
            MultipartRequest.class,
            HttpSession.class,
//            PushBuilder.class, // todo 使用Class.forName加载类，因为如果使用方没有这个依赖会报错说找不到该类
            Principal.class,
            InputStream.class,
            Reader.class,
            HttpMethod.class,
            Locale.class,
            TimeZone.class,
            java.time.ZoneId.class
    };

    public SpringClassParameterFilter()
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
