package com.yoga.backend.mypage.article;

import lombok.Data;

/**
 * 게시글(공지사항) DTO 클래스
 */
@Data
public class ArticleDto {

    private Long articleId;
    private String content;
    private String imageUrl;
    private String imageUrlSmall;
    private String userName;
    private String profileImageUrl;
    private String profileImageSmallUrl;
    private Long createdAt;
    private Long updatedAt;

}
