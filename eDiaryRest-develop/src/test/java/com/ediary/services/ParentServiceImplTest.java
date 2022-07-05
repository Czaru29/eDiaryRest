package com.ediary.services;

import com.ediary.DTO.*;
import com.ediary.converters.*;
import com.ediary.domain.*;
import com.ediary.domain.Class;
import com.ediary.domain.security.User;
import com.ediary.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParentServiceImplTest {

    private final Long parentId = 1L;

    @Mock
    StudentRepository studentRepository;

    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    ParentRepository parentRepository;

    @Mock
    GradeRepository gradeRepository;

    @Mock
    SubjectRepository subjectRepository;

    @Mock
    StudentToStudentDto studentToStudentDto;

    @Mock
    AttendanceDtoToAttendance attendanceDtoToAttendance;
    @Mock
    ExtenuationRepository extenuationRepository;

    @Mock
    ParentToParentDto parentToParentDto;

    @Mock
    SubjectToSubjectDto subjectToSubjectDto;
    @Mock
    ExtenuationToExtenuationDto extenuationToExtenuationDto;
    @Mock
    ExtenuationDtoToExtenuation extenuationDtoToExtenuation;

    ParentService parentService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        parentService = new ParentServiceImpl(studentRepository, attendanceRepository, parentRepository, gradeRepository,
                subjectRepository, extenuationRepository,
                studentToStudentDto, attendanceDtoToAttendance, parentToParentDto, subjectToSubjectDto,
                extenuationToExtenuationDto, extenuationDtoToExtenuation);
    }


    @Test
    void listStudents() {
        Parent parent = Parent.builder()
                .id(1L)
                .build();

        Student student = Student.builder()
                .id(1L)
                .parent(Parent.builder().id(parentId).build())
                .build();

        parent.setStudents(new HashSet<>(Collections.singleton(student)));

        when(studentRepository.findAllByParentId(anyLong())).thenReturn(Collections.singletonList(student));
        when(parentRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(studentToStudentDto.convertForParent(student)).thenReturn(StudentDto.builder().id(student.getId()).build());

        List<StudentDto> returnedStudents = parentService.listStudents(parentId);

        assertNotNull(returnedStudents);
        assertEquals(student.getId(), returnedStudents.get(0).getId());
        verify(studentRepository, times(1)).findAllByParentId(parentId);
        verify(studentToStudentDto, times(1)).convertForParent(student);
    }

    @Test
    void findByStudent() {
        Student student = Student.builder()
                .id(1L)
                .build();

        Parent parent = Parent.builder()
                .id(1L)
                .students(new HashSet<>(Collections.singleton(student)))
                .build();


        when(parentRepository.findById(any())).thenReturn(Optional.of(parent));
        when(studentToStudentDto.convertForParent(any())).thenReturn(StudentDto.builder().id(student.getId()).build());
        when(studentRepository.findById(any())).thenReturn(Optional.of(student));

        StudentDto returnedStudent = parentService.findStudent(parent.getId(), student.getId());

        assertNotNull(returnedStudent);
        verify(parentRepository, times(1)).findById(any());
        verify(studentToStudentDto, times(1)).convertForParent(student);
    }

    @Test
    void getAllStudentSubjectNames() {
        Long studentId = 1L;
        Long subjectId = 1L;

        Student student = Student.builder()
                .id(1L)
                .schoolClass(Class.builder().id(1L).build())
                .build();

        Parent parent = Parent.builder()
                .id(1L)
                .students(new HashSet<>(Collections.singleton(student)))
                .build();

        Subject subject = Subject.builder().id(1L).name("xx").build();

        when(studentRepository.findById(any())).thenReturn(Optional.ofNullable(student));
        when(parentRepository.findById(any())).thenReturn(Optional.of(parent));
        when(subjectRepository.findAllBySchoolClassIdOrderByName(any())).thenReturn(Collections.singletonList(subject));


        List<SubjectDto> subjectNames = parentService.getAllStudentSubjectNames(studentId, subjectId);

        assertNotNull(subjectNames);
        verify(parentRepository, times(1)).findById(any());

    }

    @Test
    void saveAttendance() {
        AttendanceDto attendanceToSave = AttendanceDto.builder()
                .id(1L)
                .build();

        Attendance attendanceSaved = Attendance.builder().id(1L).build();

        when(attendanceDtoToAttendance.convert(attendanceToSave)).thenReturn(attendanceSaved);
        when(attendanceRepository.save(attendanceSaved)).thenReturn(attendanceSaved);

        Attendance returnedAttendance = parentService.saveAttendance(attendanceToSave);

        assertNotNull(returnedAttendance);
        assertEquals(attendanceToSave.getId(), returnedAttendance.getId());
        verify(attendanceRepository, times(1)).save(any());
        verify(attendanceDtoToAttendance, times(1)).convert(attendanceToSave);
    }

    @Test
    void findByUser() {
        Long userId = 24L;
        User user = User.builder().id(userId).build();

        Parent parentReturned = Parent.builder().id(parentId).build();

        when(parentRepository.findByUser(user)).thenReturn(Optional.of(parentReturned));
        when(parentToParentDto.convert(parentReturned)).thenReturn(ParentDto.builder().id(parentReturned.getId()).build());

        ParentDto parent = parentService.findByUser(user);

        assertEquals(parent.getId(), parentReturned.getId());
        verify(parentRepository, times(1)).findByUser(user);
        verify(parentToParentDto, times(1)).convert(parentReturned);
    }

    @Test
    void addAttendancesToExtenuation() {
        Long extId = 1L;
        Extenuation extenuation = Extenuation.builder().id(extId).build();


        when(extenuationDtoToExtenuation.convert(any())).thenReturn(extenuation);
        when(attendanceRepository.findAllById(any())).thenReturn(Collections.singletonList(Attendance.builder().build()));
        when(extenuationToExtenuationDto.convert(any())).thenReturn(ExtenuationDto.builder().id(extenuation.getId()).build());

        ExtenuationDto extenuationDto = parentService.initNewExtenuation(new ArrayList<>(){{add(1L);add(1L);}},
                ExtenuationDto.builder().build(), 1L);

        assertNotNull(extenuationDto);
        verify(attendanceRepository, times(1)).findAllById(any());
        verify(extenuationToExtenuationDto, times(1)).convert(any());
        verify(extenuationDtoToExtenuation, times(1)).convert(any());
    }


    @Test
    void saveExtenuation() {
        Long extId = 1L;
        Extenuation extenuation = Extenuation.builder().id(extId).build();


        when(extenuationDtoToExtenuation.convert(any())).thenReturn(extenuation);
        when(parentRepository.findById(any())).thenReturn(Optional.ofNullable(Parent.builder().build()));
        when(attendanceRepository.findAllById(any())).thenReturn(Collections.singletonList(Attendance.builder().build()));
        when(extenuationRepository.save(any())).thenReturn(extenuation);

        Extenuation savedExtenuation = parentService.saveExtenuation(ExtenuationDto.builder().build(), 1L,
                new ArrayList<>(){{add(1L);add(1L);}});

        assertNotNull(savedExtenuation);
        verify(parentRepository, times(1)).findById(any());
        verify(attendanceRepository, times(1)).findAllById(any());
        verify(extenuationDtoToExtenuation, times(1)).convert(any());
    }


    @Test
    void getAllExtenuations() {
        Long studentId = 1L;
        Long subjectId = 1L;


        Parent parent = Parent.builder()
                .id(1L)
                .build();

        Subject subject = Subject.builder().id(1L).name("xx").build();

        when(extenuationRepository.findAllByParentId(any())).thenReturn(Collections.singletonList(Extenuation.builder().build()));
        when(parentRepository.findById(any())).thenReturn(Optional.of(parent));
        when(extenuationToExtenuationDto.convert(any())).thenReturn(ExtenuationDto.builder().build());


        List<ExtenuationDto> extenuationDtos = parentService.getAllExtenuations(parentId);

        assertNotNull(extenuationDtos);
        verify(parentRepository, times(1)).findById(any());
        verify(extenuationRepository, times(1)).findAllByParentId(parentId);
    }

}