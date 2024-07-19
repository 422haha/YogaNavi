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
        lecture.setTitle(lectureDto.getRecord_title());
        lecture.setContent(lectureDto.getRecord_content());
        lecture.setThumbnail(lectureDto.getRecord_thumbnail());

        List<RecordedLectureChapter> chapters = new ArrayList<>();
        for (ChapterDto chapterDto : lectureDto.getRecordedLectureChapter()) {
            RecordedLectureChapter chapter = new RecordedLectureChapter();
            chapter.setTitle(chapterDto.getChapter_title());
            chapter.setDescription(chapterDto.getChapter_discription());
            chapter.setThumbnail(chapterDto.getThumbnailUrl());
            chapter.setVideoUrl(chapterDto.getVideoUrl());
            chapter.setLecture(lecture);
            chapters.add(chapter);
        }
        lecture.setChapters(chapters);

        RecordedLecture savedLecture = recordedLectureRepository.save(lecture);
        return convertToDto(savedLecture);
    }

    @Override
    public LectureDto getLectureDetails(Long recorded_id, String email) {
        RecordedLecture lecture = recordedLectureRepository.findById(recorded_id)
            .orElseThrow(() -> new RuntimeException("Lecture not found"));

        return convertToDto(lecture);
    }

    private LectureDto convertToDto(RecordedLecture lecture) {
        LectureDto dto = new LectureDto();
        dto.setEmail(lecture.getEmail());
        dto.setRecord_title(lecture.getTitle());
        dto.setRecord_content(lecture.getContent());
        dto.setRecord_thumbnail(lecture.getThumbnail());
        dto.setLike_count(0); // 새로 생성된 강의이므로 좋아요 수는 0
        dto.setMy_like(false);

        List<ChapterDto> chapterDtos = new ArrayList<>();
        for (RecordedLectureChapter chapter : lecture.getChapters()) {
            ChapterDto chapterDto = new ChapterDto();
            chapterDto.setChapter_title(chapter.getTitle());
            chapterDto.setChapter_discription(chapter.getDescription());
            chapterDto.setThumbnailUrl(chapter.getThumbnail());
            chapterDto.setVideoUrl(chapter.getVideoUrl());
            chapterDtos.add(chapterDto);
        }
        dto.setRecordedLectureChapter(chapterDtos);

        return dto;
    }

//    private LectureDto convertToDtoLectureDetails(RecordedLecture lecture) {
//        LectureDto dto = new LectureDto();
//        dto.setRecorded_id(String.valueOf(lecture.getId()));
//        dto.setEmail(lecture.getEmail());
//        dto.setRecord_title(lecture.getTitle());
//        dto.setRecord_content(lecture.getContent());
//        dto.setRecord_thumbnail(lecture.getThumbnail());
//
//        List<ChapterDto> chapterDtos = new ArrayList<>();
//        for (RecordedLectureChapter chapter : lecture.getChapters()) {
//            ChapterDto chapterDto = new ChapterDto();
//            chapterDto.setChapter_title(chapter.getTitle());
//            chapterDto.setChapter_discription(chapter.getDescription());
//            chapterDto.setThumbnailUrl(chapter.getThumbnail());
//            chapterDto.setVideoUrl(chapter.getVideoUrl());
//            chapterDto.setChapter_number(
//                chapter.getId().intValue());
//            chapterDtos.add(chapterDto);
//        }
//        dto.setRecordedLectureChapter(chapterDtos);
//
//        return dto;
//    }
}

