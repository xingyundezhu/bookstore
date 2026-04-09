package com.bookstore.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 统一API响应封装类
 */
@Data
public class ApiResponse<T> {
    
    /**
     * 状态码（200-成功，其他-失败）
     */
    private int code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应时间戳
     */
    private long timestamp;
    
    /**
     * 默认构造函数，自动设置时间戳
     */
    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 返回成功响应（默认消息）
     *
     * @param data 响应数据
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }
    
    /**
     * 返回成功响应（自定义消息）
     *
     * @param message 响应消息
     * @param data 响应数据
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    
    /**
     * 返回错误响应（自定义状态码）
     *
     * @param code 错误状态码
     * @param message 错误消息
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
    
    /**
     * 返回错误响应（默认状态码500）
     *
     * @param message 错误消息
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }

    /**
     * 返回错误响应（自定义状态码和数据）
     *
     * @param code 错误状态码
     * @param message 错误消息
     * @param data 错误数据
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
