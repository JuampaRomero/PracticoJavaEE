package com.ejercicio1.ws.rest.dto;

/**
 * Wrapper genérico para las respuestas REST
 */
public class ResponseWrapper<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String timestamp;
    
    public ResponseWrapper() {
        this.timestamp = java.time.Instant.now().toString();
    }
    
    public ResponseWrapper(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    // Factory methods para facilitar la creación
    public static <T> ResponseWrapper<T> success(T data, String message) {
        return new ResponseWrapper<>(true, message, data);
    }
    
    public static <T> ResponseWrapper<T> error(String message) {
        return new ResponseWrapper<>(false, message, null);
    }
    
    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}