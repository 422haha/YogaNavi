package com.yoga.backend.redirect;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedirectController {

    @PostMapping("/is-on")
    public ResponseEntity<Map<String, Object>> redirect() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "server on");
        response.put("data", new Object[]{});
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
