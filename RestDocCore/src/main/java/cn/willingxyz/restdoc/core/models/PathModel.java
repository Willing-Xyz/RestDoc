package cn.willingxyz.restdoc.core.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * controller的方法，每个url对应一个
 */
@Data
public class PathModel {
    private List<MappingModel> _mappings = new ArrayList<>();
    /**
     * 详细描述
     */
    private String _description;
    /**
     * 参数列表
     */
    private List<ParameterModel> _parameters = new ArrayList<>();
    /**
     * 响应
     */
    private List<ResponseModel> _response = new ArrayList<>();
    private Boolean _deprecated;
}
