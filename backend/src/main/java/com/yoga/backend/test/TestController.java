package com.yoga.backend.test;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    Map<String, Object> response = new HashMap<>();

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        response.put("message", "test success    ");
        response.put("data", new Object[]{});
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}