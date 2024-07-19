package com.yoga.backend.mypage.recorded;


import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.mypage.recorded.dto.ChapterDto;
import com.yoga.backend.mypage.recorded.dto.LectureCreationStatus;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import org.springframework.web.multipart.MultipartFile;

/*
* TODO: 종아요, 좋아요 취소 구현
* TODO: 좋아요한 강의 목록 조회( 아마 끝 )
* TODO: 강의 삭제, 수정 구현
* FIXME: 업로드한 강의 목록 조회 및 업로드한 강의 상세 보기 s3와 연결
*
* */
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
    public ResponseEntity<Map<String, Object>> getMyLectures(
        @RequestHeader("Authorization") String token) {
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
     * 사용자가 좋아요한 강의 목록을 조회
     *
     * @param token jwt
     * @return 사용자가 업로드한 강의 목록과 관련된 정보가 포함된 ResponseEntity 객체
     */
    @GetMapping("/likelist")
    public ResponseEntity<Map<String, Object>> getLikeLectures(
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        String email = jwtUtil.getEmailFromToken(token);
        List<LectureDto> lectureList = recordedService.getLikeLectures(email);

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
     * 새로운 강의를 생성
     *
     * @param token      JWT 토큰
     * @param lectureDto 강의 정보
     * @return 생성된 강의 정보
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createLecture(
        @RequestHeader("Authorization") String token,
        @RequestBody LectureDto lectureDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = jwtUtil.getEmailFromToken(token);
            lectureDto.setEmail(email);

            String sessionId = UUID.randomUUID().toString();
            CompletableFuture<LectureDto> futureLecture = recordedService.saveLectureAsync(
                lectureDto, sessionId);

            response.put("message", "강의 생성 요청이 접수되었습니다.");
            response.put("data", sessionId);
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            response.put("message", "강의 생성 요청 실패: " + e.getMessage());
            response.put("data", new String[]{e.getMessage()});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 강의 생성 상태 확인
     *
     * @param sessionId 강의 생성 세션 ID
     * @return 강의 생성 상태
     */
    @GetMapping("/status/{sessionId}")
    public ResponseEntity<Map<String, Object>> getLectureCreationStatus(
        @PathVariable String sessionId) {
        Map<String, Object> response = new HashMap<>();
        LectureCreationStatus status = recordedService.getLectureCreationStatus(sessionId);
        response.put("message", status.getMessage());
        response.put("data", new String[]{status.getStatus()});
        if (status.getLectureDto() != null) {
            response.put("lecture", status.getLectureDto());
        }
        return ResponseEntity.ok(response);
    }


    /**
     * 업로드한 강의의 상세 정보 조회
     *
     * @param token       jwt
     * @param recorded_id 강의 id
     * @return 강의 상세 정보
     */
    @GetMapping("/detail/{recorded_id}")
    public ResponseEntity<Map<String, Object>> getLectureDetails(
        @RequestHeader("Authorization") String token, @PathVariable long recorded_id) {
        Map<String, Object> response = new HashMap<>();
        String email = jwtUtil.getEmailFromToken(token);
        LectureDto lectureDto = recordedService.getLectureDetails(recorded_id, email);
        if (lectureDto != null) {
            response.put("message", "녹화강의 조회 성공");
            response.put("data", lectureDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "녹화강의 조회 실패");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }


}
