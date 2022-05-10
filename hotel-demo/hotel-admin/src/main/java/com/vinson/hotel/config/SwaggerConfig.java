package com.vinson.hotel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;



/**
 * Swagger 当前配置类
 */
@Configuration
@EnableOpenApi
public class SwaggerConfig implements WebMvcConfigurer {
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.OAS_30)
                .enable(true)
                .apiInfo(apiInfo()) // API 选择启动器
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.vinson.hotel.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 创建一个ApiInfo对象
     * @return
     */
    private ApiInfo apiInfo() {
        return  new ApiInfoBuilder()
                // 文档标题
                .title("Hotel Demo API Doc")
                // 文档描述
                .description("This is api document of hotel.")
                // 作者信息：作者名称，作者地址，作者邮箱
                .contact(new Contact("Vinson", "https://github.com/ykxVinson", "ykx.vinson@gmail.com"))
                // 版本号
                .version("1.0")
                .build();
    }
}
