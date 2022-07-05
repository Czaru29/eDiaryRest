package com.ediary.services;

import com.ediary.DTO.*;
import com.ediary.converters.*;
import com.ediary.domain.*;
import com.ediary.domain.Class;
import com.ediary.domain.helpers.TimeInterval;
import com.ediary.repositories.*;
import com.ediary.services.pdf.PdfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class FormTutorServiceImplTest {

    @Mock
    PdfService pdfService;
    @Mock
    TeacherRepository teacherRepository;
    @Mock
    StudentCouncilRepository studentCouncilRepository;
    @Mock
    StudentRepository studentRepository;
    @Mock
    ParentRepository parentRepository;
    @Mock
    ParentCouncilRepository parentCouncilRepository;
    @Mock
    GradeRepository gradeRepository;
    @Mock
    SubjectRepository subjectRepository;
    @Mock
    ClassRepository classRepository;
    @Mock
    BehaviorRepository behaviorRepository;
    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    StudentCouncilDtoToStudentCouncil studentCouncilDtoToStudentCouncil;
    @Mock
    StudentCouncilToStudentCouncilDto studentCouncilToStudentCouncilDto;
    @Mock
    ParentCouncilToParentCouncilDto parentCouncilToParentCouncilDto;
    @Mock
    ParentCouncilDtoToParentCouncil parentCouncilDtoToParentCouncil;
    @Mock
    StudentToStudentDto studentToStudentDto;
    @Mock
    ParentToParentDto parentToParentDto;
    @Mock
    GradeToGradeDto gradeToGradeDto;
    @Mock
    GradeDtoToGrade gradeDtoToGrade;
    @Mock
    SubjectToSubjectDto subjectToSubjectDto;


    HttpServletResponse mockResponse;

    FormTutorService formTutorService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        formTutorService = new FormTutorServiceImpl(pdfService, teacherRepository, studentCouncilRepository, studentRepository, parentRepository,
                parentCouncilRepository, gradeRepository, subjectRepository, classRepository, behaviorRepository, attendanceRepository,
                studentCouncilDtoToStudentCouncil, studentCouncilToStudentCouncilDto,
                parentCouncilDtoToParentCouncil, parentCouncilToParentCouncilDto, studentToStudentDto, parentToParentDto,
                gradeToGradeDto, gradeDtoToGrade, subjectToSubjectDto);

        mockResponse = mock(HttpServletResponse.class);

    }

    @Test
    void initNewStudentCouncil() {
        Long teacherId = 1L;

        Teacher teacher = Teacher.builder()
                .id(1L)
                .schoolClass(Class.builder().id(1L).build())
                .build();

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));

        StudentCouncilDto studentCouncilDto = formTutorService.initNewStudentCouncil(teacherId);

        assertNotNull(studentCouncilDto);
        verify(teacherRepository, times(1)).findById(teacherId);

    }

    @Test
    void saveStudentCouncil() {
        Long teacherId = 1L;

        Teacher teacher = Teacher.builder().id(1L).build();
        StudentCouncil studentCouncil = StudentCouncil.builder().id(1L).build();
        StudentCouncilDto studentCouncilDto = StudentCouncilDto.builder().id(1L).build();
        ;

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));
        when(studentCouncilDtoToStudentCouncil.convert(any())).thenReturn(studentCouncil);
        when(studentRepository.findAllById(any())).thenReturn(Collections.singletonList(Student.builder().build()));
        when(studentCouncilRepository.save(any())).thenReturn(studentCouncil);

        StudentCouncil savedStudentCouncil = formTutorService
                .saveStudentCouncil(teacherId, studentCouncilDto, new ArrayList<>() {{
                    add(1L);
                    add(2L);
                }});

        assertNull(savedStudentCouncil);
        verify(teacherRepository, times(1)).findById(teacherId);

    }

    @Test
    void findStudentCouncil() {
        Long teacherId = 1L;

        Teacher teacher = Teacher.builder()
                .id(1L)
                .schoolClass(Class.builder()
                        .studentCouncil(StudentCouncil.builder().build())
                        .build())
                .build();
        StudentCouncilDto studentCouncilDto = StudentCouncilDto.builder().id(1L).build();
        ;

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));
        when(studentCouncilToStudentCouncilDto.convert(any())).thenReturn(studentCouncilDto);
        when(studentCouncilRepository.findBySchoolClassId(any())).thenReturn(StudentCouncil.builder().build());

        StudentCouncilDto studentCouncilDto1 = formTutorService.findStudentCouncil(teacherId);

        assertNotNull(studentCouncilDto1);
        verify(teacherRepository, times(1)).findById(teacherId);

    }


    @Test
    void removeStudentFromCouncil() {
        Long teacherId = 1L;
        Long studentId = 1L;

        StudentCouncil studentCouncil = StudentCouncil.builder()
                .id(1L)
                .students(new HashSet<>() {{
                    add(Student.builder().id(studentId).build());
                    add(Student.builder().id(2L).build());
                }})
                .build();

        StudentCouncilDto studentCouncilDto = StudentCouncilDto.builder()
                .id(1L)
                .students(new ArrayList<>() {{
                    add(StudentDto.builder().build());
                }})
                .build();

        when(studentRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(Student.builder().build()));
        when(studentCouncilDtoToStudentCouncil.convert(any())).thenReturn(studentCouncil);
        when(studentCouncilToStudentCouncilDto.convert(any())).thenReturn(studentCouncilDto);
        when(studentCouncilRepository.save(any())).thenReturn(studentCouncil);
        when(teacherRepository.findById(any())).thenReturn(Optional.ofNullable(Teacher.builder().schoolClass(Class.builder().build()).build()));

        StudentCouncilDto removedStudentCouncilDto = formTutorService
                .removeStudentFromCouncil(studentCouncilDto, 1L, studentId);


        assertNotNull(removedStudentCouncilDto);
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    void initNewParentCouncil() {
        Long teacherId = 1L;

        Teacher teacher = Teacher.builder()
                .id(1L)
                .schoolClass(Class.builder().id(1L).build())
                .build();

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));

        ParentCouncilDto parentCouncilDto = formTutorService.initNewParentCouncil(teacherId);

        assertNotNull(parentCouncilDto);
        verify(teacherRepository, times(1)).findById(teacherId);

    }


    @Test
    void saveParentCouncil() {
        Long teacherId = 1L;

        Teacher teacher = Teacher.builder().id(1L).build();
        ParentCouncil parentCouncil = ParentCouncil.builder().id(1L).build();
        ParentCouncilDto parentCouncilDto = ParentCouncilDto.builder().id(1L).build();
        ;

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));
        when(parentCouncilDtoToParentCouncil.convert(any())).thenReturn(parentCouncil);
        when(parentRepository.findAllById(any())).thenReturn(Collections.singletonList(Parent.builder().build()));
        when(parentCouncilRepository.save(any())).thenReturn(parentCouncil);

        ParentCouncil savedParentCouncil = formTutorService
                .saveParentCouncil(teacherId, parentCouncilDto, new ArrayList<>() {{
                    add(1L);
                    add(2L);
                }});

        assertNull(savedParentCouncil);
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    void findParentCouncil() {
        Long teacherId = 1L;

        Teacher teacher = Teacher.builder()
                .id(1L)
                .schoolClass(Class.builder()
                        .parentCouncil(ParentCouncil.builder().build())
                        .build())
                .build();
        ParentCouncilDto parentCouncilDto = ParentCouncilDto.builder().id(1L).build();
        ;

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));
        when(parentCouncilToParentCouncilDto.convert(any())).thenReturn(parentCouncilDto);
        when(parentCouncilRepository.findBySchoolClassId(any())).thenReturn(ParentCouncil.builder().build());

        ParentCouncilDto parentCouncilDto1 = formTutorService.findParentCouncil(teacherId);

        assertNotNull(parentCouncilDto1);
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    void removeParentFromCouncil() {
        Long teacherId = 1L;
        Long parentId = 1L;

        ParentCouncil parentCouncil = ParentCouncil.builder()
                .id(1L)
                .parents(new HashSet<>() {{
                    add(Parent.builder().id(parentId).build());
                    add(Parent.builder().id(2L).build());
                }})
                .build();

        ParentCouncilDto parentCouncilDto = ParentCouncilDto.builder()
                .id(1L)
                .parents(new ArrayList<>() {{
                    add(ParentDto.builder().build());
                }})
                .build();

        when(parentRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(Parent.builder().build()));
        when(parentCouncilDtoToParentCouncil.convert(any())).thenReturn(parentCouncil);
        when(parentCouncilToParentCouncilDto.convert(any())).thenReturn(parentCouncilDto);
        when(parentCouncilRepository.save(any())).thenReturn(parentCouncil);
        when(teacherRepository.findById(any())).thenReturn(Optional.ofNullable(Teacher.builder().schoolClass(Class.builder().build()).build()));


        ParentCouncilDto removedParentCouncilDto = formTutorService
                .removeParentFromCouncil(parentCouncilDto, 1L, parentId);


        assertNotNull(removedParentCouncilDto);
        verify(teacherRepository, times(1)).findById(teacherId);
    }


    @Test
    void listBehaviorGrades() {
        Long teacherId = 1L;
        Long schoolClassId = 1L;

        Teacher teacher = Teacher.builder()
                .id(teacherId)
                .build();

        Grade grade = Grade.builder().build();

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));
        when(gradeToGradeDto.convert(any())).thenReturn(GradeDto.builder().build());
        when(gradeRepository.findAllByTeacherIdAndWeight(any(), any())).thenReturn(Collections.singletonList(grade));


        Map<StudentDto, GradeDto> gradeDtoList = formTutorService.listBehaviorGrades(teacherId);

        assertNull(gradeDtoList);
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    void saveBehaviorGrade() {
        Long teacherId = 1L;

        Teacher teacher = Teacher.builder().id(1L).build();
        Grade grade = Grade.builder().id(1L).build();
        GradeDto gradeDto = GradeDto.builder().id(1L).build();
        ;

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));
        when(gradeToGradeDto.convert(any())).thenReturn(gradeDto);
        when(gradeDtoToGrade.convert(any())).thenReturn(grade);
        when(gradeRepository.save(any())).thenReturn(grade);

        Grade savedGrade = formTutorService.saveBehaviorGrade(teacherId, gradeDto);

        assertNotNull(savedGrade);
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(gradeDtoToGrade, times(1)).convert(gradeDto);
        verify(gradeToGradeDto, times(0)).convert(any());
    }

    @Test
    void listClassStudents() {
        Long teacherId = 1L;
        Long schoolClassId = 1L;

        Teacher teacher = Teacher.builder()
                .id(teacherId)
                .schoolClass(Class.builder().id(schoolClassId).build())
                .build();

        Student student = Student.builder().build();

        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));
        when(studentToStudentDto.convert(any())).thenReturn(StudentDto.builder().build());
        when(studentRepository.findAllBySchoolClassId(any())).thenReturn(Collections.singletonList(student));


        List<StudentDto> studentDtoList = formTutorService.listClassStudentsStudentCouncil(teacherId);

        assertNotNull(studentDtoList);
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    void listClassParents() {
        Long teacherId = 1L;
        Long schoolClassId = 1L;

        Teacher teacher = Teacher.builder()
                .id(teacherId)
                .schoolClass(Class.builder().id(schoolClassId).build())
                .build();


        when(teacherRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(teacher));
        when(parentToParentDto.convert(any())).thenReturn(ParentDto.builder().build());
        when(studentRepository.findAllBySchoolClassId(any())).thenReturn(Collections.singletonList(Student.builder().build()));


        List<ParentDto> parentDtoList = formTutorService.listClassParentsParentCouncil(teacherId);

        assertNotNull(parentDtoList);
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    void initNewTimeInterval() {
        TimeInterval timeInterval = formTutorService.initNewTimeInterval();

        assertNotNull(timeInterval);
        assertNotNull(timeInterval.getStartTime());
        assertNotNull(timeInterval.getEndTime());
        assertEquals(timeInterval.getEndTime().toLocalDate(), timeInterval.getStartTime().toLocalDate().plusYears(1));
    }

    @Test
    void setTimeInterval() {
        LocalDate startTime = LocalDate.now();
        LocalDate endTime = LocalDate.now().plusDays(5);

        TimeInterval timeInterval = formTutorService.setTimeInterval(startTime, endTime);

        assertNotNull(timeInterval);
        assertEquals(timeInterval.getStartTime().toLocalDate(), startTime);
        assertEquals(timeInterval.getEndTime().toLocalDate(), endTime);
    }

}
