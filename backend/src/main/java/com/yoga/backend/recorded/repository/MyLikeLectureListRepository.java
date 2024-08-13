package com.yoga.backend.recorded.repository;

import static com.querydsl.core.types.Projections.constructor;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.ExpressionUtils;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLectureLike;
import com.yoga.backend.recorded.dto.LectureDto;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MyLikeLectureListRepository {

    private final JPAQueryFactory queryFactory;

    public MyLikeLectureListRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<LectureDto> findMyLikedLectures(int userId) {
        QRecordedLecture lecture = QRecordedLecture.recordedLecture;
        QRecordedLectureLike like = QRecordedLectureLike.recordedLectureLike;

        NumberPath<Long> likeCount = Expressions.numberPath(Long.class, "likeCount");

        return queryFactory
            .select(constructor(LectureDto.class,
                lecture.id.as("recordedId"),
                lecture.user.id.as("userId"),
                lecture.user.nickname.as("nickname"),
                lecture.title.as("recordTitle"),
                lecture.content.as("recordContent"),
                lecture.thumbnail.as("recordThumbnail"),
                lecture.thumbnailSmall.as("recordThumbnailSmall"),
                ExpressionUtils.as(JPAExpressions.select(like.id.count())
                    .from(like)
                    .where(like.lecture.eq(lecture)), likeCount),
                lecture.createdDate.as("createdDate"),
                lecture.lastModifiedDate.as("lastModifiedDate"),
                Expressions.asBoolean(true).as("myLike")
            ))
            .from(lecture)
            .join(like).on(like.lecture.eq(lecture).and(like.user.id.eq(userId)))
            .orderBy(lecture.createdDate.desc())
            .fetch();
    }
}
