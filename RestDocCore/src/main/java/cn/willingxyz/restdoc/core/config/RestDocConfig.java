package cn.willingxyz.restdoc.core.config;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class RestDocConfig {
    private List<String> _packages = new ArrayList<>();
    private String _fieldPrefix;

    private String _apiTitle;
    private String _apiDescription;
    private String _apiVersion;
    /**
     * 把Tag的描述显示为tag Name。默认tag name为类的名字，如果该值为true，则使用javadoc的第一行作为name
     */
    private boolean _tagDescriptionAsName = false;

    /**
     * 如果controller里没有方法，则不显示该controller
     */
    private boolean _hideEmptyController = false;
}
