package com.yoga.backend.mypage.article;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
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

/**
 * 게시글(공지사항) 관련 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/mypage/notification")
public class ArticleController {

    private final ArticleService articleService;
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;

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
            int userId = jwtUtil.getUserIdFromToken(token);
            Optional<Users> optionalUser = usersRepository.findById(userId);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                Article article = new Article();
                article.setUser(user);
                article.setContent(articleDto.getContent());
                article.setImage(articleDto.getImageUrl());
                article.setImageUrlSmall(articleDto.getImageUrlSmall());
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
            int userId = jwtUtil.getUserIdFromToken(token);
            Optional<Users> optionalUser = usersRepository.findById(userId);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                List<Article> articles = articleService.getArticlesByUserId(user.getId());

                List<ArticleDto> articleDtos = articles.stream()
                    .sorted(Comparator.comparing(Article::getCreatedAt).reversed())
                    .map(this::convertArticleToDto)
                    .collect(Collectors.toList());

                response.put("message", "success");
                response.put("data", articleDtos);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "권한이 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
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
        Map<String, Object> response = new HashMap<>();
        try {
            int userId = jwtUtil.getUserIdFromToken(token);
            Optional<Users> optionalUser = usersRepository.findById(userId);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                Optional<Article> optionalArticle = articleService.getArticleById(id);
                if (optionalArticle.isEmpty()) {
                    response.put("message", "게시글을 찾을 수 없습니다");
                    response.put("data", new Object[]{});
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Article existingArticle = optionalArticle.get();
                if (!existingArticle.getUser().getNickname().equals(user.getNickname())) {
                    response.put("message", "권한이 없습니다");
                    response.put("data", new Object[]{});
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }

                Article updatedArticle = articleService.updateArticle(id, articleDto.getContent(),
                    articleDto.getImageUrl(), articleDto.getImageUrlSmall());
                response.put("message", "게시글 수정 성공");
                response.put("data", convertArticleToMap(updatedArticle));
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("message", "권한이 없습니다");
                response.put("data", new Object[]{});
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
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
            Optional<Users> optionalUser = usersRepository.findById(userId);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                Optional<Article> optionalArticle = articleService.getArticleById(id);
                if (optionalArticle.isEmpty()) {
                    response.put("message", "게시글을 찾을 수 없습니다");
                    response.put("data", new Object[]{});
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }

                Article existingArticle = optionalArticle.get();
                if (!existingArticle.getUser().getNickname().equals(user.getNickname())) {
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

        Users user = article.getUser();
        if (user != null) {
            map.put("userId", user.getId());
            map.put("userName", user.getEmail());
            map.put("profileImageUrl", user.getProfile_image_url());
        } else {
            map.put("userId", null);
            map.put("userName", null);
            map.put("profileImageUrl", null);
        }

        map.put("content", article.getContent());
        map.put("createdAt", article.getCreatedAt().atZone(ZoneOffset.ofHours(9)).toInstant().toEpochMilli());
        map.put("updatedAt", article.getUpdatedAt().atZone(ZoneOffset.ofHours(9)).toInstant().toEpochMilli());
        map.put("imageUrl", article.getImage());
        map.put("imageUrlSmall", article.getImageUrlSmall());
        return map;
    }
    private ArticleDto convertArticleToDto(Article article) {
        ArticleDto dto = new ArticleDto();
        dto.setArticleId(article.getArticleId());
        dto.setContent(article.getContent());
        dto.setImageUrl(article.getImage());
        dto.setImageUrlSmall(article.getImageUrlSmall());
        dto.setCreatedAt(article.getCreatedAt().atZone(ZoneOffset.ofHours(9)).toInstant().toEpochMilli());
        dto.setUpdatedAt(article.getUpdatedAt().atZone(ZoneOffset.ofHours(9)).toInstant().toEpochMilli());

        Users author = article.getUser();
        if (author != null) {
            dto.setUserName(author.getNickname());
            dto.setProfileImageUrl(author.getProfile_image_url());
            dto.setProfileImageSmallUrl(author.getProfile_image_url_small());
        }

        return dto;
    }
}
