package com.yoga.backend.mypage.recorded.repository;

import static com.querydsl.core.types.Projections.constructor;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.QRecordedLectureLike;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.mypage.recorded.dto.LectureDto;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MyLikeLectureListRepository {

    private final JPAQueryFactory queryFactory;
    private final UsersRepository usersRepository;

    public MyLikeLectureListRepository(JPAQueryFactory queryFactory,
        UsersRepository usersRepository) {
        this.queryFactory = queryFactory;
        this.usersRepository = usersRepository;
    }

    public List<LectureDto> findMyLikedLectures(int userId) {
        QRecordedLecture lecture = QRecordedLecture.recordedLecture;
        QRecordedLectureLike like = QRecordedLectureLike.recordedLectureLike;

        List<LectureDto> lectures = queryFactory
            .select(constructor(LectureDto.class,
                Expressions.as(lecture.id, "recordedId"),
                Expressions.as(lecture.title, "recordTitle"),
                Expressions.as(lecture.thumbnailSmall, "recordThumbnailSmall"),
                Expressions.as(lecture.thumbnail, "recordThumbnail"),
                Expressions.as(JPAExpressions.select(like.id.count())
                    .from(like)
                    .where(like.lecture.eq(lecture)), "likeCount"),
                Expressions.as(Expressions.constant(true), "myLike"),
                Expressions.as(lecture.user.id, "userId")
            ))
            .from(lecture)
            .join(like).on(like.lecture.eq(lecture).and(like.user.id.eq(userId)))
            .orderBy(lecture.createdDate.desc())
            .fetch();

        return setNickname(lectures);
    }

    private List<LectureDto> setNickname(List<LectureDto> lectures) {
        for (LectureDto lecture : lectures) {
            Users teacher = usersRepository.findById(lecture.getUserId())
                .orElseThrow(() -> new RuntimeException("선생님을 찾을 수 없습니다."));
            lecture.setNickname(teacher.getNickname());
        }
        return lectures;
    }
}
