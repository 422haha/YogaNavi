package com.yoga.backend.mypage.recorded.repository;

import static com.querydsl.core.types.Projections.constructor;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLectureChapter;
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
        QRecordedLectureChapter chapter = QRecordedLectureChapter.recordedLectureChapter;

        return queryFactory
            .select(Projections.constructor(LectureDto.class,
                lecture.id.as("recordedId"),
                lecture.title.as("recordTitle"),
                lecture.thumbnail.as("recordThumbnail"),
                lecture.likeCount.as("likeCount"),
                Expressions.as(Expressions.constant(false), "myLike")
            ))
            .from(lecture)
            .leftJoin(lecture.chapters, chapter)
            .where(lecture.userId.eq(userId))
            .groupBy(lecture.id, lecture.title, lecture.thumbnail, lecture.likeCount)
            .fetch();
    }
}
