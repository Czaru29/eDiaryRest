package com.ediary.services;

import com.ediary.DTO.AttendanceDto;
import com.ediary.converters.AttendanceToAttendanceDto;
import com.ediary.domain.Attendance;
import com.ediary.domain.Student;
import com.ediary.domain.helpers.WeeklyAttendances;
import com.ediary.repositories.AttendanceRepository;
import com.ediary.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WeeklyAttendancesServiceImpl implements WeeklyAttendancesService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final AttendanceToAttendanceDto attendanceToAttendanceDto;

    @Override
    public WeeklyAttendances getAttendancesByWeek(Long studentId, Integer daysNumber, Date date) {

        Student student = studentRepository.findById(studentId).orElse(null);

        if (student == null || student.getSchoolClass() == null) {
            return null;
        }

        LocalDate localDate = date.toLocalDate();

        Map<Date, Integer> attendancesNumber = new HashMap<>();

        Map<Date, List<AttendanceDto>> attendancesByDays = new TreeMap<>();

        java.util.Date startOfDayDate;
        java.util.Date endOfDayDate;

        for (int i = 0; i < 7; i++) {
            startOfDayDate = new java.util.Date(Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.MIDNIGHT)).getTime());
            endOfDayDate = new java.util.Date(Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.MAX)).getTime());

            List<Attendance> attendanceList = attendanceRepository.findAllByStudentIdAndLesson_DateBetween(studentId,
                    startOfDayDate, endOfDayDate);

            attendancesByDays.put(Date.valueOf(localDate), attendanceList.stream()
                    .map(attendanceToAttendanceDto::convert)
                    .collect(Collectors.toList()));

            attendancesNumber.put(Date.valueOf(localDate), (int) attendanceList.stream()
                        .filter((a) -> a.getStatus().equals(Attendance.Status.ABSENT))
                        .count());

            localDate = localDate.plusDays(1);
        }

        return WeeklyAttendances.builder().attendances(attendancesByDays).attendancesNumber(attendancesNumber).build();
    }
}
