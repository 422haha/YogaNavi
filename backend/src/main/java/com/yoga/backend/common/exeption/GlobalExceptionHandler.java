package com.yoga.backend.common.exeption;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Map<String, Object>> handleMultipartException(MultipartException e) {
        Map<String, Object> response = new HashMap<>();

        response.put("message", "Failed to process multipart request");
        response.put("data", new Object[]{});
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
