package com.bookstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j API文档配置类
 */
@Configuration
public class Knife4jConfig {
    
    /**
     * 配置OpenAPI基本信息
     *
     * @return OpenAPI对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("网上图书系统API")
                .version("1.0.0")
                .description("网上图书系统后端API接口文档")
                .contact(new Contact()
                    .name("Bookstore Team")
                    .email("support@bookstore.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
    
    /**
     * 配置普通用户端API分组
     * @return GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
            .group("普通用户端")
            .packagesToScan("com.bookstore.controller")
            .pathsToExclude(
                "/admin/**",
                "/users/**",
                "/books/admin/**",
                "/books/*/sales",
                "/books/sales/statistics",
                "/categories/admin/**",
                "/orders/admin/**",
                "/orders/statistics",
                "/orders/*/deliver",
                "/orders/*/status",
                "/reviews/admin/**"
            )
            .build();
    }
    
    /**
     * 配置管理员端API分组
     * @return GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
            .group("管理员端")
            .packagesToScan("com.bookstore.controller")
            .build();
    }
}
