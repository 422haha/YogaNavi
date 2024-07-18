package com.yoga.backend.mypage.recorded;

import com.yoga.backend.mypage.recorded.dto.LectureDto;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecordedController {

    @GetMapping("/mypage/recorded-lecture/{user_id}")
    public ResponseEntity<Map<String, Object>> getRecordedLecture(
        @PathVariable("user_id") int user_id) {

        return null;
    }

    @PostMapping("/mypage/recorded-lecture/create")
    public ResponseEntity<Map<String, Object>> createRecordedLecture(
        @RequestBody LectureDto lectureDto) {
        return null;
    }

}
