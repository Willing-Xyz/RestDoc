package cn.willingxyz.restdoc.core.utils;

import cn.willingxyz.restdoc.core.config.ExtOrder;
import cn.willingxyz.restdoc.core.config.IRestDocParseConfigAware;
import cn.willingxyz.restdoc.core.config.RestDocParseConfig;

import java.util.*;
import java.util.stream.Collectors;

public class ServiceLoaders {
    public static <T> List<T> loadServices(Class<T> clazz, RestDocParseConfig parseConfig)
    {
        List<T> processors = new ArrayList<>();
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
        List<T> finalProcessors = processors;
        serviceLoader.forEach(o -> {
            if (o instanceof IRestDocParseConfigAware)
            {
                IRestDocParseConfigAware configAware = (IRestDocParseConfigAware) o;
                configAware.setRestDocParseConfig(parseConfig);
            }
            finalProcessors.add(o);
        });
        processors = sortServices(processors);
        return processors;
    }

    private static  <T> List<T> sortServices(List<T> processors) {
        List<Map.Entry<Integer, T>> entries = new ArrayList<>();
        processors.forEach(o -> {
            ExtOrder extOrder = o.getClass().getAnnotation(ExtOrder.class);
            int order = 0;
            if (extOrder != null)
            {
                order = extOrder.value();
            }
            entries.add(new HashMap.SimpleEntry(order, o));
        });
        entries.sort(Comparator.comparing(Map.Entry::getKey));
        return entries.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }
}
