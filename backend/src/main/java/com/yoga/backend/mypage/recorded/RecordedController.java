package com.yoga.backend.mypage.recorded;

import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.mypage.recorded.dto.ChapterDto;
import com.yoga.backend.mypage.recorded.dto.DeleteDto;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/recorded-lecture")
public class RecordedController {

    private final RecordedService recordedService;
    private final JwtUtil jwtUtil;

    @Autowired
    public RecordedController(RecordedService recordedService, JwtUtil jwtUtil) {
        this.recordedService = recordedService;
        this.jwtUtil = jwtUtil;
    }


    /**
     * 사용자가 업로드한 강의 목록을 조회
     *
     * @param token jwt
     * @return 사용자가 업로드한 강의 목록과 관련된 정보가 포함된 ResponseEntity 객체
     */
    @GetMapping("/mypage/list")
    public ResponseEntity<Map<String, Object>> getMyLectures(
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        log.info("사용자 ID: {}", userId);
        List<LectureDto> lectureList = recordedService.getMyLectures(userId);

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
    @GetMapping("/mypage/likelist")
    public ResponseEntity<Map<String, Object>> getLikeLectures(
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        List<LectureDto> lectureList = recordedService.getLikeLectures(userId);

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
     * 새로운 강의를 생성하기를 요청
     *
     * @param token      JWT 토큰
     * @param lectureDto 강의 정보
     * @return sessionId
     */
    @PostMapping("/mypage/create")
    public ResponseEntity<Map<String, Object>> createLecture(
        @RequestHeader("Authorization") String token,
        @RequestBody LectureDto lectureDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            lectureDto.setUserId(userId);
            recordedService.saveLecture(lectureDto);
            response.put("message", "강의가 성공적으로 생성되었습니다.");
            response.put("data", true);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "강의 생성 실패: " + e.getMessage());
            response.put("data", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 업로드한 강의의 상세 정보 조회
     *
     * @param token       jwt
     * @param recorded_id 강의 id
     * @return 강의 상세 정보
     */
    @GetMapping("/mypage/detail/{recorded_id}")
    public ResponseEntity<Map<String, Object>> getLectureDetails(
        @RequestHeader("Authorization") String token, @PathVariable long recorded_id) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        LectureDto lectureDto = recordedService.getLectureDetails(recorded_id, userId);
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

    /**
     * 강의의 정보 수정
     *
     * @param token      jwt 토큰
     * @param lectureDto 수정된 강의 dto
     * @return 강의 수정 성공/실패 응답
     */
    @PutMapping("/mypage/update/{recordedId}")
    public ResponseEntity<Map<String, Object>> updateLecture(
        @RequestHeader("Authorization") String token,
        @PathVariable Long recordedId,
        @RequestBody LectureDto lectureDto) {
        Map<String, Object> response = new HashMap<>();
        lectureDto.setRecordedId(recordedId);

        for (ChapterDto ctr : lectureDto.getRecordedLectureChapters()) {
            System.out.println(ctr.getId() + "      =======        " + ctr.getRecordVideo());
        }

        log.info("강의 수정 ID: {}", recordedId);
        log.debug("LectureDto: {}", lectureDto);

        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            lectureDto.setUserId(userId);
            boolean updateResult = recordedService.updateLecture(lectureDto);

            if (updateResult) {
                log.info("강의 ID {} 수정 성공", recordedId);
                response.put("message", "강의 수정 성공");
                response.put("data", true);
                return ResponseEntity.ok(response);
            } else {
                log.warn("강의 ID {} 수정 실패", recordedId);
                response.put("message", "강의 수정 실패");
                response.put("data", false);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            log.error("강의 ID {} 수정 중 오류 발생: {}", recordedId, e.getMessage(), e);
            response.put("message", "강의 수정 중 오류 발생: " + e.getMessage());
            response.put("data", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 강의 삭제 요청
     *
     * @param token     jwt 토큰
     * @param deleteDto 삭제할 강의 ID 리스트를 포함한 DTO
     * @return 강의 삭제 성공/실패 응답
     */
    @PostMapping("/mypage/delete")
    public ResponseEntity<Map<String, Object>> deleteLectures(
        @RequestHeader("Authorization") String token,
        @RequestBody DeleteDto deleteDto) {

        for (Long l : deleteDto.getLectureIds()) {
            log.debug("삭제할 강의 ID: {}", l); // 삭제할 강의 ID를 로그에 기록
        }

        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            log.info("사용자 {}가 강의 삭제 시도: {}", userId, deleteDto);

            recordedService.deleteLectures(deleteDto, userId);

            response.put("message", "선택된 강의들이 성공적으로 삭제되었습니다.");
            response.put("data", true);
            log.info("사용자 {}의 강의 삭제 성공", userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("강의 삭제 중 오류 발생: {}", e.getMessage());
            response.put("message", e.getMessage());
            response.put("data", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("강의 삭제중 예상치 못한 에러 발생", e);
            response.put("message", "서버 오류가 발생했습니다: " + e.getMessage());
            response.put("data", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * 강의 좋아요, 좋아요 취소
     *
     * @param token      jwt
     * @param recordedId 강의 id
     * @return 좋아요/취소 성공/실패 응답
     */
    @PostMapping("/like/{recordedId}")
    public ResponseEntity<Map<String, Object>> like(@RequestHeader("Authorization") String token,
        @PathVariable Long recordedId) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        try {
            boolean isLiked = recordedService.toggleLike(recordedId, userId);
            log.info("사용자 {}가 강의 {} 좋아요/취소", userId, recordedId);
            response.put("message", isLiked ? "좋아요 성공" : "좋아요 취소");
            response.put("data", isLiked);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error processing like/unlike for lecture {} by user {}", recordedId, userId,
                e);
            response.put("message", "오류가 발생했습니다: " + e.getMessage());
            response.put("data", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/sort/{sort}")
    public ResponseEntity<Map<String, Object>> getAllLectures(
        @RequestHeader("Authorization") String token,
        @PathVariable String sort,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "21") int size
    ) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        log.info("사용자 ID: {}", userId);
        List<LectureDto> lectureList = recordedService.getAllLectures(userId, page, size, sort);

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

    @GetMapping("/search/{keyword}/sort/{sort}")
    public ResponseEntity<Map<String, Object>> searchLectures(
        @RequestHeader("Authorization") String token,
        @PathVariable String keyword,
        @PathVariable String sort,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "21") int size,
        @RequestParam(defaultValue = "false") boolean title,
        @RequestParam(defaultValue = "false") boolean content
    ) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        List<LectureDto> lectureList = recordedService.searchLectures(userId, keyword, sort, page, size, title, content);

        if (!lectureList.isEmpty()) {
            response.put("message", "강의 검색 성공");
            response.put("data", lectureList);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            response.put("message", "검색 결과 없음");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}
