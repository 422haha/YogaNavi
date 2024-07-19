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

    private Long recordedId; // Changed to Long
    private String email;
    private String recordTitle;
    private String recordContent;
    private String recordThumbnail;
    private List<ChapterDto> recordedLectureChapters;
    private long likeCount;
    private boolean myLike;

    // QueryDSL용 생성자 추가
    public LectureDto(Long recordedId, String recordTitle, String recordThumbnail, long likeCount, Boolean myLike) {
        this.recordedId = recordedId;
        this.recordTitle = recordTitle;
        this.recordThumbnail = recordThumbnail;
        this.likeCount = likeCount;
        this.myLike = myLike;
    }

}
