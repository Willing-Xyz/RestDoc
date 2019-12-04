package cn.willingxyz.restdoc.core.config;

/**
 * 通过SPI机制加载的类如果实现了该接口，那么当实例化该类后，会自动调用set方法
 */
public interface IRestDocParseConfigAware {
    void setRestDocParseConfig(RestDocParseConfig config);
}
