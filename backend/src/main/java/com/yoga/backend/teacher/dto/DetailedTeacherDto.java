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
    private String profileImageUrlSmall; // 필드 추가
    private String content;
    private Set<String> hashtags;
    private List<LectureDto> recordedLectures;
    private List<NoticeDto> notices;

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
     */
    public DetailedTeacherDto(int id, String email, String nickname, String profileImageUrl,
        String profileImageUrlSmall, String content, Set<String> hashtags,
        List<LectureDto> recordedLectures, List<NoticeDto> notices) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.profileImageUrlSmall = profileImageUrlSmall;
        this.content = content;
        this.hashtags = hashtags;
        this.recordedLectures = recordedLectures;
        this.notices = notices;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileImageUrlSmall() {
        return profileImageUrlSmall;
    }

    public void setProfileImageUrlSmall(String profileImageUrlSmall) {
        this.profileImageUrlSmall = profileImageUrlSmall;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

    public List<LectureDto> getRecordedLectures() {
        return recordedLectures;
    }

    public void setRecordedLectures(List<LectureDto> recordedLectures) {
        this.recordedLectures = recordedLectures;
    }

    public List<NoticeDto> getNotices() {
        return notices;
    }

    public void setNotices(List<NoticeDto> notices) {
        this.notices = notices;
    }

    /**
     * 강의 DTO 클래스
     */
    @Data
    @Builder
    public static class LectureDto {

        private String lectureId;
        private String lectureTitle;
        private String lectureDescription;
        private int likes;
        private boolean likedByUser;

        /**
         * LectureDto 생성자
         *
         * @param lectureId          강의 ID
         * @param lectureTitle       강의 제목
         * @param lectureDescription 강의 설명
         * @param likes              좋아요 수
         * @param likedByUser        사용자에 의한 좋아요 여부
         */
        public LectureDto(String lectureId, String lectureTitle, String lectureDescription,
            int likes, boolean likedByUser) {
            this.lectureId = lectureId;
            this.lectureTitle = lectureTitle;
            this.lectureDescription = lectureDescription;
            this.likes = likes;
            this.likedByUser = likedByUser;
        }
    }

    /**
     * 공지 DTO 클래스
     */
    @Data
    @Builder
    public static class NoticeDto {

        private String noticeId;
        private String noticeContent;
        private String noticeImage;  // 공지 이미지 URL
        private String noticeImageSmall; // 공지 작은 이미지 URL

        /**
         * NoticeDto 생성자
         *
         * @param noticeId         공지 ID
         * @param noticeContent    공지 내용
         * @param noticeImage      공지 이미지 URL
         * @param noticeImageSmall 공지 작은 이미지 URL
         */
        public NoticeDto(String noticeId, String noticeContent, String noticeImage,
            String noticeImageSmall) {
            this.noticeId = noticeId;
            this.noticeContent = noticeContent;
            this.noticeImage = noticeImage;
            this.noticeImageSmall = noticeImageSmall;
        }
    }
}
