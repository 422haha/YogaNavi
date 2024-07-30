package com.yoga.backend.home;

import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.livelectures.LiveLectureService;
import com.yoga.backend.mypage.livelectures.MyLiveLectureRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 홈 컨트롤러 클래스.
 * 홈 관련 요청을 처리합니다.
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    // LiveLectureService 인스턴스를 주입받습니다.
    @Autowired
    private LiveLectureService liveLectureService;

    // MyLiveLectureRepository 인스턴스를 주입받습니다.
    @Autowired
    private MyLiveLectureRepository myLiveLectureRepository;

    // UsersRepository 인스턴스를 주입받습니다.
    @Autowired
    private UsersRepository usersRepository;

    // JwtUtil 인스턴스를 주입받습니다.
    @Autowired
    private JwtUtil jwtUtil;

    // HomeService 인스턴스를 주입받습니다.
    @Autowired
    private HomeService homeService;


    /**
     * 홈 페이지 요청을 처리합니다.
     *
     * @param token JWT 토큰
     * @return 홈 페이지에 대한 응답 DTO를 포함한 ResponseEntity 객체
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHomeData(@RequestHeader("Authorization") String token) {
        // JWT 토큰에서 사용자 ID를 추출합니다.
        Integer userId = jwtUtil.getUserIdFromToken(token);
        // 사용자 ID로 홈 데이터를 가져옵니다.
        List<HomeResponseDto> homeData = homeService.getHomeData(userId);

        // 홈 데이터가 비어있는 경우
        if (homeData.isEmpty()) {
            // 성공 메시지와 빈 리스트를 포함한 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "내 화상 강의 할 일 조회 성공", "data", List.of()));
        }

        // 홈 데이터를 포함한 성공 응답을 반환합니다.
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "내 화상 강의 할 일 조회 성공", "data", homeData));
    }
}