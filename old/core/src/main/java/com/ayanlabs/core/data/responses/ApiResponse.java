package com.ayanlabs.core.data.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;    // For Serializable interface

    private String correlationId;               // end to end correlationId
    private boolean success;                    // Indicates if the operation was successful
    private String message;                     // A message describing the outcome (e.g., "User created", "Error occurred")
    private T data;                             // The actual data payload (e.g., a User object, a List of products)
    @Builder.Default
    private Instant timestamp = Instant.now();  // Timestamp executed
    private Object errors;


    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, String message, String correlationId) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .correlationId(correlationId)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Object errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, Object errors, String correlationId) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .correlationId(correlationId)
                .build();
    }
}
