package cn.willingxyz.restdoc.core.utils;

import cn.willingxyz.restdoc.core.parse.ITypeInspector;
import cn.willingxyz.restdoc.core.parse.ITypeNameParser;
import lombok.var;

import java.lang.reflect.Type;

public class ClassNameUtils {

    // List<String> -> [String
    // List<List<String> -> [[String
    // com.willing.List<String> -> com.willing.[String
    public static String getComponentName(ITypeInspector typeInspector, ITypeNameParser typeNameParser, Type type) {
        // 统计嵌套集合的深度
        int collectionCount = 0;
        while (typeInspector.isCollection(type)) {
            collectionCount++;
            type = typeInspector.getCollectionComponentType(type);
        }
        var name = typeNameParser.parse(type);
        int $index = name.lastIndexOf('$');
        int dotIndex = name.lastIndexOf('.');
        if ($index > dotIndex) {
            return convertCollectionName(collectionCount, name, '$');
        } else {
            return convertCollectionName(collectionCount, name, ',');
        }
    }

    public static String convertCollectionName(int collectionCount, String name, char ch) {
        String prefix = "";
        int index = name.lastIndexOf(ch);
        if (index != -1) {
            prefix = name.substring(0, index + 1);
            name = name.substring(index + 1);
        }
        while (collectionCount-- > 0) {
            name = "[" + name;
        }
        return prefix + name;
    }
}
