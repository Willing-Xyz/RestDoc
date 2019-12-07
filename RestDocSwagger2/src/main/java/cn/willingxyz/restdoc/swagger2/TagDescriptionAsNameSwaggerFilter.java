package cn.willingxyz.restdoc.swagger2;

import cn.willingxyz.restdoc.core.parse.utils.TextUtils;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;

/**
 * 把tag的描述的第一行作为name
 */
public class TagDescriptionAsNameSwaggerFilter implements ISwaggerFilter {

    @Override
    public Swagger handle(Swagger swagger) {
        if (swagger.getTags() == null) return swagger;

        for (Tag tag : swagger.getTags())
        {
            String newTagName = TextUtils.getFirstLine(tag.getDescription());

            swagger.getPaths().values().forEach(path -> {
                handleOperation(tag, newTagName, path.getGet());
                handleOperation(tag, newTagName, path.getPost());
                handleOperation(tag, newTagName, path.getPut());
                handleOperation(tag, newTagName, path.getDelete());
                handleOperation(tag, newTagName, path.getHead());
                handleOperation(tag, newTagName, path.getOptions());
                handleOperation(tag, newTagName, path.getPatch());
            });

            tag.setName(newTagName);
        }
        return swagger;
    }

    private void handleOperation(Tag tag, String newTagName, Operation operation) {
        if (operation == null) return;
        if (operation.getTags().contains(tag.getName()))
        {
            operation.getTags().remove(tag.getName());
            operation.getTags().add(newTagName);
        }
    }
}
