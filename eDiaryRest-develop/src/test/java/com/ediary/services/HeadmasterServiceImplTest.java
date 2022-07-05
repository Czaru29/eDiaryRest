package com.ediary.services;


import com.ediary.DTO.TeacherDto;
import com.ediary.converters.EndYearReportToEndYearReportDto;
import com.ediary.converters.TeacherToTeacherDto;
import com.ediary.domain.Teacher;
import com.ediary.domain.helpers.TimeInterval;
import com.ediary.domain.security.User;
import com.ediary.repositories.*;
import com.ediary.services.pdf.PdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class HeadmasterServiceImplTest {

    @Mock TeacherRepository teacherRepository;
    @Mock LessonRepository lessonRepository;
    @Mock GradeRepository gradeRepository;
    @Mock EventRepository eventRepository;
    @Mock ClassRepository classRepository;
    @Mock StudentRepository studentRepository;
    @Mock SubjectRepository subjectRepository;
    @Mock ExtenuationRepository extenuationRepository;
    @Mock EndYearReportRepository endYearReportRepository;
    @Mock SchoolPeriodRepository schoolPeriodRepository;
    @Mock StudentCouncilRepository studentCouncilRepository;
    @Mock ParentCouncilRepository parentCouncilRepository;
    @Mock TopicRepository topicRepository;
    @Mock ParentRepository parentRepository;
    @Mock AttendanceRepository attendanceRepository;
    @Mock BehaviorRepository behaviorRepository;
    @Mock NoticeRepository noticeRepository;


    @Mock
    TeacherToTeacherDto teacherToTeacherDto;
    @Mock
    EndYearReportToEndYearReportDto endYearReportToEndYearReportDto;
    @Mock
    PdfService pdfService;


    HeadmasterService headmasterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        headmasterService = new HeadmasterServiceImpl(teacherRepository, lessonRepository, gradeRepository, eventRepository,
                classRepository, studentRepository, subjectRepository, extenuationRepository, endYearReportRepository,
                schoolPeriodRepository, studentCouncilRepository, parentCouncilRepository, topicRepository, parentRepository,
                attendanceRepository, behaviorRepository, noticeRepository, teacherToTeacherDto,endYearReportToEndYearReportDto,
                pdfService);
    }

    @Test
    void listAllTeachers() {
        Long teacherId = 1L;

        Teacher teacher = Teacher.builder()
                .user(User.builder().firstName("Adam").build())
                .id(teacherId)
                .build();

        TeacherDto teacherDto = TeacherDto.builder()
                .id(teacherId)
                .build();


        when(teacherRepository.findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "User.lastName"))))
                .thenReturn(new PageImpl<>(Collections.singletonList(teacher)));
        when(teacherToTeacherDto.convert(any())).thenReturn(teacherDto);

        List<TeacherDto> teacherDtos = headmasterService.listAllTeachers(0, 1);

        assertNotNull(teacherDtos);
        verify(teacherRepository, times(1))
                .findAll(PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "User.lastName")));
    }


    @Test
    void initNewTimeInterval() {

        TimeInterval timeInterval = headmasterService.initNewTimeInterval();

        assertNotNull(timeInterval);
    }

    @Test
    void setTimeInterval() {
        LocalDate startTime = LocalDate.now();
        LocalDate endTime = LocalDate.now().plusDays(1);


        TimeInterval timeInterval = headmasterService.setTimeInterval(startTime, endTime);

        assertNotNull(timeInterval);
        assertEquals(Date.valueOf(startTime), timeInterval.getStartTime());
        assertEquals(Date.valueOf(endTime), timeInterval.getEndTime());

    }


    @Test
    void createTeacherReport() throws Exception {
        //just it for now

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(Teacher.builder().build()));

        Boolean result = headmasterService.createTeacherReport(null, 1L,
                Date.valueOf(LocalDate.now()), Date.valueOf(LocalDate.now()));

        assertFalse(result);
    }

}
