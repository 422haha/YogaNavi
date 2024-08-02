package com.yoga.backend.teacher.service;

import com.yoga.backend.common.entity.Reservation;
import com.yoga.backend.common.entity.Users;
import com.yoga.backend.common.entity.LiveLectures;
import com.yoga.backend.common.entity.ReservationDate;
import com.yoga.backend.teacher.dto.ReservationRequestDto;
import com.yoga.backend.mypage.livelectures.dto.LiveLectureDto;
import com.yoga.backend.mypage.livelectures.LiveLectureRepository;
import com.yoga.backend.teacher.repository.ReservationRepository;
import com.yoga.backend.members.repository.UsersRepository;
import com.yoga.backend.teacher.repository.ReservationDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UsersRepository usersRepository;
    private final LiveLectureRepository liveLectureRepository;
    private final ReservationDateRepository reservationDateRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository,
        UsersRepository usersRepository,
        LiveLectureRepository liveLectureRepository,
        ReservationDateRepository reservationDateRepository) {
        this.reservationRepository = reservationRepository;
        this.usersRepository = usersRepository;
        this.liveLectureRepository = liveLectureRepository;
        this.reservationDateRepository = reservationDateRepository;
    }

    @Override
    @Transactional
    public Reservation createReservation(int userId, ReservationRequestDto reservationRequest) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LiveLectures liveLecture = liveLectureRepository.findById((long) reservationRequest.getLiveLectureId())
            .orElseThrow(() -> new RuntimeException("실시간 강의를 찾을 수 없습니다."));

        List<Instant> reservationDates = getReservationDatesWithinRange((long) reservationRequest.getLiveLectureId(),
            Instant.ofEpochMilli(reservationRequest.getStartDate()),
            Instant.ofEpochMilli(reservationRequest.getEndDate()));

        for (Instant reservationDate : reservationDates) {
            ReservationDate date = new ReservationDate();
            date.setDate(reservationDate);
            reservationDateRepository.save(date); // ReservationDate를 먼저 저장

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setLiveLecture(liveLecture);
            reservation.setReservationDate(date);

            reservationRepository.save(reservation);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getUserReservations(int userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getLiveLectureReservations(int liveLectureId) {
        return reservationRepository.findByLiveLecture_LiveId(liveLectureId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiveLectureDto> getAllLiveLectures(int method) {
        if (method == 0) {
            return liveLectureRepository.findAllByMaxLiveNum(1).stream()
                .map(LiveLectureDto::fromEntity)
                .collect(Collectors.toList());
        } else {
            return liveLectureRepository.findAllByMaxLiveNumGreaterThan(1).stream()
                .map(LiveLectureDto::fromEntity)
                .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LiveLectureDto> getLiveLecturesByTeacherAndMethod(int teacherId, int method) {
        if (method == 0) {
            return liveLectureRepository.findByUserIdAndMaxLiveNum(teacherId, 1).stream()
                .map(LiveLectureDto::fromEntity)
                .collect(Collectors.toList());
        } else {
            return liveLectureRepository.findByUserIdAndMaxLiveNumGreaterThan(teacherId, 1).stream()
                .map(LiveLectureDto::fromEntity)
                .collect(Collectors.toList());
        }
    }

    @Override
    public List<Reservation> getReservationsByTeacher(int teacherId) {
        return reservationRepository.findByLiveLecture_UserId(teacherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Instant> getReservationDatesWithinRange(Long liveId, Instant startDate, Instant endDate) {
        LiveLectures liveLecture = liveLectureRepository.findById(liveId)
            .orElseThrow(() -> new RuntimeException("실시간 강의를 찾을 수 없습니다."));

        List<Instant> reservationDates = new ArrayList<>();
        Instant current = startDate;
        ZoneId zoneId = ZoneId.systemDefault();
        DayOfWeek[] availableDays = parseAvailableDays(liveLecture.getAvailableDay());

        while (!current.isAfter(endDate)) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(current, zoneId);
            DayOfWeek currentDayOfWeek = localDateTime.getDayOfWeek();

            for (DayOfWeek day : availableDays) {
                if (currentDayOfWeek.equals(day)) {
                    reservationDates.add(current);
                }
            }
            current = current.plus(1, ChronoUnit.DAYS);
        }
        return reservationDates;
    }

    private DayOfWeek[] parseAvailableDays(String availableDay) {
        String[] days = availableDay.split(",");
        DayOfWeek[] dayOfWeeks = new DayOfWeek[days.length];
        for (int i = 0; i < days.length; i++) {
            dayOfWeeks[i] = convertToDayOfWeek(days[i].trim().toUpperCase());
        }
        return dayOfWeeks;
    }

    private DayOfWeek convertToDayOfWeek(String day) {
        switch (day) {
            case "MON":
                return DayOfWeek.MONDAY;
            case "TUE":
                return DayOfWeek.TUESDAY;
            case "WED":
                return DayOfWeek.WEDNESDAY;
            case "THU":
                return DayOfWeek.THURSDAY;
            case "FRI":
                return DayOfWeek.FRIDAY;
            case "SAT":
                return DayOfWeek.SATURDAY;
            case "SUN":
                return DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("Invalid day of week: " + day);
        }
    }
}
