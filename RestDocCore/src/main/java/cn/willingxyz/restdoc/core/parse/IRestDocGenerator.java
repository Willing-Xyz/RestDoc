package cn.willingxyz.restdoc.core.parse;

import cn.willingxyz.restdoc.core.models.RootModel;

public interface IRestDocGenerator {
    String generate(RootModel rootModel);
}
