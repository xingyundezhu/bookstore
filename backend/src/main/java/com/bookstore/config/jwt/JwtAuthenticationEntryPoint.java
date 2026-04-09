package com.bookstore.config.jwt;

import com.alibaba.fastjson.JSON;
import com.bookstore.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * JWT认证入口点
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    /**
     * 处理认证失败
     * @param request HTTP请求
     * @param response HTTP响应
     * @param authException 认证异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        ApiResponse<Void> apiResponse = ApiResponse.error(401, "未授权访问：" + authException.getMessage());
        response.getWriter().write(JSON.toJSONString(apiResponse));
    }
}
