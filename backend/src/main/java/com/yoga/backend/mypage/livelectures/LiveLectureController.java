package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.UsersRepository;
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
 * 실시간 강의 컨트롤러 클래스 실시간 강의 생성 및 조회에 대한 API 엔드포인트를 제공
 */
@RestController
@RequestMapping("/mypage/live-lecture-manage")
public class LiveLectureController {

    @Autowired
    private LiveLectureService liveLectureService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UsersRepository usersRepository;

    @PersistenceContext
    private EntityManager entityManager;

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
        // JWT 및 CSRF 토큰 검증 로직 추가 필요
        // 예: String accessToken = request.getHeader("accessToken");
//        1. 토큰을 받아온다.
        String token = request.getHeader("Authorization");
        System.out.println(token);
//        2. 토큰값을 JwtUtil을 이용해서 Id으로 바꾼다.
        Integer userid = jwtUtil.getUserIdFromToken(token);
//        3. userId를 liveLectureDto에 설정
        liveLectureCreateDto.setUserId(userid);
//        liveLectureDto.setUserId(user.getId()); // DTO에 사용자 ID 설정

//        LiveLectures createdLecture = liveLectureService.createLiveLecture(liveLectureCreateDto);
        LiveLectureCreateResponseDto responseDto = liveLectureService.createLiveLecture(
            liveLectureCreateDto);
//        return createdLecture; // 화상 강의 생성

        Map<String, Object> response = new HashMap<>();
        response.put("message", responseDto.getMessage());
        response.put("data", responseDto.getData());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);

//        return liveLectureService.createLiveLecture(liveLectureDto);// 화상 강의 생성
    }

    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회하는 API 엔드포인트
     *
     * @param request HttpServletRequest 객체
     * @return 나의 실시간 강의 리스트
     */
    // DTO 나누고 난 이후 전체 화상강의 조회 (아래 코드로 대체됨)
//    @GetMapping
//    public ResponseEntity<List<LiveLectureResponseDto>> getLiveLecturesByUser(HttpServletRequest request) {
//        String token = request.getHeader("Authorization");
//        Integer userId = jwtUtil.getUserIdFromToken(token);
//        List<LiveLectures> lectures = liveLectureService.getLiveLecturesByUserId(userId);
//
//        List<LiveLectureResponseDto> response = lectures.stream().map(lecture -> {
//            LiveLectureResponseDto dto = new LiveLectureResponseDto();
//            dto.setLiveId(lecture.getLiveId());
//            dto.setRegDate(lecture.getRegDate());
//            dto.setUserId(lecture.getUser().getId());
//            dto.setNickname(lecture.getUser().getNickname());
//            dto.setProfileImageUrl(lecture.getUser().getProfile_image_url());
//            dto.setLiveTitle(lecture.getLiveTitle());
//            dto.setLiveContent(lecture.getLiveContent());
//            dto.setAvailableDay(lecture.getAvailableDay());
//            dto.setStartDate(lecture.getStartDate());
//            dto.setStartTime(lecture.getStartTime());
//            dto.setEndDate(lecture.getEndDate());
//            dto.setEndTime(lecture.getEndTime());
//            dto.setMaxLiveNum(lecture.getMaxLiveNum());
//            return dto;
//        }).collect(Collectors.toList());
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
// 아래 코드로 대체됨

//    전체 화상강의 조회(일부 데이터는 모바일팀 요청에 의해 제외함)
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

    // 신원 확인 후 수정
//    @PutMapping("/update/{live_id}")
//    public ResponseEntity<Object> updateLiveLecture(
//        @PathVariable("live_id") Integer liveId,
//        @RequestBody @Valid LiveLectureCreateDto liveLectureCreateDto,
//        @RequestHeader("Authorization") String token) {
//        try {
//            int userId = jwtUtil.getUserIdFromToken(token);
//            liveLectureCreateDto.setLiveId(liveId);
//
//            // 신원 확인 및 권한 확인
//            if (!liveLectureService.isLectureOwner(liveId, userId)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다");
//            }
//
//            LiveLectures updatedLecture = liveLectureService.updateLiveLecture(
//                liveLectureCreateDto);
//            return ResponseEntity.ok(updatedLecture);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("강의 수정 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }

    //신원 확인 후 수정 2
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

    //단일 조회 신원 확인 2
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

// 삭제

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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 신원 확인 및 권한 확인
            if (!liveLectureService.isLectureOwner(liveId, userId)) {
                response.put("message", "권한이 없습니다");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            liveLectureService.deleteLiveLectureById(liveId);
            response.put("message", "강의 삭제 성공");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "강의 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    //삭제 수정버전
//    @DeleteMapping("/delete/{live_id}")
//    public ResponseEntity<ResponseMessage> deleteLiveLecture(
//        @PathVariable("live_id") Integer liveId,
//        @RequestHeader("Authorization") String token) {
//        try {
//            int userId = jwtUtil.getUserIdFromToken(token);
//            LiveLectures lecture = liveLectureService.getLiveLectureById(liveId);
//
//            if (lecture == null) {
//                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//            }
//
//            // 신원 확인 및 권한 확인
//            if (!liveLectureService.isLectureOwner(liveId, userId)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(new ResponseMessage("권한이 없습니다", null));
//            }
//
//            // 먼저 my_live_lecture 테이블에서 관련 항목 삭제
//            liveLectureService.deleteMyLiveLectureByLiveId(liveId);
//
//            // 그런 다음 live_lectures 테이블에서 항목 삭제
//            liveLectureService.deleteLiveLectureById(liveId);
//
//            return ResponseEntity.ok(new ResponseMessage("success", null));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ResponseMessage("강의 삭제 중 오류가 발생했습니다: " + e.getMessage(), null));
//        }
//    }


}