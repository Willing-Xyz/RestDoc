package cn.willingxyz.restdoc.swagger2;

import io.swagger.models.Swagger;
import lombok.var;

import java.util.HashSet;
import java.util.Set;

public class HideEmptyControllerSwaggerFilter implements ISwaggerFilter{
    @Override
    public Swagger handle(Swagger swagger) {
        if (swagger.getPaths() == null) return swagger;
        Set<String> tags = new HashSet<>();
        for (var path : swagger.getPaths().values()) {
            if (path.getGet() != null) tags.addAll(path.getGet().getTags());
            if (path.getPost() != null) tags.addAll(path.getPost().getTags());
            if (path.getPut() != null) tags.addAll(path.getPut().getTags());
            if (path.getDelete() != null) tags.addAll(path.getDelete().getTags());
            if (path.getOptions() != null) tags.addAll(path.getOptions().getTags());
            if (path.getHead() != null) tags.addAll(path.getHead().getTags());
            if (path.getPatch() != null) tags.addAll(path.getPatch().getTags());
        }

        swagger.getTags().removeIf(tag -> tags.stream().noneMatch(o -> o.equals(tag.getName())));
        return swagger;
    }
}
