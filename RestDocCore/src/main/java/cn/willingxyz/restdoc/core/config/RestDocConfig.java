package cn.willingxyz.restdoc.core.config;

import cn.willingxyz.restdoc.core.parse.postprocessor.IPropertyPostProcessor;
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
     * 将JavaDoc的注释作为类的名称
     */
    private boolean _resolveJavaDocAsTypeName;

    /**
     * 如果controller里没有方法，则不显示该controller
     */
    private boolean _hideEmptyController = false;

    /**
     * 启用httpBasic认证
     */
    private HttpBasicAuth _httpBasicAuth;

    private List<Server> _servers = new ArrayList<>();

    @Data
    @Builder
    public static class Server {
        private String _url;
        private String _description;
    }

    @Data
    public static class HttpBasicAuth{
        private String _username;
        private String _password;

        public HttpBasicAuth(String username, String password) {
            this._username = username;
            this._password = password;
        }
    }

}
