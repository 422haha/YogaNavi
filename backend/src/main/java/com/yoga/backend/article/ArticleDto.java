package com.yoga.backend.article;

import lombok.Data;

/**
 * 게시글(공지사항) DTO
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
