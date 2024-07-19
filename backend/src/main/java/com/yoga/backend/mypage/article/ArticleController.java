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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * @return 작성 결과 메시지
     */
    @PostMapping("/write")
    public ResponseEntity<Map<String, Object>> createArticle(
        @RequestHeader("Authorization") String token, @RequestBody ArticleDto articleDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Users> optionalUser = getUserFromToken(token);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                Article article = new Article();
                article.setUser(user);
                article.setContent(articleDto.getContent());
                article.setImageUrl(articleDto.getImageUrl());
                article.setCreatedAt(LocalDateTime.now());
                article.setUpdatedAt(LocalDateTime.now());

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
            logger.error("Error creating article", e);
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
            List<Article> articles = articleService.getAllArticles();
            List<Map<String, Object>> articleList = articles.stream()
                .map(this::convertArticleToMap)
                .collect(Collectors.toList());
            response.put("message", "success");
            response.put("data", Map.of("article_list", articleList));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching all articles", e);
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
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getArticlesByUserId(
        @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Users> optionalUser = getUserFromToken(token);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                List<Article> articles = articleService.getArticlesByUserId(user.getId());
                List<Map<String, Object>> articleList = articles.stream()
                    .map(this::convertArticleToMap)
                    .collect(Collectors.toList());
                response.put("message", "success");
                response.put("data", Map.of("article_list", articleList));
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "권한이 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("Error fetching articles by user ID", e);
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 특정 게시글을 수정합니다.
     *
     * @param token      JWT 토큰
     * @param id         게시글 ID
     * @param articleDto 수정할 게시글 정보
     * @return 수정 결과 메시지
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateArticle(
        @RequestHeader("Authorization") String token, @PathVariable Long id,
        @RequestBody ArticleDto articleDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Users> optionalUser = getUserFromToken(token);

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

                existingArticle.setContent(articleDto.getContent());
                existingArticle.setImageUrl(articleDto.getImageUrl());
                existingArticle.setUpdatedAt(LocalDateTime.now());
                articleService.saveArticle(existingArticle);

                response.put("message", "게시글 수정 성공");
                response.put("data", convertArticleToMap(existingArticle));
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "권한이 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("Error updating article", e);
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
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteArticle(
        @RequestHeader("Authorization") String token, @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Users> optionalUser = getUserFromToken(token);

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
            logger.error("Error deleting article", e);
            response.put("message", "서버 내부 오류가 발생했습니다");
            response.put("data", new Object[]{});
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * JWT 토큰을 이용해 사용자 정보를 가져옵니다.
     *
     * @param token JWT 토큰
     * @return 사용자 정보
     */
    private Optional<Users> getUserFromToken(String token) {
        String email = jwtUtil.getEmailFromToken(token);
        return usersRepository.findByEmail(email).stream().findFirst();
    }

    /**
     * Article 객체를 Map으로 변환합니다.
     *
     * @param article 게시글 객체
     * @return 변환된 Map 객체
     */
    private Map<String, Object> convertArticleToMap(Article article) {
        Map<String, Object> map = new HashMap<>();
        map.put("article_id", article.getArticleId());

        // 작성자 정보가 null인지 확인합니다.
        Users user = article.getUser();
        if (user != null) {
            map.put("user_id", user.getId());
            map.put("user_name", user.getEmail()); // assuming user_name is the email
        } else {
            map.put("user_id", null);
            map.put("user_name", null);
        }

        map.put("content", article.getContent());
        map.put("created_at", article.getCreatedAt().toString());
        map.put("image_url", article.getImageUrl());
        return map;
    }
}
