package cn.willingxyz.restdoc.swagger3;

import cn.willingxyz.restdoc.core.config.RestDocConfig;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class RestDocConfigSwagger3Ext {
    private List<IOpenAPIFilter> _openAPIFilters;

    public List<IOpenAPIFilter> getOpenAPIFilters()
    {
        if (_openAPIFilters == null) _openAPIFilters = new ArrayList<>();
        return _openAPIFilters;
    }
}
