package com.willing.restdoc.core.parse;

import com.willing.restdoc.core.models.RootModel;

public interface IRestDocGenerator {
    String generate(RootModel rootModel);
}
