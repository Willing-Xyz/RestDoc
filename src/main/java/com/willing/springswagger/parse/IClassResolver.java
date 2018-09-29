package com.willing.springswagger.parse;

import java.util.List;

/**
 * 获取可处理的类
 */
public interface IClassResolver {
    List<Class> getClasses();
}
