package com.yoga.backend.recorded.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LectureDto {

    private Long recordedId;
    private Integer userId;
    private String nickname;
    private String recordTitle;
    private String recordContent;
    private String recordThumbnail;
    private String recordThumbnailSmall;
    private List<ChapterDto> recordedLectureChapters;
    private Long likeCount;
    private Boolean myLike;
    private String creationStatus;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    // QueryDSL용 생성자
    public LectureDto(Long recordedId, Integer userId, String nickname, String recordTitle,
        String recordContent,
        String recordThumbnail, String recordThumbnailSmall, Long likeCount,
        LocalDateTime createdDate, LocalDateTime lastModifiedDate, Boolean myLike) {
        this.recordedId = recordedId;
        this.userId = userId;
        this.nickname = nickname;
        this.recordTitle = recordTitle;
        this.recordContent = recordContent;
        this.recordThumbnail = recordThumbnail;
        this.recordThumbnailSmall = recordThumbnailSmall;
        this.likeCount = likeCount;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.myLike = myLike;
    }

    // findAllLectures, findMyLikedLectures
    public LectureDto(Long recordedId, Long userId, String nickname, String recordTitle,
        String recordThumbnailSmall, String recordThumbnail, Long likeCount, Boolean myLike) {
        this.recordedId = recordedId;
        this.userId = userId != null ? userId.intValue() : null;
        this.nickname = nickname;
        this.recordTitle = recordTitle;
        this.recordThumbnailSmall = recordThumbnailSmall;
        this.recordThumbnail = recordThumbnail;
        this.likeCount = likeCount != null ? likeCount : 0L;
        this.myLike = myLike != null && myLike;
    }

    public LectureDto(Long id, Integer userId, String title, String content,
        String thumbnail, String thumbnailSmall, Long likeCount,
        LocalDateTime createdDate, LocalDateTime lastModifiedDate,
        Boolean myLike) {
        this.recordedId = id;
        this.userId = userId;
        this.recordTitle = title;
        this.recordContent = content;
        this.recordThumbnailSmall = thumbnailSmall;
        this.recordThumbnail = thumbnail;
        this.likeCount = likeCount;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.myLike = myLike;
    }

    public LectureDto(Long recordedId, String recordTitle, String recordContent,
        String recordThumbnail, String recordThumbnailSmall,
        Long likeCount, LocalDateTime createdDate, LocalDateTime lastModifiedDate,
        Boolean myLike) {
        this.recordedId = recordedId;
        this.recordTitle = recordTitle;
        this.recordContent = recordContent;
        this.recordThumbnail = recordThumbnail;
        this.recordThumbnailSmall = recordThumbnailSmall;
        this.likeCount = likeCount;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.myLike = myLike;
    }

    public LectureDto(Long recordedId, String recordTitle, String recordThumbnail, Long likeCount,
        Boolean myLike, String creationStatus) {
        this.recordedId = recordedId;
        this.recordTitle = recordTitle;
        this.recordThumbnail = recordThumbnail;
        this.likeCount = likeCount;
        this.myLike = myLike != null && myLike;
        this.creationStatus = creationStatus;
    }

    public LectureDto(Long recordedId, Integer userId, String recordTitle,
        String recordThumbnailSmall, String recordThumbnail,
        Long likeCount, Boolean myLike) {
        this.recordedId = recordedId;
        this.userId = userId;
        this.recordTitle = recordTitle;
        this.recordThumbnailSmall = recordThumbnailSmall;
        this.recordThumbnail = recordThumbnail;
        this.likeCount = likeCount;
        this.myLike = myLike;
    }

    public LectureDto(Long recordedId, Integer userId, String recordTitle,
        String recordContent, String recordThumbnailSmall,
        Long likeCount, LocalDateTime createdDate,
        LocalDateTime lastModifiedDate, Boolean myLike) {
        this.recordedId = recordedId;
        this.userId = userId;
        this.recordTitle = recordTitle;
        this.recordContent = recordContent;
        this.recordThumbnailSmall = recordThumbnailSmall;
        this.likeCount = likeCount;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.myLike = myLike;
    }

    public LectureDto() {
    }
}
