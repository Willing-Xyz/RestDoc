package com.willing.restdoc.core.parse;

import com.willing.restdoc.core.models.RootModel;

public interface IDocGenerator {
    String generate(RootModel rootModel);
}
