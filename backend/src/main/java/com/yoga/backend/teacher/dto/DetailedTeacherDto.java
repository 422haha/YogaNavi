package com.yoga.backend.teacher.dto;

import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

/**
 * 상세 강사 DTO 클래스
 */
@Data
@Builder
public class DetailedTeacherDto {

    private int id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String profileImageUrlSmall;
    private String content;
    private Set<String> hashtags;
    private List<LectureDto> recordedLectures;
    private List<NoticeDto> notices;
    private int likeCount;
    private boolean liked;

    /**
     * DetailedTeacherDto 생성자
     *
     * @param id                   강사 ID
     * @param email                강사 이메일
     * @param nickname             강사 닉네임
     * @param profileImageUrl      강사 프로필 이미지 URL
     * @param profileImageUrlSmall 강사 프로필 이미지 작은 URL
     * @param content              강사 소개
     * @param hashtags             강사 해시태그
     * @param recordedLectures     녹화 강의 리스트
     * @param notices              공지 리스트
     * @param likeCount            좋아요 수
     * @param liked                좋아요 여부
     */
    public DetailedTeacherDto(int id, String email, String nickname, String profileImageUrl,
        String profileImageUrlSmall, String content, Set<String> hashtags,
        List<LectureDto> recordedLectures, List<NoticeDto> notices, int likeCount,
        boolean liked) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.profileImageUrlSmall = profileImageUrlSmall;
        this.content = content;
        this.hashtags = hashtags;
        this.recordedLectures = recordedLectures;
        this.notices = notices;
        this.likeCount = likeCount;
        this.liked = liked;
    }

    /**
     * 강의 DTO 클래스
     */
    @Data
    @Builder
    public static class LectureDto {

        private String recordedId;
        private String recordTitle;
        private String recordThumbnail;
        private String recordThumbnailSmall;
        private int likeCount;
        private boolean myLike;

        /**
         * LectureDto 생성자
         *
         * @param recordedId           녹화 강의 ID
         * @param recordTitle          강의 제목
         * @param recordThumbnail      강의 썸네일 URL
         * @param recordThumbnailSmall 강의 작은 썸네일 URL
         * @param likeCount            좋아요 수
         * @param myLike               사용자에 의한 좋아요 여부
         */
        public LectureDto(String recordedId, String recordTitle, String recordThumbnail,
            String recordThumbnailSmall, int likeCount, boolean myLike) {
            this.recordedId = recordedId;
            this.recordTitle = recordTitle;
            this.recordThumbnail = recordThumbnail;
            this.recordThumbnailSmall = recordThumbnailSmall;
            this.likeCount = likeCount;
            this.myLike = myLike;
        }
    }

    /**
     * 공지 DTO 클래스
     */
    @Data
    @Builder
    public static class NoticeDto {

        private String articleId;
        private String content;
        private String imageUrl;
        private String imageUrlSmall;
        private long createdAt;
        private long updatedAt;
        private String userName;
        private String profileImageUrl;
        private String profileImageSmallUrl;

        /**
         * NoticeDto 생성자
         *
         * @param articleId            공지 ID
         * @param content              공지 내용
         * @param imageUrl             공지 이미지 URL
         * @param imageUrlSmall        공지 작은 이미지 URL
         * @param createdAt            생성 시간
         * @param updatedAt            수정 시간
         * @param userName             사용자 이름
         * @param profileImageUrl      프로필 이미지 URL
         * @param profileImageSmallUrl 프로필 작은 이미지 URL
         */
        public NoticeDto(String articleId, String content, String imageUrl, String imageUrlSmall,
            long createdAt, long updatedAt, String userName, String profileImageUrl,
            String profileImageSmallUrl) {
            this.articleId = articleId;
            this.content = content;
            this.imageUrl = imageUrl;
            this.imageUrlSmall = imageUrlSmall;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.userName = userName;
            this.profileImageUrl = profileImageUrl;
            this.profileImageSmallUrl = profileImageSmallUrl;
        }
    }
}
