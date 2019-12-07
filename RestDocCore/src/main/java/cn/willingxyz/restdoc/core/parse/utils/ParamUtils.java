package cn.willingxyz.restdoc.core.parse.utils;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author cweijan
 * @since 2019/12/07 20:13
 */
public class ParamUtils {

    private static Map<Parameter, String> parameterNameMap = new HashMap<>();

    /**
     * 缓存参数名称
     * @param parameter 参数实体
     * @param parameterName 参数实际名称
     */
    public static void cacheParameterName(Parameter parameter, String parameterName) {
        if (parameter == null) return;
        parameterNameMap.put(parameter, parameterName);

    }

    /**
     * 从缓存的map寻找参数名
     */
    public static String discoverParameterName(Parameter parameter) {
        if (parameter == null) return null;

        String parameterName = parameterNameMap.get(parameter);
        if (parameterName != null) {
            parameterNameMap.remove(parameter);
        } else {
            parameterName = parameter.getName();
        }
        return parameterName;

    }

}
