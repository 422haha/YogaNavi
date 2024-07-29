package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureCreateDto;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureCreateResponseDto;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureResponseDto;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureCreateDto;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureCreateResponseDto;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 실시간 강의 컨트롤러 클래스.
 * 실시간 강의 생성 및 조회에 대한 API 엔드포인트를 제공.
 */
@RestController
@RequestMapping("/mypage/live-lecture-manage")
public class LiveLectureController {

    @Autowired
    private LiveLectureService liveLectureService; // 실시간 강의 서비스
    @Autowired
    private JwtUtil jwtUtil; // JWT 유틸리티 클래스
    @Autowired
    private UsersRepository usersRepository; // 사용자 저장소

    /**
     * 실시간 강의를 생성하는 API 엔드포인트
     *
     * @param liveLectureCreateDto 실시간 강의 DTO
     * @param request              HttpServletRequest 객체
     * @return 생성된 실시간 강의 엔티티
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createLiveLecture(
        @RequestBody @Valid LiveLectureCreateDto liveLectureCreateDto,
        HttpServletRequest request) {
//        1. 토큰을 받아온다.
        String token = request.getHeader("Authorization");
//        2. 토큰값을 JwtUtil을 이용해서 Id으로 바꾼다.
        Integer userid = jwtUtil.getUserIdFromToken(token);
//        3. userId를 liveLectureDto에 설정
        liveLectureCreateDto.setUserId(userid);
        // 4. 사용자 역할을 확인한다.
        String userRole = usersRepository.findById(liveLectureCreateDto.getUserId()).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")).getRole();

        if (!"TEACHER".equals(userRole)) {
            // 5. 역할이 TEACHER가 아닌 경우
            Map<String, Object> response = new HashMap<>();
            response.put("message", "권한이 없습니다");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        LiveLectureCreateResponseDto responseDto = liveLectureService.createLiveLecture(
            liveLectureCreateDto);

        Map<String, Object> response = new HashMap<>();
        response.put("message", responseDto.getMessage());
        response.put("data", responseDto.getData());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회하는 API 엔드포인트
     * (일부 데이터는 모바일팀 요청에 의해 제외함)
     *
     * @param request HttpServletRequest 객체
     * @return 나의 실시간 강의 리스트
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLiveLecturesByUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, Object> response = new HashMap<>();
        Integer userId = jwtUtil.getUserIdFromToken(token);

        List<LiveLectures> lectureList = liveLectureService.getLiveLecturesByUserId(userId);

        List<LiveLectureResponseDto> responseList = lectureList.stream().map(lecture -> {
            LiveLectureResponseDto dto = new LiveLectureResponseDto();
            dto.setLiveId(lecture.getLiveId());
            dto.setRegDate(lecture.getRegDate());
            dto.setUserId(lecture.getUser().getId());
            dto.setNickname(lecture.getUser().getNickname());
            dto.setProfileImageUrl(lecture.getUser().getProfile_image_url());
            dto.setProfileImageUrlSmall(lecture.getUser().getProfile_image_url_small());
            dto.setLiveTitle(lecture.getLiveTitle());
            dto.setLiveContent(lecture.getLiveContent());
            dto.setAvailableDay(lecture.getAvailableDay());
            dto.setStartDate(lecture.getStartDate());
            dto.setStartTime(lecture.getStartTime());
            dto.setEndDate(lecture.getEndDate());
            dto.setEndTime(lecture.getEndTime());
            dto.setMaxLiveNum(lecture.getMaxLiveNum());
            return dto;
        }).collect(Collectors.toList());

        response.put("message", "화상 강의 조회 성공");
        response.put("data", responseList);

        return ResponseEntity.ok(response);
    }

    /**
     * 실시간 강의를 수정하는 API 엔드포인트.
     *
     * @param liveId               수정할 강의 ID
     * @param liveLectureCreateDto 수정할 실시간 강의 DTO
     * @param token                인증 토큰
     * @return 수정 결과 응답
     */
    @PutMapping("/update/{live_id}")
    public ResponseEntity<Map<String, Object>> updateLiveLecture(
        @PathVariable("live_id") Integer liveId,
        @RequestBody @Valid LiveLectureCreateDto liveLectureCreateDto,
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            liveLectureCreateDto.setLiveId(liveId);

            if (!liveLectureService.isLectureOwner(liveId, userId)) {
                response.put("message", "권한이 없습니다");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            liveLectureService.updateLiveLecture(liveLectureCreateDto);
            response.put("message", "화상강의 수정 성공");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "강의 수정 중 오류가 발생했습니다: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 강의를 조회하는 API 엔드포인트.
     *
     * @param liveId 조회할 강의 ID
     * @param token  인증 토큰
     * @return 조회 결과 응답
     */
    @GetMapping("/{live_id}")
    public ResponseEntity<Map<String, Object>> getLiveLectureById(
        @PathVariable("live_id") Integer liveId,
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            LiveLectures lecture = liveLectureService.getLiveLectureById(liveId);

            if (lecture == null) {
                response.put("message", "강의 없음");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 신원 확인 및 권한 확인
            if (!liveLectureService.isLectureOwner(liveId, userId)) {
                response.put("message", "권한이 없습니다");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Map<String, Object> lectureData = new HashMap<>();
            lectureData.put("liveId", lecture.getLiveId());
            lectureData.put("regDate", lecture.getRegDate());
            lectureData.put("userId", lecture.getUser().getId());
            lectureData.put("nickname", lecture.getUser().getNickname());
            lectureData.put("profileImageUrl", lecture.getUser().getProfile_image_url());
            lectureData.put("liveTitle", lecture.getLiveTitle());
            lectureData.put("liveContent", lecture.getLiveContent());
            lectureData.put("availableDay", lecture.getAvailableDay());
            lectureData.put("startDate", lecture.getStartDate());
            lectureData.put("startTime", lecture.getStartTime());
            lectureData.put("endDate", lecture.getEndDate());
            lectureData.put("endTime", lecture.getEndTime());
            lectureData.put("maxLiveNum", lecture.getMaxLiveNum());

            response.put("message", "조회에 성공했습니다");
            response.put("data", lectureData);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "강의 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 실시간 강의를 삭제하는 API 엔드포인트.
     *
     * @param liveId 삭제할 강의 ID
     * @param token  인증 토큰
     * @return 삭제 결과 응답
     */
    @DeleteMapping("/delete/{live_id}")
    public ResponseEntity<Map<String, Object>> deleteLiveLecture(
        @PathVariable("live_id") Integer liveId,
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            LiveLectures lecture = liveLectureService.getLiveLectureById(liveId);

            if (lecture == null) {
                response.put("message", "강의 없음");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 신원 확인 및 권한 확인
            if (!liveLectureService.isLectureOwner(liveId, userId)) {
                response.put("message", "권한이 없습니다");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            liveLectureService.deleteLiveLectureById(liveId);
            response.put("message", "강의 삭제 성공");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "강의 삭제 중 오류가 발생했습니다: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}