package com.yoga.backend.livelectures.Controller;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.livelectures.service.LiveLectureService;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.livelectures.dto.LiveLectureCreateDto;
import com.yoga.backend.livelectures.dto.LiveLectureCreateResponseDto;
import com.yoga.backend.livelectures.dto.LiveLectureResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
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
 * 실시간 강의 컨트롤러.
 */
@Slf4j
@RestController
@RequestMapping("/mypage/live-lecture-manage")
public class LiveLectureController {

    private final LiveLectureService liveLectureService;
    private final JwtUtil jwtUtil;
    private final UsersRepository usersRepository;

    public LiveLectureController(LiveLectureService liveLectureService, JwtUtil jwtUtil,
        UsersRepository usersRepository) {
        this.liveLectureService = liveLectureService;
        this.jwtUtil = jwtUtil;
        this.usersRepository = usersRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createLiveLecture(
        @RequestBody @Valid LiveLectureCreateDto liveLectureCreateDto,
        HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        int userId = jwtUtil.getUserIdFromToken(token);
        liveLectureCreateDto.setUserId(userId);

        log.info("라이브 강의 생성 요청: 사용자 ID {}", userId);

        String userRole = usersRepository.findById(liveLectureCreateDto.getUserId())
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")).getRole();

        if (!"TEACHER".equals(userRole)) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "권한이 없습니다");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        LiveLectureCreateResponseDto responseDto = liveLectureService.createLiveLecture(
            liveLectureCreateDto);
        log.info("라이브 강의 생성 완료: 사용자 ID {}", userId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", responseDto.getMessage());
        response.put("data", responseDto.getData());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getLiveLecturesByUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        log.info("라이브 강의 목록 조회 요청: 사용자 ID {}", userId);

        List<LiveLectures> lectureList = liveLectureService.getLiveLecturesByUserId(userId);

        List<LiveLectureResponseDto> responseList = lectureList.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());

        response.put("message", "화상 강의 조회 성공");
        response.put("data", responseList);

        log.info("라이브 강의 목록 조회 완료: 사용자 ID {}, 조회된 강의 수 {}", userId, responseList.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{live_id}")
    public ResponseEntity<Map<String, Object>> updateLiveLecture(
        @PathVariable("live_id") Long liveId,
        @RequestBody @Valid LiveLectureCreateDto liveLectureCreateDto,
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        log.info("라이브 강의 수정 요청: 강의 ID {}, 사용자 ID {}", liveId, userId);

        try {
            liveLectureCreateDto.setLiveId(liveId);

            if (!liveLectureService.isLectureOwner(liveId, userId)) {
                log.warn("수정 권한이 없습니다. 강의 ID {}, 사용자 ID {}", liveId, userId);
                response.put("message", "수정 권한이 없습니다");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            liveLectureService.updateLiveLecture(liveLectureCreateDto);
            log.info("라이브 강의 수정 완료: 강의 ID {}", liveId);

            response.put("message", "화상강의 수정 성공");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("라이브 강의 수정 중 오류 발생: 강의 ID {}, 오류 메시지 {}", liveId, e.getMessage());
            response.put("message", "강의 수정 중 오류가 발생했습니다: ");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{live_id}")
    public ResponseEntity<Map<String, Object>> getLiveLectureById(
        @PathVariable("live_id") Long liveId,
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        log.info("단일 라이브 강의 조회 요청: 강의 ID {}, 사용자 ID {}", liveId, userId);

        try {
            LiveLectures lecture = liveLectureService.getLiveLectureById(liveId);

            if (lecture == null) {
                response.put("message", "강의 없음");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!liveLectureService.isLectureOwner(liveId, userId)) {
                response.put("message", "권한이 없습니다");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            LiveLectureResponseDto dto = convertToDto(lecture);

            log.info("단일 라이브 강의 조회 완료: 강의 ID {}", liveId);
            response.put("message", "조회에 성공했습니다");
            response.put("data", dto);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("단일 라이브 강의 조회 중 오류 발생: 강의 ID {}, 오류 메시지 {}", liveId, e.getMessage());
            response.put("message", "강의 조회 중 오류가 발생했습니다: ");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{live_id}")
    public ResponseEntity<Map<String, Object>> deleteLiveLecture(
        @PathVariable("live_id") Long liveId,
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        int userId = jwtUtil.getUserIdFromToken(token);
        log.info("라이브 강의 삭제 요청: 강의 ID {}, 사용자 ID {}", liveId, userId);

        try {
            LiveLectures lecture = liveLectureService.getLiveLectureById(liveId);

            if (lecture == null) {
                response.put("message", "강의 없음");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!liveLectureService.isLectureOwner(liveId, userId)) {
                response.put("message", "권한이 없습니다");
                response.put("data", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            liveLectureService.deleteLiveLectureById(liveId);
            log.info("라이브 강의 삭제 완료: 강의 ID {}", liveId);

            response.put("message", "강의 삭제 성공");
            response.put("data", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("라이브 강의 삭제 중 오류 발생: 강의 ID {}, 오류 메시지 {}", liveId, e.getMessage());
            response.put("message", "강의 삭제 중 오류가 발생했습니다: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private LiveLectureResponseDto convertToDto(LiveLectures lecture) {
        LiveLectureResponseDto dto = new LiveLectureResponseDto();
        dto.setLiveId(lecture.getLiveId());
        dto.setRegDate(lecture.getRegDate().toEpochMilli());
        dto.setUserId(lecture.getUser().getId());
        dto.setNickname(lecture.getUser().getNickname());
        dto.setProfileImageUrl(lecture.getUser().getProfile_image_url());
        dto.setProfileImageUrlSmall(lecture.getUser().getProfile_image_url_small());
        dto.setLiveTitle(lecture.getLiveTitle());
        dto.setLiveContent(lecture.getLiveContent());
        dto.setAvailableDay(lecture.getAvailableDay());
        dto.setStartDate(lecture.getStartDate().toEpochMilli());
        dto.setStartTime(
            ZonedDateTime.ofInstant(lecture.getStartTime(), ZoneId.of("UTC")).toLocalTime()
                .toNanoOfDay() / 1_000_000);
        dto.setEndDate(lecture.getEndDate().toEpochMilli());
        dto.setEndTime(ZonedDateTime.ofInstant(lecture.getEndTime(), ZoneId.of("UTC")).toLocalTime()
            .toNanoOfDay() / 1_000_000);
        dto.setMaxLiveNum(lecture.getMaxLiveNum());
        return dto;
    }
}