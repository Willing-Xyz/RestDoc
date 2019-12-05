package cn.willingxyz.restdoc.spring.examples.ext;

import cn.willingxyz.restdoc.swagger3.IOpenAPIFilter;
import com.google.auto.service.AutoService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@AutoService(IOpenAPIFilter.class)
public class TestOpenAPIFilter implements IOpenAPIFilter {
    @Override
    public OpenAPI handle(OpenAPI openAPI) {
        System.out.println("handle openapi");

        Server server = new Server();
        server.setDescription("add by extension");
        server.setUrl("http://localhost:8084?info=add_by_extension");
        openAPI.getServers().add(server);

        return openAPI;
    }
}
