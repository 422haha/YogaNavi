package com.yoga.backend.mypage.recorded.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLectureLike;
import com.yoga.backend.mypage.recorded.dto.LectureDto;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class RecordedLectureListRepository {
    private final JPAQueryFactory queryFactory;

    public RecordedLectureListRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<LectureDto> findAllLectures(int userId) {
        QRecordedLecture lecture = QRecordedLecture.recordedLecture;
        QRecordedLectureLike like = QRecordedLectureLike.recordedLectureLike;

        return queryFactory
            .select(Projections.constructor(LectureDto.class,
                lecture.id.as("recordedId"),
                lecture.title.as("recordTitle"),
                lecture.thumbnailSmall.as("recordThumbnailSmall"),
                lecture.likeCount.as("likeCount"),
                JPAExpressions.selectOne()
                    .from(like)
                    .where(like.lecture.eq(lecture).and(like.user.id.eq(userId)))
                    .exists().as("myLike")
            ))
            .from(lecture)
            .where(lecture.user.id.eq(userId))
            .fetch();
    }
}