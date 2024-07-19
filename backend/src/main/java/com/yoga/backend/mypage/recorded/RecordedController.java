package com.yoga.backend.mypage.recorded;


import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import java.util.List;

@RestController
@RequestMapping("/mypage/recorded-lecture")
public class RecordedController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RecordedService recordedService;


    /**
     * 사용자가 업로드한 강의 목록을 조회
     *
     * @param token jwt
     * @return 사용자가 업로드한 강의 목록과 관련된 정보가 포함된 ResponseEntity 객체
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMyLectures(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        String email = jwtUtil.getEmailFromToken(token);
        List<LectureDto> lectureList = recordedService.getMyLectures(email);

        if (!lectureList.isEmpty()) {
            response.put("message", "녹화강의 조회 성공");
            response.put("data", lectureList);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "녹화강의 없음");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }


    /**
     * 사용자가 강의 업로드
     *
     * @param lectureDto 강의 dto
     * @return 사용자가 업로드한 강의 정보가 포함된 ResponseEntity 객체
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createLecture(@RequestHeader("Authorization") String token,@RequestBody LectureDto lectureDto) {
        Map<String, Object> response = new HashMap<>();
        lectureDto.setEmail(jwtUtil.getEmailFromToken(token));
        LectureDto savedLecture = recordedService.saveLecture(lectureDto);
        if (savedLecture != null) {
            response.put("message", "녹화강의 생성 성공");
            response.put("data", savedLecture);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }else{
            response.put("message", "녹화강의 생성 실패");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    /**
     * 사용자가 업로드한 강의의 상세 정보 조회
     * @param token jwt
     * @param recorded_id 강의 id
     * @return 강의 상세 정보
     */
    @GetMapping("/detail/{recorded_id}")
    public ResponseEntity<Map<String, Object>> getLectureDetails(@RequestHeader("Authorization") String token, @PathVariable Long recorded_id) {
        Map<String, Object> response = new HashMap<>();
        String email = jwtUtil.getEmailFromToken(token);
        LectureDto lectureDto = recordedService.getLectureDetails(recorded_id, email);
        if (lectureDto != null) {
            response.put("message", "녹화강의 조회 성공");
            response.put("data", lectureDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }else{
            response.put("message", "녹화강의 조회 실패");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

}
