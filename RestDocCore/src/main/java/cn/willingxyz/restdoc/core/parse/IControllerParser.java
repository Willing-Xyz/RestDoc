package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.models.ControllerModel;
import com.github.therapi.runtimejavadoc.ClassJavadoc;

/**
 * 解析controller
 */
public interface IControllerParser {
    void parse(Class clazz, ClassJavadoc classJavadoc, ControllerModel controllerModel);
}
