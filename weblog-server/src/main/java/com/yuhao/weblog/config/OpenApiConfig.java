package com.yuhao.weblog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API 文档（springdoc-openapi，访问 /swagger-ui.html）
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI weblogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Weblog 博客接口文档")
                        .description("前后端分离博客 Weblog 1.0 接口")
                        .version("1.0.0"));
    }
}
