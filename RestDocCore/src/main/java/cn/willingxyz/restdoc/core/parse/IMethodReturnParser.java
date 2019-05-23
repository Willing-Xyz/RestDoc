package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.models.ResponseModel;
import com.github.therapi.runtimejavadoc.Comment;

import java.lang.reflect.Method;

/**
 * 解析返回类型
 */
public interface IMethodReturnParser {

    ResponseModel parse(Method method, Comment returns, ResponseModel responseModel);

    /**
     * 是否创建一个新的Response，而不是在之前的Response上再加工
     */
    boolean isNew();
}
