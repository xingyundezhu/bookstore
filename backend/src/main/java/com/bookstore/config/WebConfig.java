package com.bookstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web MVC配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * 文件上传路径
     */
    @Value("${app.upload.path}")
    private String uploadPath;
    
    /**
     * 配置静态资源处理器
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadPath);
    }
}
