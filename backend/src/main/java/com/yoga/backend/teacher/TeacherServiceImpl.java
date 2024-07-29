package com.yoga.backend.teacher;

import com.yoga.backend.common.entity.Users;
import com.yoga.backend.teacher.dto.DetailedTeacherDto;
import com.yoga.backend.teacher.dto.TeacherDto;
import com.yoga.backend.teacher.dto.DetailedTeacherDto.LectureDto;
import com.yoga.backend.teacher.dto.DetailedTeacherDto.NoticeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 강사 서비스 구현 클래스
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Override
    public List<TeacherDto> getAllTeachers() {
        List<Users> teachers = teacherRepository.findAll();
        return teachers.stream()
            .filter(teacher -> "TEACHER".equals(teacher.getRole()))
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    public DetailedTeacherDto getTeacherById(int id) {
        Users teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));

        List<LectureDto> recordedLectures = teacher.getRecordedLectures().stream()
            .map(lecture -> new LectureDto(
                String.valueOf(lecture.getId()),
                lecture.getTitle(),
                lecture.getContent(),
                (int) lecture.getLikeCount(),
                false // 필요에 따라 사용자에 의한 좋아요 여부를 설정
            )).collect(Collectors.toList());

        List<NoticeDto> notices = teacher.getArticles().stream()
            .sorted(Comparator.comparing(notice -> notice.getArticleId(), Comparator.reverseOrder()))
            .map(notice -> new NoticeDto(
                String.valueOf(notice.getArticleId()),
                "Notice Title", // 필요에 따라 공지 제목 설정
                notice.getContent()
            )).collect(Collectors.toList());

        return new DetailedTeacherDto(
            teacher.getId(),
            teacher.getEmail(),
            teacher.getNickname(),
            teacher.getProfile_image_url(),
            teacher.getContent(),
            teacher.getHashtags().stream().map(hashtag -> hashtag.getName()).collect(Collectors.toSet()),
            recordedLectures,
            notices
        );
    }

    private TeacherDto convertToDto(Users teacher) {
        return new TeacherDto(
            teacher.getId(),
            teacher.getEmail(),
            teacher.getNickname(),
            teacher.getProfile_image_url(),
            teacher.getContent(),
            teacher.getHashtags().stream().map(hashtag -> hashtag.getName()).collect(Collectors.toSet()),
            false,  // 필요에 따라 좋아요 여부를 설정
            0       // 필요에 따라 좋아요 수를 설정
        );
    }
}
