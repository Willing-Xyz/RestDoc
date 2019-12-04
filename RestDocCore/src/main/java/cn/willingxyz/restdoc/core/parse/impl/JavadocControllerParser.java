package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.parse.utils.FormatUtils;
import cn.willingxyz.restdoc.core.models.ControllerModel;
import com.github.therapi.runtimejavadoc.ClassJavadoc;
import cn.willingxyz.restdoc.core.parse.IControllerParser;
import com.google.auto.service.AutoService;

@AutoService(IControllerParser.class)
public class JavadocControllerParser implements IControllerParser {

    @Override
    public void parse(Class clazz, ClassJavadoc classDoc, ControllerModel controllerModel) {
        controllerModel.setControllerClass(clazz);
        if (classDoc != null && classDoc.isPresent()) {
            controllerModel.setDescription(FormatUtils.format(classDoc.getComment()));
        }
    }
}
