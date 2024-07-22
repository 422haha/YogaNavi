package com.yoga.backend.Test;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test {

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> getLectureDetails() {
        Map<String, Object> response = new HashMap<>();

        response.put("message", "tttt");
        response.put("data", "");
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
}
