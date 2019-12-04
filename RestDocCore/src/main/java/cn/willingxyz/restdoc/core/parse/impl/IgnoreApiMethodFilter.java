package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.annotations.IgnoreApi;
import cn.willingxyz.restdoc.core.parse.IMethodFilter;
import cn.willingxyz.restdoc.core.parse.utils.RuntimeJavadocUtils;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import com.github.therapi.runtimejavadoc.OtherJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class IgnoreApiMethodFilter implements IMethodFilter {
    private static Logger _logger = LoggerFactory.getLogger(IgnoreApiMethodFilter.class);

    @Override
    public boolean isSupport(Method method) {
        if (method.isAnnotationPresent(IgnoreApi.class))
        {
            _logger.debug("ignore method: {}:{}", method.getDeclaringClass(), method.getName());
            return false;
        }
        MethodJavadoc methodJavadoc = RuntimeJavadoc.getJavadoc(method);
        if (methodJavadoc.getOther() != null)
        {
            OtherJavadoc ignoreApiJavadoc = RuntimeJavadocUtils.getTag(method, "ignoreApi");
            if (ignoreApiJavadoc != null)
                return false;
        }
        return true;
    }
}
