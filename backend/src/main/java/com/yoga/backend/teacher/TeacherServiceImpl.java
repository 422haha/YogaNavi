package com.yoga.backend.teacher;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.Hashtag;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.entity.TeacherLike;
import com.yoga.backend.teacher.dto.DetailedTeacherDto;
import com.yoga.backend.teacher.dto.TeacherDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 강사 서비스 구현 클래스
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    private static final long URL_EXPIRATION_SECONDS = 86400; // 24시간

    private final TeacherRepository teacherRepository;
    private final TeacherLikeRepository teacherLikeRepository;
    private final S3Service s3Service;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository,
        TeacherLikeRepository teacherLikeRepository, S3Service s3Service) {
        this.teacherRepository = teacherRepository;
        this.teacherLikeRepository = teacherLikeRepository;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherDto> getAllTeachers(TeacherFilter filter, int userId) {
        List<Users> users = teacherRepository.findAllTeachers();
        LocalDate now = LocalDate.now();

        return users.stream()
            .filter(user -> {
                // 필터 조건에 맞는 강의가 있는지 확인
                boolean hasValidLecture = user.getLiveLectures().stream().anyMatch(lecture -> {
                    // 시작 시간과 종료 시간 필터링
                    boolean isValidTime = lecture.getStartTime() >= filter.getStartTime() &&
                        lecture.getEndTime() <= filter.getEndTime();

                    // 요일 필터링
                    boolean isValidDay = true;
                    if (!filter.getDay().equals("MON, TUE, WED, THU, FRI, SAT, SUN")) {
                        for (String day : filter.getDay().split(", ")) {
                            if (!lecture.getAvailableDay().contains(day.trim())) {
                                isValidDay = false;
                                break;
                            }
                        }
                    }

                    // 기간 필터링
                    boolean isValidPeriod = switch (filter.getPeriod()) {
                        case 0 -> lecture.getEndDate() > now.plusWeeks(1).toEpochDay(); // 일주일
                        case 1 -> lecture.getEndDate() > now.plusMonths(1).toEpochDay(); // 한달
                        case 2 -> lecture.getEndDate() > now.plusMonths(3).toEpochDay(); // 세달
                        case 3 -> true; // 무기한
                        default -> false;
                    };

                    return isValidTime && isValidDay && isValidPeriod;
                });

                // maxLiveNum 필터링
                boolean isValidMaxLiveNum = filter.getMaxLiveNum() == 1 ||
                    user.getLiveLectures().stream()
                        .anyMatch(lecture -> lecture.getMaxLiveNum() == filter.getMaxLiveNum());

                return hasValidLecture && isValidMaxLiveNum;
            })
            .sorted((user1, user2) -> {
                // 좋아요 수 기준 정렬
                int user1Likes = user1.getTeacherLikes().size();
                int user2Likes = user2.getTeacherLikes().size();
                return Integer.compare(user2Likes, user1Likes); // 내림차순 정렬
            })
            .map(user -> {
                String profileImageUrl = null;
                String profileImageUrlSmall = null;
                try {
                    if (user.getProfile_image_url() != null) {
                        profileImageUrl = s3Service.generatePresignedUrl(
                            user.getProfile_image_url(), URL_EXPIRATION_SECONDS);
                    }
                    if (user.getProfile_image_url_small() != null) {
                        profileImageUrlSmall = s3Service.generatePresignedUrl(
                            user.getProfile_image_url_small(), URL_EXPIRATION_SECONDS);
                    }
                } catch (Exception e) {
                    System.err.println("Presigned URL 생성 오류: " + e.getMessage());
                }

                boolean likedByUser =
                    teacherLikeRepository.findByTeacherAndUser(user, getUserById(userId)) != null;

                return TeacherDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImageUrl(profileImageUrl)
                    .profileImageUrlSmall(profileImageUrlSmall)
                    .content(user.getContent())
                    .hashtags(user.getHashtags().stream().map(Hashtag::getName)
                        .collect(Collectors.toSet()))
                    .liked(likedByUser)
                    .likeCount(user.getTeacherLikes().size())
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DetailedTeacherDto getTeacherById(int teacherId, int userId) {
        Users user = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("강사를 찾을 수 없습니다."));

        String profileImageUrl = null;
        String profileImageUrlSmall = null;
        try {
            if (user.getProfile_image_url() != null) {
                profileImageUrl = s3Service.generatePresignedUrl(user.getProfile_image_url(),
                    URL_EXPIRATION_SECONDS);
                System.out.println("생성된 profileImageUrl: " + profileImageUrl);
            }
            if (user.getProfile_image_url_small() != null) {
                profileImageUrlSmall = s3Service.generatePresignedUrl(
                    user.getProfile_image_url_small(), URL_EXPIRATION_SECONDS);
                System.out.println("생성된 profileImageUrlSmall: " + profileImageUrlSmall);
            }
        } catch (Exception e) {
            System.err.println("Presigned URL 생성 오류: " + e.getMessage());
        }

        boolean likedByUser =
            teacherLikeRepository.findByTeacherAndUser(user, getUserById(userId)) != null;

        return DetailedTeacherDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .profileImageUrl(profileImageUrl)
            .profileImageUrlSmall(profileImageUrlSmall)
            .content(user.getContent())
            .hashtags(user.getHashtags().stream().map(Hashtag::getName).collect(Collectors.toSet()))
            .recordedLectures(user.getRecordedLectures().stream().map(lecture ->
                DetailedTeacherDto.LectureDto.builder()
                    .lectureId(lecture.getId().toString())
                    .lectureTitle(lecture.getTitle())
                    .lectureDescription(lecture.getContent())
                    .likes((int) lecture.getLikeCount())
                    .likedByUser(false)
                    .build()
            ).collect(Collectors.toList()))
            .notices(user.getArticles().stream().map(article -> {
                    String noticeImage = null;
                    String noticeImageSmall = null;
                    try {
                        if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                            noticeImage = s3Service.generatePresignedUrl(article.getImageUrl(),
                                URL_EXPIRATION_SECONDS);
                            System.out.println("생성된 noticeImage: " + noticeImage);
                        }
                        if (article.getImageUrlSmall() != null && !article.getImageUrlSmall()
                            .isEmpty()) {
                            noticeImageSmall = s3Service.generatePresignedUrl(
                                article.getImageUrlSmall(), URL_EXPIRATION_SECONDS);
                            System.out.println("생성된 noticeImageSmall: " + noticeImageSmall);
                        }
                    } catch (Exception e) {
                        System.err.println("공지 Presigned URL 생성 오류: " + e.getMessage());
                    }

                    return DetailedTeacherDto.NoticeDto.builder()
                        .noticeId(article.getArticleId().toString())
                        .noticeContent(article.getContent())
                        .noticeImage(noticeImage)
                        .noticeImageSmall(noticeImageSmall)
                        .build();
                }).sorted(Comparator.comparing(DetailedTeacherDto.NoticeDto::getNoticeId).reversed())
                .collect(Collectors.toList()))
            .likeCount(user.getTeacherLikes().size())
            .liked(likedByUser)
            .build();
    }

    @Override
    @Transactional
    public boolean toggleLike(int teacherId, int userId) {
        Users teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("강사를 찾을 수 없습니다."));
        Users user = teacherRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        TeacherLike existingLike = teacherLikeRepository.findByTeacherAndUser(teacher, user);
        if (existingLike != null) {
            teacherLikeRepository.delete(existingLike);
            return false; // 좋아요 취소
        } else {
            TeacherLike like = new TeacherLike();
            like.setTeacher(teacher);
            like.setUser(user);
            teacherLikeRepository.save(like);
            return true; // 좋아요 추가
        }
    }

    private Users getUserById(int userId) {
        return teacherRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
    }
}
