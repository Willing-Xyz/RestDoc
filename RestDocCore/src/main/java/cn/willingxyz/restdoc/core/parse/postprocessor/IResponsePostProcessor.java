package cn.willingxyz.restdoc.core.parse.postprocessor;

import cn.willingxyz.restdoc.core.models.ResponseModel;
import cn.willingxyz.restdoc.core.models.ReturnModel;

import java.lang.reflect.Method;

public interface IResponsePostProcessor {
    ResponseModel postProcess(ResponseModel model, Method method);
}
