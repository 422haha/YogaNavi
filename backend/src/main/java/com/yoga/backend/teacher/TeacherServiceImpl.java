package com.yoga.backend.teacher;

import com.yoga.backend.common.awsS3.S3Service;
import com.yoga.backend.common.entity.Hashtag;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.teacher.dto.DetailedTeacherDto;
import com.yoga.backend.teacher.dto.TeacherDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 강사 서비스 구현 클래스
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    private static final long URL_EXPIRATION_SECONDS = 86400; // 24시간

    private final TeacherRepository teacherRepository;
    private final S3Service s3Service;

    @Autowired
    public TeacherServiceImpl(TeacherRepository teacherRepository, S3Service s3Service) {
        this.teacherRepository = teacherRepository;
        this.s3Service = s3Service;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherDto> getAllTeachers() {
        List<Users> users = teacherRepository.findAllTeachers();
        return users.stream()
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

                return TeacherDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImageUrl(profileImageUrl)
                    .profileImageUrlSmall(profileImageUrlSmall)
                    .content(user.getContent())
                    .hashtags(user.getHashtags().stream().map(Hashtag::getName)
                        .collect(Collectors.toSet()))
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DetailedTeacherDto getTeacherById(int teacherId) {
        Users user = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("강사를 찾을 수 없습니다."));

        String profileImageUrl = null;
        String profileImageUrlSmall = null;
        try {
            if (user.getProfile_image_url() != null) {
                profileImageUrl = s3Service.generatePresignedUrl(user.getProfile_image_url(),
                    URL_EXPIRATION_SECONDS);
            }
            if (user.getProfile_image_url_small() != null) {
                profileImageUrlSmall = s3Service.generatePresignedUrl(
                    user.getProfile_image_url_small(), URL_EXPIRATION_SECONDS);
            }
        } catch (Exception e) {
            System.err.println("Presigned URL 생성 오류: " + e.getMessage());
        }

        DetailedTeacherDto detailedTeacher = DetailedTeacherDto.builder()
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
                    .likedByUser(
                        false) // This is a placeholder, you should implement the actual logic
                    .build()
            ).collect(Collectors.toList()))
            .notices(user.getArticles().stream().map(article -> {
                String noticeImage = null;
                String noticeImageSmall = null;
                try {
                    if (article.getImageUrl() != null) {
                        noticeImage = s3Service.generatePresignedUrl(article.getImageUrl(),
                            URL_EXPIRATION_SECONDS);
                    }
                    if (article.getImageUrlSmall() != null) {
                        noticeImageSmall = s3Service.generatePresignedUrl(
                            article.getImageUrlSmall(), URL_EXPIRATION_SECONDS);
                    }
                } catch (Exception e) {
                    System.err.println("Notice Presigned URL 생성 오류: " + e.getMessage());
                }

                return DetailedTeacherDto.NoticeDto.builder()
                    .noticeId(article.getArticleId().toString())
                    .noticeContent(article.getContent())
                    .noticeImage(noticeImage)
                    .noticeImageSmall(noticeImageSmall)
                    .build();
            }).collect(Collectors.toList()))
            .build();

        return detailedTeacher;
    }
}
