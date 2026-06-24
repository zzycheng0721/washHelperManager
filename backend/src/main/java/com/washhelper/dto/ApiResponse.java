package com.washhelper.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean ok;
    private String id;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success() {
        ApiResponse<T> response = new ApiResponse<>();
        response.setOk(true);
        return response;
    }

    public static <T> ApiResponse<T> success(String id) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setOk(true);
        response.setId(id);
        return response;
    }

    public static <T> ApiResponse<T> success(String id, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setOk(true);
        response.setId(id);
        response.setMessage(message);
        return response;
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setOk(true);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setOk(false);
        response.setMessage(message);
        return response;
    }
}
