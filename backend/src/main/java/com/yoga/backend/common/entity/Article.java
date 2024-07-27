package com.yoga.backend.common.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 게시글(공지사항) 엔티티 클래스
 */
@Data
@Entity
@Table(name = "Article")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId; // 게시글 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;  // 작성자 (강사)

    private String content; // 게시글 내용
    private LocalDateTime createdAt; // 생성일자
    private LocalDateTime updatedAt; // 수정일자
    @Column(length = 512)
    private String imageUrl; // 이미지 URL
    @Column(length = 512)
    private String imageUrlSmall;

    @Version
    private Integer version; // 낙관적 락을 위한 버전 필드

    /**
     * 게시글 생성 전에 호출되어 생성일자를 설정합니다.
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.version = 0;
    }

    /**
     * 게시글 수정 전에 호출되어 수정일자를 설정합니다.
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 이미지 URL을 반환합니다.
     *
     * @return 이미지 URL
     */
    public String getImage() {
        return imageUrl;
    }

    /**
     * 이미지 URL을 설정합니다.
     *
     * @param image 이미지 URL
     */
    public void setImage(String image) {
        this.imageUrl = image;
    }
}
