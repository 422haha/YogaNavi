package com.yoga.backend.mypage.recorded.repository;

import static com.querydsl.core.types.ExpressionUtils.as;
import static com.querydsl.core.types.ExpressionUtils.count;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
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

    public List<LectureDto> findAllLectures(String email) {
        QRecordedLecture lecture = QRecordedLecture.recordedLecture;
        QRecordedLectureLike like = QRecordedLectureLike.recordedLectureLike;

        return queryFactory
            .select(Projections.constructor(LectureDto.class,

//                lecture.email,
                as(lecture.id,"recorded_id"),
                as(lecture.title, "record_title"),
                as(lecture.thumbnail, "record_thumbnail"),
                as(JPAExpressions.select(count(like.id))
                    .from(like)
                    .where(like.lecture.eq(lecture)), "like_count"),
                as(Expressions.constant(false), "my_like")
            ))
            .from(lecture)
            .where(lecture.email.eq(email))
            .fetch();
    }
}