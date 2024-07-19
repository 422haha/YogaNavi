package com.yoga.backend.mypage.article;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.UsersRepository;
import com.yoga.backend.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 게시글(공지사항) 관련 요청을 처리하는 컨트롤러 클래스
 */
@RestController
@RequestMapping("/mypage/notification")
public class ArticleController {

    private final ArticleService articleService;
    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public ArticleController(ArticleService articleService, UsersRepository usersRepository, JwtUtil jwtUtil) {
        this.articleService = articleService;
        this.usersRepository = usersRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 새로운 게시글을 작성합니다.
     *
     * @param token JWT 토큰
     * @param articleDto 게시글 DTO
     * @return 작성 결과 메시지
     */
    @PostMapping("/write")
    public ResponseEntity<ApiResponse> createArticle(@RequestHeader("Authorization") String token, @RequestBody ArticleDto articleDto) {
        try {
            String email = jwtUtil.getEmailFromToken(token);

            Optional<Users> optionalUser = usersRepository.findByEmail(email).stream().findFirst();
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("권한이 없습니다", null));
            }

            Users user = optionalUser.get();
            Article article = new Article();
            article.setUser(user);
            article.setTitle(articleDto.getTitle());
            article.setContent(articleDto.getContent());
            article.setImageUrl(articleDto.getImageUrl());

            articleService.saveArticle(article);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("서버 내부 오류가 발생했습니다", null));
        }
    }

    /**
     * 모든 게시글을 조회합니다.
     *
     * @return 게시글 목록
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllArticles() {
        try {
            List<Article> articles = articleService.getAllArticles();
            return ResponseEntity.ok(new ApiResponse("success", articles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("서버 내부 오류가 발생했습니다", null));
        }
    }

    /**
     * 특정 사용자가 작성한 모든 게시글을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 게시글 목록
     */
    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponse> getArticlesByUserId(@PathVariable("user_id") int userId) {
        try {
            Optional<Users> optionalUser = usersRepository.findById((long) userId);  // 여기서 int를 long으로 변환
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("권한이 없습니다", null));
            }

            List<Article> articles = articleService.getArticlesByUserId(userId);

            return ResponseEntity.ok(new ApiResponse("success", articles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("서버 내부 오류가 발생했습니다", null));
        }
    }

    /**
     * 특정 게시글을 수정합니다.
     *
     * @param id         게시글 ID
     * @param articleDto 수정할 게시글 정보
     * @return 수정 결과 메시지
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateArticle(@PathVariable Long id, @RequestBody ArticleDto articleDto) {
        try {
            Optional<Article> optionalArticle = articleService.getArticleById(id);
            if (optionalArticle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Article not found", null));
            }

            Article existingArticle = optionalArticle.get();
            existingArticle.setTitle(articleDto.getTitle());
            existingArticle.setContent(articleDto.getContent());
            existingArticle.setImageUrl(articleDto.getImageUrl());
            articleService.saveArticle(existingArticle);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("서버 내부 오류가 발생했습니다", null));
        }
    }

    /**
     * 특정 게시글을 삭제합니다.
     *
     * @param id 게시글 ID
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteArticle(@PathVariable Long id) {
        try {
            Optional<Article> optionalArticle = articleService.getArticleById(id);
            if (optionalArticle.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Article not found", null));
            }

            articleService.deleteArticle(id);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("서버 내부 오류가 발생했습니다", null));
        }
    }

    /**
     * API 응답을 위한 클래스
     */
    private static class ApiResponse {
        private final String message; // 응답 메시지
        private final Object data; // 응답 데이터

        public ApiResponse(String message, Object data) {
            this.message = message;
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }
}
