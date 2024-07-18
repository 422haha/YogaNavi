package com.yoga.backend;

import com.yoga.backend.common.entity.Article;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.UsersRepository;
import com.yoga.backend.mypage.article.ArticleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Test
    @WithMockUser(username = "example2@example.com", roles = {"STUDENT"})
    public void testCreateArticle() throws Exception {
        // 유저가 존재하지 않을 경우를 대비해 유저를 미리 저장합니다.
        Users user = new Users();
        user.setEmail("example2@example.com");
        usersRepository.save(user);

        String newArticleJson = "{ \"title\": \"Test Unique Title\", \"content\": \"Test Content2\", \"imageUrl\": \"http://example.com/image.jpg\" }";

        mockMvc.perform(post("/mypage/notification/write")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newArticleJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("success"));

        // 테스트 후 DB에 저장된 데이터 확인
        Optional<Users> retrievedUser = usersRepository.findByEmail("example2@example.com").stream().findFirst();
        assertThat(retrievedUser).isPresent();

        List<Article> articles = articleRepository.findByTitle("Test Unique Title");
        assertThat(articles).isNotEmpty();
        Article article = articles.get(0);  // 첫 번째 결과만 확인
        assertThat(article.getContent()).isEqualTo("Test Content2");
        assertThat(article.getImageUrl()).isEqualTo("http://example.com/image.jpg");
    }

    @Test
    @WithMockUser(username = "example2@example.com", roles = {"STUDENT"})
    public void testGetAllArticles() throws Exception {
        mockMvc.perform(get("/mypage/notification/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("success"));
    }
}
