package com.yoga.backend.mypage.recorded;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecordedController {

//    private final RecordedService recordedService;

    @GetMapping("/mypage/recorded-lecture/{user_id}")
    public ResponseEntity<Map<String, Object>> getRecordedLecture(@PathVariable("user_id") int user_id) {

        return null;
    }
}
