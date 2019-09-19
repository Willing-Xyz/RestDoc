package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.annotations.IgnoreApi;
import cn.willingxyz.restdoc.core.parse.IControllerFilter;
import com.github.therapi.runtimejavadoc.ClassJavadoc;
import com.github.therapi.runtimejavadoc.OtherJavadoc;
import com.github.therapi.runtimejavadoc.RuntimeJavadoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 忽略带 @Ignore 和 ignore javadoc的类
 */
public class IgnoreApiControllerFilter implements IControllerFilter {

    private static Logger _logger = LoggerFactory.getLogger(IgnoreApiControllerFilter.class);

    @Override
    public boolean isSupport(Class clazz) {
        if (clazz.getAnnotation(IgnoreApi.class) != null)
        {
            _logger.debug("ignore controller:{}", clazz.getCanonicalName());
            return false;
        }
        ClassJavadoc classJavadoc = RuntimeJavadoc.getJavadoc(clazz);
        if (classJavadoc.getOther() != null)
        {
            OtherJavadoc ignoreApiJavadoc = classJavadoc.getOther().stream()
                    .filter(o -> o.getName().trim().equalsIgnoreCase("ignoreApi"))
                    .findFirst().orElse(null);
            if (ignoreApiJavadoc != null)
                return false;
        }
        return true;
    }
}
