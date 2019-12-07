package cn.willingxyz.restdoc.swagger3;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.var;

import java.util.HashSet;
import java.util.Set;

public class HideEmptyControllerOpenAPIFilter implements IOpenAPIFilter {
    @Override
    public OpenAPI handle(OpenAPI openApi) {
        if (openApi.getPaths() == null) return openApi;

        Set<String> tags = new HashSet<>();
        for (var path : openApi.getPaths().values()) {
            if (path.getGet() != null) tags.addAll(path.getGet().getTags());
            if (path.getPost() != null) tags.addAll(path.getPost().getTags());
            if (path.getPut() != null) tags.addAll(path.getPut().getTags());
            if (path.getDelete() != null) tags.addAll(path.getDelete().getTags());
            if (path.getOptions() != null) tags.addAll(path.getOptions().getTags());
            if (path.getHead() != null) tags.addAll(path.getHead().getTags());
            if (path.getPatch() != null) tags.addAll(path.getPatch().getTags());
        }

        openApi.getTags().removeIf(tag -> tags.stream().noneMatch(o -> o.equals(tag.getName())));
        return openApi;
    }
}
