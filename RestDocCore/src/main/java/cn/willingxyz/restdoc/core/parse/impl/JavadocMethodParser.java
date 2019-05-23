package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.parse.utils.FormatUtils;
import cn.willingxyz.restdoc.core.models.PathModel;
import com.github.therapi.runtimejavadoc.MethodJavadoc;
import cn.willingxyz.restdoc.core.parse.IMethodParser;

import java.lang.reflect.Method;

public class JavadocMethodParser implements IMethodParser {
    @Override
    public PathModel parse(Method method, MethodJavadoc methodJavadoc, PathModel pathModel) {
        if (methodJavadoc != null)
        {
            pathModel.setDescription(FormatUtils.format(methodJavadoc.getComment()));
        }
        return pathModel;
    }
}
