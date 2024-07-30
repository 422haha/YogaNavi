package com.yoga.backend.mypage.recorded.dto;

import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@AllArgsConstructor
public class LectureDto {

    private Long recordedId;
    private int userId;
    private String recordTitle;
    private String recordContent;
    private String recordThumbnail;
    private String recordThumbnailSmall;
    private List<ChapterDto> recordedLectureChapters;
    private long likeCount;
    private boolean myLike;
    private String creationStatus;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    // QueryDSL용 생성자 수정
    //
    public LectureDto(Long recordedId, String recordTitle, String recordContent,
        String recordThumbnailSmall, Long likeCount,
        LocalDateTime createdDate, LocalDateTime lastModifiedDate,
        Boolean myLike) {
        this.recordedId = recordedId;
        this.recordTitle = recordTitle;
        this.recordContent = recordContent;
        this.recordThumbnailSmall = recordThumbnailSmall;
        this.likeCount = likeCount;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.myLike = myLike != null && myLike;
    }

    // findAllLectures, findMyLikedLectures
    public LectureDto(Long recordedId, String recordTitle, String recordThumbnailSmall,
        String recordThumbnail, Long likeCount, Boolean myLike) {
        this.recordedId = recordedId;
        this.recordTitle = recordTitle;
        this.recordThumbnailSmall = recordThumbnailSmall;
        this.recordThumbnail = recordThumbnail;
        this.likeCount = likeCount != null ? likeCount : 0L;
        this.myLike = myLike != null && myLike;
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

    public LectureDto(Long recordedId, String recordTitle, String recordThumbnail, long likeCount,
        Boolean myLike, String creationStatus) {
        this.recordedId = recordedId;
        this.recordTitle = recordTitle;
        this.recordThumbnail = recordThumbnail;
        this.likeCount = likeCount;
        this.myLike = myLike != null && myLike;
        this.creationStatus = creationStatus;
    }

    public LectureDto() {
    }
}