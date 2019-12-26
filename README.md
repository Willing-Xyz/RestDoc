## 介绍

该项目用于在运行时使用javadoc生成swagger文档，并使用swagger-ui进行显示。


在线示例： http://www.willingxyz.cn:8084/swagger-ui/index.html 

![示例](./images/example_summary.png?)

## 使用

第一步, 为SpringBoot项目中配置依赖, 配置RestDocConfig

Maven项目增加依赖：

```
<dependency>
     <groupId>cn.willingxyz.restdoc</groupId>
     <artifactId>RestDocSpringSwagger3</artifactId>
     <version>0.2.0.0</version>
</dependency>
<dependency>
    <groupId>com.github.therapi</groupId>
    <artifactId>therapi-runtime-javadoc-scribe</artifactId>
    <version>0.9.0</version>
    <scope>provided</scope>
</dependency>
```

新建配置文件，如下：

```java 
@Configuration
@EnableSwagger3
public class SwaggerConfig {
    @Bean
    RestDocConfig _swaggerConfig()
    {
            return RestDocConfig.builder()
                    .apiTitle("rest doc title")
                    .apiDescription("rest doc desc")
                    .apiVersion("api version")
                    .packages(Arrays.asList("cn.willingxyz.restdoc.spring.examples"))
                    .build();
    }
}
```

主要需要修改包名为自己的应用包名。

[其他配置参考](#配置参考)

第二步，启用Annotation Processors 

- **IntelliJ IDEA**: File > Settings > Preferences > Build, Execution, Deployment > Compiler > Annotation Processors > 勾选"Enable annotation processing".
![编译设置](./images/compile-setting.png?)

启动应用后，打开 http://host/swagger-ui/index.html 浏览

具体可参考 [RestDocSpringExamples](https://github.com/Willing-Xyz/RestDoc/tree/master/RestDocSpringExamples)。

## BeanValidation支持

如果需要BeanValidation的支持，需要增加以下依赖：
```
<dependency>
    <groupId>cn.willingxyz.restdoc</groupId>
    <artifactId>RestDocBeanValidation</artifactId>
    <version>0.2.0.0</version>
</dependency>
```

支持BeanValidation的注解：

- NotNull
- AssertFalse
- AssertTrue
- DecimalMax
- DecimalMin
- Email （不支持正则表达式）
- Max
- Min
- NegativeOrZero
- Negative
- NotBlank
- NotEmpty
- NotNull
- Null
- PositiveOrZero
- Positive

## Jackson支持

如果需要Jackson的支持，增加以下依赖：
```
<dependency>
    <groupId>cn.willingxyz.restdoc</groupId>
    <artifactId>RestDocJackson</artifactId>
    <version>0.2.0.0</version>
</dependency>
```

支持的Jackson注解：

- JsonGetter
- JsonSetter
- JsonIgnore
- JsonIgnoreType
- JsonProperty (不支持对枚举的操作)


## 配置参考

```java 
@Bean
RestDocConfig _swaggerConfig()
{
        return RestDocConfig.builder()
                //配置文档标题
                .apiTitle("rest doc title")
                //配置文档描述
                .apiDescription("rest doc desc")
                //配置文档版本
                .apiVersion("api version")
                //是否将类的javadoc解析为swagger显示名称
                .resolveJavaDocAsTypeName(true)
                //是否隐藏没有方法的Controller
                .hideEmptyController(true)
                //配置扫描的包
                .packages(Arrays.asList("cn.willingxyz.restdoc.spring.examples"))
                //启用httpBasic认证
                .httpBasicAuth(new RestDocConfig.HttpBasicAuth("restdoc","restdoc"))
                //配置接口地址
                .servers(Arrays.asList(RestDocConfig.Server.builder().description("url desc").url("localhost:8080").build()))
                //配置field前缀
                .fieldPrefix("_")
                .build();
}
```

其中 fieldPrefix表示字段前缀。
因为在获取javadoc时，会从field、get方法、set方法上获取，因此如果field有前缀，需要通过fieldPrefix设置，否则将无法获取到javadoc。
如：

```java
public class Response {
    /**
    * name javadoc
    */
    private String _name;
    public String getName() {
           return _name;
    }
    public void setName(String name) {
        _name = name;
    }
}
```
Name属性对应的字段是_name，因此 fieldPrefix应该设置为 `.fieldPrefix("_")`

## 使用swagger2

如果需要使用Swagger2(兼容其他文档工具)，替换依赖为：
```
<dependency>
     <groupId>cn.willingxyz.restdoc</groupId>
     <artifactId>RestDocSpringSwagger2</artifactId>
     <version>0.2.0.0</version>
</dependency>
<dependency>
    <groupId>com.github.therapi</groupId>
    <artifactId>therapi-runtime-javadoc-scribe</artifactId>
    <version>0.9.0</version>
    <scope>provided</scope>
</dependency>
```
将EnableSwagger3改为EnableSwagger2
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    //...
}
```


## docker

docker通过以下命令运行：

`docker run --rm -d -p 8084:8084 willingxyz/restdoc:0.2.0.0`

swagger3规范打开 http://localhost:8084/swagger-ui/index.html 查看。
swagger2规范打开 http://localhost:8084/swagger2-ui/index.html 查看。

## 原理

通过注解处理器在编译时生成javadoc的json文件, 将这些文件转换为Swagger-ui的OpenApi数据格式。

更多信息请参考 Wiki 
