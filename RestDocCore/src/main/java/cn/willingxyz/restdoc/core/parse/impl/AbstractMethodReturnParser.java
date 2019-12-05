package cn.willingxyz.restdoc.core.parse.impl;

import cn.willingxyz.restdoc.core.models.TypeContext;
import cn.willingxyz.restdoc.core.config.AbstractRestDocParseConfigAware;
import cn.willingxyz.restdoc.core.parse.utils.FormatUtils;
import cn.willingxyz.restdoc.core.models.ResponseModel;
import com.github.therapi.runtimejavadoc.Comment;
import cn.willingxyz.restdoc.core.parse.IMethodReturnParser;
import lombok.var;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public abstract class AbstractMethodReturnParser extends AbstractRestDocParseConfigAware implements IMethodReturnParser {

    @Override
    public ResponseModel parse(Method method, Comment returns, ResponseModel responseModel) {
        responseModel.setStatusCode(parseStatusCode(method));

        var returnModel = responseModel.getReturnModel();

        returnModel.setDescription(FormatUtils.format(returns));

        Type actualType = method.getGenericReturnType();
        if (actualType == Optional.class)
        {
            if (method.getGenericReturnType() instanceof ParameterizedType)
            {
                actualType = ((ParameterizedType)method.getGenericReturnType()).getActualTypeArguments()[0];
            }
            else
            {
                throw new RuntimeException("why?");
            }
        }
        returnModel.setReturnType(actualType);

        boolean isArray = _config.getTypeInspector().isCollection(actualType);
        returnModel.setArray(isArray);

        if (!isArray) {
            returnModel.setChildren(_config.getTypeParser().parse(new TypeContext(actualType, null, method)));
        }
        else
        {
            returnModel.setChildren(_config.getTypeParser().parse(
                    new TypeContext(_config.getTypeInspector().getCollectionComponentType(actualType), null, method)
            ));
        }
        parseInternal(method, actualType, responseModel);
        return responseModel;
    }

    protected ResponseModel parseInternal(Method method, Type actualType, ResponseModel responseModel)
    {
        return responseModel;
    }

    protected abstract int parseStatusCode(Method method);

    @Override
    public boolean isNew() {
        return false;
    }
}
