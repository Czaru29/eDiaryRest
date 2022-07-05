package com.ediary.services;

import com.ediary.DTO.*;
import com.ediary.converters.*;
import com.ediary.domain.*;
import com.ediary.domain.Class;
import com.ediary.domain.security.User;
import com.ediary.domain.timetable.Timetable;
import com.ediary.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceImplTest {

    private final Long studentId = 1L;

    private final Long subjectId = 2L;

    private final Long classId = 3L;

    private final Long gradeId = 3L;
    private final String value = "4";

    @Mock
    TimetableService timetableService;
    @Mock
    EventService eventService;

    @Mock
    GradeRepository gradeRepository;
    @Mock
    AttendanceRepository attendanceRepository;
    @Mock
    BehaviorRepository behaviorRepository;
    @Mock
    StudentRepository studentRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    SubjectRepository subjectRepository;
    @Mock
    EndYearReportRepository endYearReportRepository;

    @Mock
    GradeToGradeDto gradeToGradeDto;
    @Mock
    AttendanceToAttendanceDto attendanceToAttendanceDto;
    @Mock
    BehaviorToBehaviorDto behaviorToBehaviorDto;
    @Mock
    EventToEventDto eventToEventDto;
    @Mock
    StudentToStudentDto studentToStudentDto;
    @Mock
    SubjectToSubjectDto subjectToSubjectDto;
    @Mock
    EndYearReportToEndYearReportDto endYearReportToEndYearReportDto;

    StudentService studentService;

    Grade gradeReturned;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        studentService = new StudentServiceImpl(timetableService,eventService, gradeRepository, attendanceRepository,
                behaviorRepository, studentRepository, eventRepository,subjectRepository, endYearReportRepository,
                gradeToGradeDto, attendanceToAttendanceDto, behaviorToBehaviorDto, eventToEventDto, studentToStudentDto, subjectToSubjectDto,
                endYearReportToEndYearReportDto);

        gradeReturned = Grade.builder().id(gradeId).value(value).build();
    }

    @Test
    void listGrades() {
        when(gradeRepository.findAllByStudentId(studentId)).thenReturn(Arrays.asList(gradeReturned));

        when(gradeToGradeDto.convert(gradeReturned)).thenReturn(GradeDto.builder().id(gradeReturned.getId()).build());

        List<GradeDto> grades = studentService.listGrades(studentId);

        assertEquals(1, grades.size());
        assertEquals(gradeId, grades.get(0).getId());
        verify(gradeRepository, times(1)).findAllByStudentId(anyLong());
        verify(gradeToGradeDto, times(1)).convert(gradeReturned);
    }


    @Test
    void listSubjects(){
        Student student = Student.builder().id(studentId).build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(subjectRepository.findAllBySchoolClass_Students(student)).thenReturn(Arrays.asList(
                Subject.builder().id(1L).build(),
                Subject.builder().id(2L).build()
        ));

        when(subjectToSubjectDto.convert(any(Subject.class))).thenReturn(SubjectDto.builder().id(1L).build());

        List<SubjectDto> subjects = studentService.listSubjects(studentId);

        assertEquals(2, subjects.size());
        assertEquals(1L, subjects.get(0).getId());
        verify(studentRepository, times(1)).findById(studentId);
        verify(subjectRepository, times(1)).findAllBySchoolClass_Students(student);
        verify(subjectToSubjectDto, times(2)).convert(any(Subject.class));
    }

    @Test
    void listGradesBySubject() {
        when(gradeRepository.findAllByStudentIdAndSubjectId(studentId, subjectId)).thenReturn(Arrays.asList(gradeReturned));

        when(gradeToGradeDto.convert(gradeReturned)).thenReturn(GradeDto.builder().id(gradeReturned.getId()).build());

        List<GradeDto> grades = studentService.listGrades(studentId, subjectId);

        assertEquals(1, grades.size());
        assertEquals(gradeId, grades.get(0).getId());
        verify(gradeRepository, times(1)).findAllByStudentIdAndSubjectId(anyLong(), anyLong());
        verify(gradeToGradeDto, times(1)).convert(gradeReturned);
    }

    @Test
    void listAttendances() {
        when(attendanceRepository.findAllByStudentId(studentId)).thenReturn(Arrays.asList(
                Attendance.builder().id(1L).build(),
                Attendance.builder().id(2L).build()
        ));

        when(attendanceToAttendanceDto.convert(any())).thenReturn(AttendanceDto.builder().id(5L).build());

        List<AttendanceDto> attendances = studentService.listAttendances(studentId);

        assertEquals(2, attendances.size());
        assertEquals(5L, attendances.get(1).getId());
        verify(attendanceRepository, times(1)).findAllByStudentId(anyLong());
        verify(attendanceToAttendanceDto, times(2)).convert(any());
    }

    @Test
    void listBehaviors() {

        when(behaviorRepository.findAllByStudentIdOrderByDateDesc(any(), any())).thenReturn(Arrays.asList(
                Behavior.builder().id(1L).build(),
                Behavior.builder().id(2L).build()
        ));

        when(behaviorToBehaviorDto.convert(any())).thenReturn(BehaviorDto.builder().id(3L).build());

        List<BehaviorDto> behaviors = studentService.listBehaviors(studentId, 0, 2);

        assertEquals(2, behaviors.size());
        assertEquals(3L, behaviors.get(0).getId());
        verify(behaviorRepository, times(1)).findAllByStudentIdOrderByDateDesc(any(), any());
        verify(behaviorToBehaviorDto, times(2)).convert(any());
    }

    @Test
    void listEvents() {

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(
                Student.builder().id(studentId).build()
                ));

        List<Event> events = new ArrayList<>() {{
            add(Event.builder().build());
        }};

        Page<Event> page = new PageImpl<>(events);

        when(eventRepository.findAllBySchoolClassIdAndDateAfter(any(), any(), any())).thenReturn(page);
        when(eventToEventDto.convert(any())).thenReturn(EventDto.builder().id(3L).build());
        when(studentRepository.findById(any()))
                .thenReturn(Optional.ofNullable(Student.builder().schoolClass(Class.builder().id(1L).build()).build()));

        List<EventDto> gotEvents = studentService.listEvents(studentId, 0, 1, false);

        assertEquals(1, gotEvents.size());
        verify(studentRepository, times(1)).findById(studentId);
        verify(eventRepository, times(1)).findAllBySchoolClassIdAndDateAfter(any(), any(), any());
        verify(eventToEventDto, times(1)).convert(any());
    }

    @Test
    void getTimetableByStudentId() {

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(
                Student.builder().id(studentId).schoolClass(
                        Class.builder().id(classId).build())
                        .build()
        ));

        when(timetableService.getTimetableByClassId(classId)).thenReturn(Timetable.builder().build());

        when(timetableService.getTimetableByClassId(studentId)).thenReturn(Timetable.builder().build());

        Timetable timetable = studentService.getTimetableByStudentId(studentId);

        assertNotNull(timetable);
        verify(studentRepository, times(1)).findById(studentId);
        verify(timetableService, times(1)).getTimetableByClassId(classId);

    }

    @Test
    void findByUser() {
        Long userId = 24L;
        User user = User.builder().id(userId).build();

        Student studentReturned = Student.builder().id(studentId).build();

        when(studentRepository.findByUser(user)).thenReturn(Optional.of(studentReturned));
        when(studentToStudentDto.convert(studentReturned)).thenReturn(StudentDto.builder().id(studentReturned.getId()).build());

        StudentDto student = studentService.findByUser(user);

        assertEquals(student.getId(), studentReturned.getId());
        verify(studentRepository, times(1)).findByUser(user);
        verify(studentToStudentDto, times(1)).convert(studentReturned);
    }
}