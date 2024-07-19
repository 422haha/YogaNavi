package com.yoga.backend.mypage.recorded;

import com.yoga.backend.common.entity.RecordedLectures.RecordedLecture;
import com.yoga.backend.common.entity.RecordedLectures.RecordedLectureChapter;
import com.yoga.backend.mypage.recorded.dto.ChapterDto;
import com.yoga.backend.mypage.recorded.dto.LectureDto;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureLikeRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureListRepository;
import com.yoga.backend.mypage.recorded.repository.RecordedLectureRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordedServiceImpl implements RecordedService {

    @Autowired
    private RecordedLectureListRepository recordedLectureListRepository;

    @Autowired
    private RecordedLectureRepository recordedLectureRepository;

    @Autowired
    private RecordedLectureLikeRepository likeRepository;

    @Override
    public List<LectureDto> getMyLectures(String email) {
        return recordedLectureListRepository.findAllLectures(email);
    }

    @Override
    public LectureDto saveLecture(LectureDto lectureDto) {
        RecordedLecture lecture = new RecordedLecture();
        lecture.setEmail(lectureDto.getEmail());
        lecture.setTitle(lectureDto.getRecordTitle());
        lecture.setContent(lectureDto.getRecordContent());
        lecture.setThumbnail(lectureDto.getRecordThumbnail());

        List<RecordedLectureChapter> chapters = new ArrayList<>();
        int chapterNum = 1;
        for (ChapterDto chapterDto : lectureDto.getRecordedLectureChapters()) {
            RecordedLectureChapter chapter = new RecordedLectureChapter();
            chapter.setTitle(chapterDto.getChapterTitle());
            chapter.setDescription(chapterDto.getChapterDescription());
            chapter.setThumbnail(chapterDto.getThumbnailUrl());
            chapter.setVideoUrl(chapterDto.getVideoUrl());
            chapter.setLecture(lecture);
            chapter.setChapterNumber(chapterNum++);
            chapters.add(chapter);
        }
        lecture.setChapters(chapters);

        RecordedLecture savedLecture = recordedLectureRepository.save(lecture);
        return convertToDto(savedLecture);
    }

    @Override
    public LectureDto getLectureDetails(Long recordedId, String email) {
        RecordedLecture lecture = recordedLectureRepository.findById(recordedId)
            .orElseThrow(() -> new RuntimeException("Lecture not found"));

        return convertToDto(lecture);
    }

    private LectureDto convertToDto(RecordedLecture lecture) {
        LectureDto dto = new LectureDto();
        dto.setRecordedId(lecture.getId());
        dto.setEmail(lecture.getEmail());
        dto.setRecordTitle(lecture.getTitle());
        dto.setRecordContent(lecture.getContent());
        dto.setRecordThumbnail(lecture.getThumbnail());
        dto.setLikeCount(0); // 새로 생성된 강의이므로 좋아요 수는 0
        dto.setMyLike(false);

        List<ChapterDto> chapterDtos = new ArrayList<>();
        for (RecordedLectureChapter chapter : lecture.getChapters()) {
            ChapterDto chapterDto = new ChapterDto();
            chapterDto.setChapterTitle(chapter.getTitle());
            chapterDto.setChapterDescription(chapter.getDescription());
            chapterDto.setThumbnailUrl(chapter.getThumbnail());
            chapterDto.setVideoUrl(chapter.getVideoUrl());
            chapterDto.setChapterNumber(chapter.getChapterNumber());
            chapterDtos.add(chapterDto);
        }
        dto.setRecordedLectureChapters(chapterDtos);

        return dto;
    }
}