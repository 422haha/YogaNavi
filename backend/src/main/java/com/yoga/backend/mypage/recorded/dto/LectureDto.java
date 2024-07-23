package com.yoga.backend.mypage.recorded.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<ChapterDto> recordedLectureChapters;
    private long likeCount;
    private boolean myLike;
    private String creationStatus;

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