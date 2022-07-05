package com.ediary.services;

import com.ediary.DTO.AttendanceDto;
import com.ediary.converters.AttendanceToAttendanceDto;
import com.ediary.domain.Attendance;
import com.ediary.domain.Class;
import com.ediary.domain.Event;
import com.ediary.domain.Student;
import com.ediary.domain.helpers.WeeklyAttendances;
import com.ediary.repositories.AttendanceRepository;
import com.ediary.repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


public class WeeklyAttendancesServiceImplTest {

    @Mock
    AttendanceRepository attendanceRepository;
    @Mock
    StudentRepository studentRepository;
    @Mock
    AttendanceToAttendanceDto attendanceToAttendanceDto;

    WeeklyAttendancesService weeklyAttendancesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        weeklyAttendancesService = new WeeklyAttendancesServiceImpl(attendanceRepository, studentRepository, attendanceToAttendanceDto);
    }

    @Test
    void getAttendanceByWeek() {
        when(attendanceRepository.findAllByStudentIdAndLesson_Date(any(),any())).thenReturn(Arrays.asList(
                Attendance.builder().id(1L).status(Attendance.Status.ABSENT).build(),
                Attendance.builder().id(2L).status(Attendance.Status.ABSENT).build()
        ));

        when(attendanceToAttendanceDto.convert(any())).thenReturn(AttendanceDto.builder().id(1L).build());
        when(studentRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(Student.builder().schoolClass(Class.builder().build()).build()));

        WeeklyAttendances weeklyAttendances = weeklyAttendancesService.getAttendancesByWeek(1L, 7, Date.valueOf(LocalDate.now()));

        assertNotNull(weeklyAttendances);
        assertEquals(7, weeklyAttendances.getAttendances().keySet().size());
    }
}
