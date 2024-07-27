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
@NoArgsConstructor
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
    public LectureDto(Long recordedId, String recordTitle, String recordThumbnail,
        long likeCount, boolean myLike) {
        this.recordedId = recordedId;
        this.recordTitle = recordTitle;
        this.recordThumbnail = recordThumbnail;
        this.likeCount = likeCount;
        this.myLike = myLike;
    }

    public LectureDto(Long recordedId, String recordTitle, String recordThumbnail, long likeCount,
        Boolean myLike, String creationStatus) {
        this.recordedId = recordedId;
        this.recordTitle = recordTitle;
        this.recordThumbnail = recordThumbnail;
        this.likeCount = likeCount;
        this.myLike = myLike;
        this.creationStatus = creationStatus;
    }
}