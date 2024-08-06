package com.yoga.backend.mypage.recorded.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLectureLike;
import com.yoga.backend.mypage.recorded.dto.LectureDto;

import java.util.Arrays;
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
                lecture.user.id.as("userId"),
                lecture.title.as("recordTitle"),
                lecture.thumbnailSmall.as("recordThumbnailSmall"),
                lecture.thumbnail.as("recordThumbnail"),
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

    public List<LectureDto> searchLectures(int userId, String keyword, String sort, int page,
        int size, boolean searchTitle, boolean searchContent) {
        QRecordedLecture lecture = QRecordedLecture.recordedLecture;
        QRecordedLectureLike like = QRecordedLectureLike.recordedLectureLike;

        // 키워드 개별 단어로 분리
        List<String> keywords = Arrays.asList(keyword.split("\\s+"));

        // 검색 조건 생성
        BooleanExpression keywordCondition = createSearchCondition(lecture, keywords, searchTitle,
            searchContent);

        // 쿼리
        var query = queryFactory
            .select(Projections.constructor(LectureDto.class,
                lecture.id.as("recordedId"),
                lecture.user.id.as("userId"),
                lecture.title.as("recordTitle"),
                lecture.content.as("recordContent"),
                lecture.thumbnailSmall.as("recordThumbnailSmall"),
                lecture.likeCount.as("likeCount"),
                lecture.createdDate,
                lecture.lastModifiedDate,
                JPAExpressions.selectOne()
                    .from(like)
                    .where(like.lecture.eq(lecture).and(like.user.id.eq(userId)))
                    .exists().as("myLike")
            ))
            .from(lecture)
            .where(keywordCondition);

        // 정렬 조건
        if ("fame".equals(sort)) {
            query.orderBy(lecture.likeCount.desc(), lecture.createdDate.desc());
        } else {
            query.orderBy(lecture.createdDate.desc());
        }

        // 페이지네이션 적용
        return query
            .offset((long) page * size)
            .limit(size)
            .fetch();
    }

    // 검색 조건을 생성
    private BooleanExpression createSearchCondition(QRecordedLecture lecture, List<String> keywords,
        boolean searchTitle, boolean searchContent) {
        BooleanExpression titleCondition = null;
        BooleanExpression contentCondition = null;

        // 각 키워드에 대해 검색 조건 생성
        for (String keyword : keywords) {
            if (searchTitle) {
                BooleanExpression titleExpression = lecture.title.containsIgnoreCase(keyword);
                titleCondition =
                    titleCondition == null ? titleExpression : titleCondition.or(titleExpression);
            }
            if (searchContent) {
                BooleanExpression contentExpression = lecture.content.containsIgnoreCase(keyword);
                contentCondition = contentCondition == null ? contentExpression
                    : contentCondition.or(contentExpression);
            }
        }

        // 제목과 내용 검색 조건 결합
        if (searchTitle && searchContent) {
            return titleCondition.or(contentCondition);
        } else if (searchTitle) {
            return titleCondition;
        } else if (searchContent) {
            return contentCondition;
        }

        return null;
    }
}

