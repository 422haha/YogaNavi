package com.yoga.backend.mypage.article;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.UsersRepository;
import com.yoga.backend.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 게시글(공지사항) 관련 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/mypage/notification")
public class ArticleController {

    private final ArticleService articleService;
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    /**
     * ArticleController 생성자
     *
     * @param articleService  ArticleService 객체
     * @param usersRepository UsersRepository 객체
     * @param jwtUtil         JwtUtil 객체
     */
    @Autowired
    public ArticleController(ArticleService articleService, UsersRepository usersRepository,
        JwtUtil jwtUtil) {
        this.articleService = articleService;
        this.usersRepository = usersRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 새로운 게시글을 작성합니다.
     *
     * @param token      JWT 토큰
     * @param articleDto 게시글 DTO
     * @return 작성 결과 메시지와 데이터
     */
    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> createArticle(
        @RequestHeader("Authorization") String token, @RequestBody ArticleDto articleDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token); // JWT 토큰에서 사용자 ID 추출
            Optional<Users> optionalUser = usersRepository.findById((long) userId);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                Article article = new Article();
                article.setUser(user);
                article.setContent(articleDto.getContent());
                article.setImage(articleDto.getImageUrl());
                article.setCreatedAt(LocalDateTime.now());
                article.setUpdatedAt(LocalDateTime.now());

                // 게시글을 저장합니다.
                articleService.saveArticle(article);

                response.put("message", "게시글 작성 성공");
                response.put("data", convertArticleToMap(article));
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("message", "권한이 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("게시글 작성 중 오류 발생", e);
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 모든 게시글을 조회합니다.
     *
     * @return 게시글 목록
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllArticles() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 모든 게시글을 가져옵니다.
            List<Article> articles = articleService.getAllArticles();

            // 게시글을 생성일 기준으로 역순으로 정렬합니다.
            List<Map<String, Object>> articleList = articles.stream()
                .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                .map(this::convertArticleToMap)
                .collect(Collectors.toList());

            response.put("message", "success");
            response.put("data", articleList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("모든 게시글 조회 중 오류 발생", e);
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 사용자가 작성한 모든 게시글을 조회합니다.
     *
     * @param token JWT 토큰
     * @return 게시글 목록
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getArticlesByUserId(
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token); // JWT 토큰에서 사용자 ID 추출
            Optional<Users> optionalUser = usersRepository.findById((long) userId);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                List<Article> articles = articleService.getArticlesByUserId(user.getId());

                // 게시글을 생성일 기준으로 역순으로 정렬합니다.
                List<Map<String, Object>> articleList = articles.stream()
                    .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                    .map(this::convertArticleToMap)
                    .collect(Collectors.toList());

                response.put("message", "success");
                response.put("data", articleList);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "권한이 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("사용자 ID로 게시글 조회 중 오류 발생", e);
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 게시글을 조회합니다.
     *
     * @param id 게시글 ID
     * @return 게시글 정보
     */
    @GetMapping("/update/{article_id}")
    public ResponseEntity<Map<String, Object>> getArticleById(
        @PathVariable("article_id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 게시글 ID로 특정 게시글을 가져옵니다.
            Optional<Article> article = articleService.getArticleById(id);
            if (article.isPresent()) {
                response.put("message", "success");
                response.put("data", convertArticleToMap(article.get()));
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "게시글을 찾을 수 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            logger.error("게시글 ID로 게시글 조회 중 오류 발생", e);
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 게시글을 업데이트합니다.
     *
     * @param token      JWT 토큰
     * @param id         게시글 ID
     * @param articleDto 수정할 게시글 정보
     * @return 수정 결과 메시지와 데이터
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateArticle(
        @RequestHeader("Authorization") String token, @PathVariable Long id,
        @RequestBody ArticleDto articleDto) {
        logger.info("updateArticle:"+articleDto);
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token); // JWT 토큰에서 사용자 ID 추출
            Optional<Users> optionalUser = usersRepository.findById((long) userId);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                Optional<Article> optionalArticle = articleService.getArticleById(id);
                if (optionalArticle.isEmpty()) {
                    response.put("message", "게시글을 찾을 수 없습니다");
                    response.put("data", new Object[]{});
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Article existingArticle = optionalArticle.get();
                if (!existingArticle.getUser().getEmail().equals(user.getEmail())) {
                    response.put("message", "권한이 없습니다");
                    response.put("data", new Object[]{});
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }

                // 게시글 내용을 업데이트합니다.
                Article updatedArticle = articleService.updateArticle(id, articleDto.getContent(),
                    articleDto.getImageUrl());
                response.put("message", "게시글 수정 성공");
                response.put("data", convertArticleToMap(updatedArticle));
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "권한이 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("게시글 수정 중 오류 발생", e);
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 게시글을 삭제합니다.
     *
     * @param token JWT 토큰
     * @param id    게시글 ID
     * @return 삭제 결과 메시지와 데이터
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteArticle(
        @RequestHeader("Authorization") String token, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token); // JWT 토큰에서 사용자 ID 추출
            Optional<Users> optionalUser = usersRepository.findById((long) userId);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                Optional<Article> optionalArticle = articleService.getArticleById(id);
                if (optionalArticle.isEmpty()) {
                    response.put("message", "게시글을 찾을 수 없습니다");
                    response.put("data", new Object[]{});
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Article existingArticle = optionalArticle.get();
                if (!existingArticle.getUser().getEmail().equals(user.getEmail())) {
                    response.put("message", "권한이 없습니다");
                    response.put("data", new Object[]{});
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }

                // 게시글을 삭제합니다.
                articleService.deleteArticle(id);

                response.put("message", "게시글 삭제 성공");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "권한이 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("게시글 삭제 중 오류 발생", e);
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Article 객체를 Map으로 변환합니다.
     *
     * @param article 게시글 객체
     * @return 변환된 Map 객체
     */
    private Map<String, Object> convertArticleToMap(Article article) {
        Map<String, Object> map = new HashMap<>();
        map.put("articleId", article.getArticleId());

        // 작성자 정보가 null인지 확인합니다.
        Users user = article.getUser();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("userName", user.getEmail()); // 사용자 이메일을 userName으로 사용
            map.put("profileImageUrl", user.getProfile_image_url()); // 사용자 프로필 이미지 추가
        } else {
            map.put("userId", null);
            map.put("userName", null);
            map.put("profileImageUrl", null);
        }

        map.put("content", article.getContent());
        map.put("createdAt",
            article.getCreatedAt().atZone(ZoneOffset.ofHours(9)).toInstant().toEpochMilli());
        map.put("updatedAt",
            article.getUpdatedAt().atZone(ZoneOffset.ofHours(9)).toInstant().toEpochMilli());
        map.put("imageUrl", article.getImage());
        return map;
    }
}
