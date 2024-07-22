package com.yoga.backend.mypage.livelectures;

import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.MyLiveLecture;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.util.JwtUtil;
import com.yoga.backend.members.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 실시간 강의 컨트롤러 클래스
 * 실시간 강의 생성 및 조회에 대한 API 엔드포인트를 제공
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
     * @param liveLectureDto 실시간 강의 DTO
     * @param request HttpServletRequest 객체
     * @return 생성된 실시간 강의 엔티티
     */

    @PostMapping("/create")
    public LiveLectures createLiveLecture(@RequestBody @Valid LiveLectureDto liveLectureDto,
        HttpServletRequest request) {
        // JWT 및 CSRF 토큰 검증 로직 추가 필요
        // 예: String accessToken = request.getHeader("accessToken");
        String token = request.getHeader("accessToken"); // 헤더에서 accessToken 추출
        String email = jwtUtil.getEmailFromToken(token); // accessToken에서 이메일 추출
        // 이메일을 이용하여 사용자 정보 조회
        Users user = (Users) entityManager.createQuery("SELECT u FROM Users u WHERE u.email = :email", Users.class)
            .setParameter("email", email)
            .getSingleResult();

//        liveLectureDto.setUserId(user.getId()); // DTO에 사용자 ID 설정

        return liveLectureService.createLiveLecture(liveLectureDto);// 화상 강의 생성
    }
    /**
     * 특정 사용자 ID에 대한 나의 실시간 강의 목록을 조회하는 API 엔드포인트
     * @param request HttpServletRequest 객체
     * @return 나의 실시간 강의 리스트
     */
    @GetMapping
    public List<MyLiveLecture> getMyLiveLectures(HttpServletRequest request) {
        // JWT 토큰 검증 로직 추가 필요
        // 예: String accessToken = request.getHeader("accessToken");
//        return liveLectureService.getAllLiveLectures();
// JWT 토큰에서 userId를 추출하는 로직 필요
//        Long userId = ...;
//        1. 토큰을 받아온다.
        String token = request.getHeader("accessToken");
//        2. 토큰값을 JwtUtil을 이용해서 Email으로 바꾼다.
        String email = jwtUtil.getEmailFromToken(token);
//        3. Email을 이용해서 user id 를 찾는다.
        Users user = (Users) entityManager.createQuery("SELECT u FROM Users u WHERE u.email = :email", Users.class)
            .setParameter("email", email)
            .getSingleResult();

//        return liveLectureService.getMyLiveLecturesByUserId(user.getId());  // 사용자가 등록한 화상 강의 목록 조회
        return null;
    }


}