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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherServiceImplTest {

    private final Long teacherId = 1L;

    @Mock
    EventService eventService;

    @Mock
    ClassRepository classRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    TeacherRepository teacherRepository;
    @Mock
    BehaviorRepository behaviorRepository;
    @Mock
    LessonRepository lessonRepository;
    @Mock
    SubjectRepository subjectRepository;
    @Mock
    GradeRepository gradeRepository;
    @Mock
    AttendanceRepository attendanceRepository;
    @Mock
    StudentRepository studentRepository;
    @Mock
    TopicRepository topicRepository;
    @Mock
    SchoolPeriodRepository schoolPeriodRepository;
    @Mock
    ExtenuationRepository extenuationRepository;
    @Mock
    ParentRepository parentRepository;
    @Mock
    EndYearReportRepository endYearReportRepository;

    @Mock
    EventToEventDto eventToEventDto;
    @Mock
    EventDtoToEvent eventDtoToEvent;
    @Mock
    ClassToClassDto classToClassDto;
    @Mock
    BehaviorToBehaviorDto behaviorToBehaviorDto;
    @Mock
    BehaviorDtoToBehavior behaviorDtoToBehavior;
    @Mock
    LessonToLessonDto lessonToLessonDto;
    @Mock
    LessonDtoToLesson lessonDtoToLesson;
    @Mock
    SubjectToSubjectDto subjectToSubjectDto;
    @Mock
    SubjectDtoToSubject subjectDtoToSubject;
    @Mock
    GradeToGradeDto gradeToGradeDto;
    @Mock
    GradeDtoToGrade gradeDtoToGrade;
    @Mock
    TeacherToTeacherDto teacherToTeacherDto;
    @Mock
    AttendanceDtoToAttendance attendanceDtoToAttendance;
    @Mock
    AttendanceToAttendanceDto attendanceToAttendanceDto;
    @Mock
    StudentToStudentDto studentToStudentDto;
    @Mock
    TopicToTopicDto topicToTopicDto;
    @Mock
    TopicDtoToTopic topicDtoToTopic;
    @Mock
    ExtenuationToExtenuationDto extenuationToExtenuationDto;
    @Mock
    ExtenuationDtoToExtenuation extenuationDtoToExtenuation;
    @Mock
    EndYearReportToEndYearReportDto endYearReportToEndYearReportDto;

    @Mock
    TimetableService timetableService;

    TeacherService teacherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        teacherService = new TeacherServiceImpl(eventService, teacherRepository, classRepository, eventRepository,
                behaviorRepository, lessonRepository, subjectRepository, gradeRepository, attendanceRepository, studentRepository,
                topicRepository, schoolPeriodRepository, extenuationRepository, parentRepository, endYearReportRepository,
                eventToEventDto, eventDtoToEvent, classToClassDto, behaviorToBehaviorDto,
                behaviorDtoToBehavior, lessonToLessonDto, lessonDtoToLesson, subjectToSubjectDto, subjectDtoToSubject,
                gradeToGradeDto, gradeDtoToGrade, teacherToTeacherDto, attendanceDtoToAttendance, attendanceToAttendanceDto,
                studentToStudentDto,topicToTopicDto,topicDtoToTopic, extenuationToExtenuationDto, extenuationDtoToExtenuation,
                endYearReportToEndYearReportDto,
                timetableService);
    }

    @Test
    void findByUser() {
        Long userId = 24L;
        User user = User.builder().id(userId).build();

        Teacher teacherReturned = Teacher.builder().id(teacherId).build();

        when(teacherRepository.findByUser(user)).thenReturn(Optional.of(teacherReturned));
        when(teacherToTeacherDto.convert(teacherReturned)).thenReturn(TeacherDto.builder().id(teacherReturned.getId()).build());

        TeacherDto student = teacherService.findByUser(user);

        assertEquals(student.getId(), teacherReturned.getId());
        verify(teacherRepository, times(1)).findByUser(user);
        verify(teacherToTeacherDto, times(1)).convert(teacherReturned);
    }

    @Test
    void listLessonStudents() {
        Lesson lesson = Lesson.builder().id(1L).build();
        Class schoolClass = Class.builder().id(1L).lessons(Collections.singleton(lesson)).build();
        Subject subject = Subject.builder().id(1L).schoolClass(schoolClass).build();

        Teacher teacher = Teacher.builder().id(1L).subjects(Collections.singleton(subject)).build();
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));


        when(studentToStudentDto.convert(any())).thenReturn(StudentDto.builder().id(1L).build());

        when(studentRepository.findAllBySchoolClassId(1L)).thenReturn(Arrays.asList(
                Student.builder().id(1L).build(),
                Student.builder().id(2L).build()
        ));

        List<StudentDto> students = teacherService.listLessonStudents(1L, 1L, 1L, 1L);

        assertEquals(2, students.size());
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(studentRepository, times(1)).findAllBySchoolClassId(1L);
        verify(studentToStudentDto, times(2)).convert(any());
    }

    @Test
    void saveLesson() {
        Lesson lesson = Lesson.builder()
                .id(1L)
                .build();

        LessonDto lessonDto = LessonDto.builder().id(1L).build();

        when(lessonRepository.save(any())).thenReturn(lesson);
        when(lessonDtoToLesson.convert(any())).thenReturn(lesson);

        Lesson savedLesson = teacherService.saveLesson(lessonDto);

        assertNotNull(savedLesson);
        verify(lessonRepository, times(1)).save(lesson);
    }

    @Test
    void initNewLesson() {
        Long teacherId = 1L;
        Long subjectId = 1L;

        Lesson lesson = Lesson.builder()
                .subject(Subject.builder().id(subjectId).build())
                .build();

        LessonDto lessonDto = LessonDto.builder()
                .subjectId(1L)
                .build();

        when(classRepository.findAllBySubjects(any())).thenReturn(Collections.singletonList(Class.builder().build()));
        when(subjectRepository.findById(any())).thenReturn(Optional.ofNullable(Subject.builder().id(subjectId).build()));
        when(lessonToLessonDto.convert(any())).thenReturn(lessonDto);

        LessonDto genLesson = teacherService.initNewLesson(subjectId);

        assertNotNull(genLesson);
        assertEquals(lesson.getSubject().getId(), lessonDto.getSubjectId());
        verify(subjectRepository, times(1)).findById(any());

    }



    @Test
    void listEvents() {
        Teacher teacher = Teacher.builder().id(1L).build();
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        when(eventToEventDto.convert(any())).thenReturn(EventDto.builder().id(3L).build());
        when(eventRepository.findAllByTeacherIdAndDateAfter(any(), any(), any())).thenReturn(Arrays.asList(
                Event.builder().build(),
                Event.builder().build()
        ));

        when(eventService.listEventsByTeacher(teacher)).thenReturn(Arrays.asList(
                Event.builder().id(1L).build(),
                Event.builder().id(2L).build()
        ));

        List<EventDto> events = teacherService.listEvents(teacherId, 0, 1, false);

        assertEquals(2, events.size());
        verify(teacherRepository, times(1)).findById(any());
        verify(eventToEventDto, times(2)).convert(any());
    }

    @Test
    void getEvent() {
        Long eventId = 3L;
        Teacher teacher = Teacher.builder().id(1L).build();
        Event eventDB = Event.builder().id(eventId).teacher(teacher).build();

        when(eventRepository.findById(any())).thenReturn(Optional.of(eventDB));
        when(eventToEventDto.convert(any())).thenReturn(EventDto.builder().id(eventDB.getId()).build());
        when(teacherRepository.findById(any())).thenReturn(Optional.ofNullable(teacher));

        EventDto event = teacherService.getEvent(teacherId, eventId);

        assertEquals(eventId, event.getId());
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventToEventDto, times(1)).convert(any());
    }

    @Test
    void saveEvent() {
        Long eventId = 4L;
        Teacher teacher = Teacher.builder().build();
        EventDto eventToSave = EventDto.builder().id(eventId).build();
        Event eventReturned = Event.builder().id(eventId).teacher(teacher).build();

        when(eventDtoToEvent.convert(eventToSave)).thenReturn(Event.builder().id(eventToSave.getId()).teacher(teacher).build());
        when(eventRepository.findById(any())).thenReturn(Optional.ofNullable(eventReturned));
        when(eventRepository.save(any())).thenReturn(eventReturned);
        when(teacherRepository.findById(any())).thenReturn(Optional.ofNullable(teacher));

        Event savedEvent = teacherService.saveOrUpdateEvent(eventToSave);

        verify(eventDtoToEvent, times(1)).convert(eventToSave);
        verify(eventRepository, times(1)).save(any());
    }

    @Test
    void initNewEvent() {
        Teacher teacher = Teacher.builder().id(1L).build();
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        when(eventToEventDto.convert(any())).thenReturn(EventDto.builder().id(2L).build());

        EventDto newEvent = teacherService.initNewEvent(teacherId);

        assertEquals(2L, newEvent.getId());
        verify(teacherRepository, times(2)).findById(teacherId);
        verify(eventToEventDto, times(1)).convert(any());
    }

    @Test
    void initNewClassEvent() {
        Long classId = 1L;

        Teacher teacher = Teacher.builder().id(1L).subjects(new HashSet<>(){{add(Subject.builder().id(1L).build());}}).build();
        Class schoolClass = Class.builder().id(classId).build();

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(classRepository.findById(1L)).thenReturn(Optional.of(schoolClass));
        when(subjectRepository.findById(any())).thenReturn(Optional.ofNullable(Subject.builder().build()));

        when(eventToEventDto.convert(any())).thenReturn(EventDto.builder().id(1L).build());

        EventDto newEvent = teacherService.initNewClassEvent(teacherId, classId);

        assertEquals(1L, newEvent.getId());
        verify(teacherRepository, times(2)).findById(teacherId);
        verify(eventToEventDto, times(1)).convert(any());
    }

    @Test
    void deleteEvent() {
        Teacher teacher = Teacher.builder().id(teacherId).build();

        Long eventId = 2L;
        Event event = Event.builder().id(eventId).teacher(teacher).build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(teacherRepository.findById(any())).thenReturn(Optional.ofNullable(teacher));

        Boolean deleteStatus = teacherService.deleteEvent(teacherId, eventId);

        assertTrue(deleteStatus);
        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(1)).delete(any());
    }

    @Test
    void deleteEventNotOwner() {
        Teacher teacher = Teacher.builder().id(teacherId).build();

        Long eventId = 2L;
        Event event = Event.builder().id(eventId).teacher(Teacher.builder().id(teacherId + 1).build()).build();

        when(eventRepository.findById(any())).thenReturn(Optional.of(event));
        when(teacherRepository.findById(any())).thenReturn(Optional.ofNullable(teacher));

//        Exception exception = assertThrows(NotFoundException.class, () -> {
            Boolean deleteStatus = teacherService.deleteEvent(teacherId, eventId);
//        });

        verify(eventRepository, times(1)).findById(eventId);
        verify(eventRepository, times(0)).delete(any());
    }

    @Test
    void listAllClasses() {

        when(classRepository.findAll()).thenReturn(Arrays.asList(
                Class.builder().id(1L).build(),
                Class.builder().id(2L).build()
        ));

        when(classToClassDto.convert(any())).thenReturn(SchoolClassDto.builder().build());

        List<SchoolClassDto> returnedClasses = teacherService.listAllClasses();

        assertEquals(2, returnedClasses.size());
        verify(classRepository, times(1)).findAll();
        verify(classToClassDto, times(2)).convert(any());
    }


    @Test
    void updatePatchEvent() {
        EventDto event = EventDto.builder()
                .id(1L)
                .description("first desc")
                .date(new Date(1, 2, 3))
                .type(Event.Type.EXAM)
                .build();

        EventDto eventToUpdate = EventDto.builder()
                .description("second desc")
                .date(new Date(4, 4, 4))
                .build();

        Event eventDB = Event.builder().build();
        Event savedEvent = Event.builder().build();

        when(eventRepository.findById(eventToUpdate.getId())).thenReturn(Optional.of(eventDB));
        when(eventToEventDto.convert(eventDB)).thenReturn(event);

        when(eventRepository.save(any())).thenReturn(savedEvent);
        when(eventToEventDto.convert(savedEvent)).thenReturn(event);

       EventDto eventDto = teacherService.updatePatchEvent(eventToUpdate);

        assertEquals(eventDto.getId(), event.getId());
        assertNotEquals(eventDto.getDate(), eventToUpdate.getDate());

        assertNotEquals(eventDto.getType(), eventToUpdate.getType());

        verify(eventRepository, times(1)).findById(eventToUpdate.getId());
        verify(eventToEventDto, times(1)).convert(any());
        verify(eventRepository, times(1)).save(any());
    }

    @Test
    void saveBehavior() {
        Long behaviorId = 4L;
        BehaviorDto behaviorToSave = BehaviorDto.builder().id(behaviorId).build();
        Behavior behaviorReturned = Behavior.builder().id(behaviorId).build();

        when(behaviorDtoToBehavior.convert(behaviorToSave)).thenReturn(Behavior.builder().id(behaviorToSave.getId()).build());
        when(behaviorRepository.save(any())).thenReturn(behaviorReturned);

        Behavior behavior = teacherService.saveBehavior(behaviorToSave);

        assertEquals(behaviorId, behavior.getId());
        verify(behaviorDtoToBehavior, times(1)).convert(behaviorToSave);
        verify(behaviorRepository, times(1)).save(any());
    }

    @Test
    void listBehaviorsByTeacher() {
        Teacher teacher = Teacher.builder().id(1L).build();
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        when(behaviorRepository.findAllByTeacherOrderByDateDesc(any(), any())).thenReturn(Arrays.asList(
                Behavior.builder().id(1L).teacher(teacher).build(),
                Behavior.builder().id(2L).teacher(teacher).build()
        ));

        when(behaviorToBehaviorDto.convert(any(Behavior.class))).thenReturn(BehaviorDto.builder().id(2L).build());

        List<BehaviorDto> behaviors = teacherService.listBehaviors(teacherId, 0, 2);

        assertEquals(2, behaviors.size());
        assertEquals(2L, behaviors.get(1).getId());
        verify(teacherRepository,times(1)).findById(teacherId);
        verify(behaviorRepository, times(1)).findAllByTeacherOrderByDateDesc(any(), any());
        verify(behaviorToBehaviorDto, times(2)).convert(any());
    }

    @Test
    void initNewBehavior() {
        Teacher teacher = Teacher.builder().id(1L).build();
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        when(behaviorToBehaviorDto.convert(any())).thenReturn(BehaviorDto.builder().id(2L).build());

        BehaviorDto behavior = teacherService.initNewBehavior(teacherId);

        assertEquals(2L, behavior.getId());
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(behaviorToBehaviorDto, times(1)).convert(any());
    }

    @Test
    void initNewBehaviorWithTwoId() {
        Long studentId = 2L;

        Teacher teacher = Teacher.builder().id(1L).build();
        Student student = Student.builder().id(2L).build();
        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        when(behaviorToBehaviorDto.convert(any())).thenReturn(
                BehaviorDto.builder().id(2L).teacherId(teacher.getId()).studentId(student.getId()).build());

        BehaviorDto behavior = teacherService.initNewBehavior(teacherId, studentId);

        assertEquals(2L, behavior.getId());
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(studentRepository, times(1)).findById(studentId);
        verify(behaviorToBehaviorDto, times(1)).convert(any());
    }

    @Test
    void deleteBehavior() {
        Teacher teacher = Teacher.builder().id(teacherId).build();

        Long behaviorId = 2L;
        Behavior behavior = Behavior.builder().id(behaviorId).teacher(teacher).build();

        when(behaviorRepository.findById(behaviorId)).thenReturn(Optional.of(behavior));

        Boolean deleteStatus = teacherService.deleteBehavior(teacherId,behaviorId);

        assertTrue(deleteStatus);
        verify(behaviorRepository, times(1)).findById(behaviorId);
        verify(behaviorRepository, times(1)).delete(any());
    }

    @Test
    void deleteBehaviorNotOwner() {
        Teacher teacher = Teacher.builder().id(teacherId).build();

        Long behaviorId = 2L;
        Behavior behavior = Behavior.builder().id(behaviorId).teacher(Teacher.builder().id(teacherId + 1).build()).build();

        when(behaviorRepository.findById(behaviorId)).thenReturn(Optional.of(behavior));

        Boolean deleteStatus = teacherService.deleteBehavior(teacherId,behaviorId);

        assertFalse(deleteStatus);
        verify(behaviorRepository, times(1)).findById(behaviorId);
        verify(behaviorRepository, times(0)).delete(any());
    }

    @Test
    void deleteBehaviorNotFound() {

        Long behaviorId = 2L;

        when(behaviorRepository.findById(behaviorId)).thenReturn(Optional.empty());

        Boolean deleteStatus = teacherService.deleteBehavior(teacherId,behaviorId);

        assertFalse(deleteStatus);
        verify(behaviorRepository, times(1)).findById(behaviorId);
        verify(behaviorRepository, times(0)).delete(any());
    }

    @Test
    void updatePutBehavior() {
        Long behaviorToUpdateId = 1L;
        String behaviorToUpdateContent = "before";
        BehaviorDto behaviorToUpdate = BehaviorDto.builder()
                .id(behaviorToUpdateId).content(behaviorToUpdateContent).build();

        Long behaviorUpdatedId = 1L;
        String behaviorUpdatedContent = "after";
        BehaviorDto behaviorUpdated = BehaviorDto.builder()
                .id(behaviorUpdatedId).content(behaviorUpdatedContent).build();

        when(behaviorDtoToBehavior.convert(behaviorToUpdate)).thenReturn(
                Behavior.builder().id(behaviorUpdatedId).build());
        when(behaviorRepository.save(any())).thenReturn(Behavior.builder().build());

        BehaviorDto behavior = teacherService.updatePutBehavior(behaviorToUpdate);

        verify(behaviorDtoToBehavior, times(1)).convert(any());
        verify(behaviorRepository, times(1)).save(any());
    }

    @Test
    void updatePatchBehavior() {

        BehaviorDto behavior = BehaviorDto.builder()
                .id(1L)
                .content("first")
                .date(new Date(1, 2, 3))
                .positive(true)
                .build();

        BehaviorDto behaviorToUpdate = BehaviorDto.builder()
                .content("second")
                .build();

        Behavior behaviorDB = Behavior.builder().build();
        Behavior savedBehavior = Behavior.builder().build();

        when(behaviorRepository.findById(behaviorToUpdate.getId())).thenReturn(Optional.of(behaviorDB));
        when(behaviorToBehaviorDto.convert(behaviorDB)).thenReturn(behavior);

        when(behaviorRepository.save(any())).thenReturn(savedBehavior);
        when(behaviorToBehaviorDto.convert(savedBehavior)).thenReturn(behavior);

        BehaviorDto behaviorDto = teacherService.updatePatchBehavior(behaviorToUpdate);

        assertEquals(behaviorDto.getId(), behavior.getId());
        assertEquals(behaviorDto.getContent(), behaviorToUpdate.getContent());

        verify(behaviorRepository, times(1)).findById(behaviorToUpdate.getId());
        verify(behaviorToBehaviorDto, times(2)).convert(any());
        verify(behaviorDtoToBehavior, times(1)).convert(any());
        verify(behaviorRepository, times(1)).save(any());
    }

    @Test
    void listLessonsWithSubjectId() {
        Long subjectId = 2L;

        Subject subject = Subject.builder()
                .id(subjectId)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .subjects(Collections.singleton(subject))
                .build();

        List<Lesson> lessons = new ArrayList<>(){{
            add(Lesson.builder().build());
        }};


        Page<Lesson> page = new PageImpl<>(lessons);


        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        when(lessonToLessonDto.convert(any())).thenReturn(LessonDto.builder().id(1L).subjectId(subjectId).build());

        when(lessonRepository.findAllBySubjectIdOrderByDateDesc(any(), any())).thenReturn(page);

        List<LessonDto> lessons2 = teacherService.listLessons(0, 1, teacherId, subjectId);

        assertEquals(1, lessons.size());
        assertNotNull(lessons2);
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(lessonRepository, times(1)).findAllBySubjectIdOrderByDateDesc(any(), any());
        verify(lessonToLessonDto, times(1)).convert(any());
    }


    @Test
    void listLessonsWithSubjectClassId() {
        Long subjectId = 1L;
        Long classId = 1L;

        Class schoolClass = Class.builder().id(classId).build();

        Subject subject = Subject.builder()
                .id(subjectId)
                .schoolClass(schoolClass)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .subjects(Collections.singleton(subject))
                .build();

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        when(lessonToLessonDto.convert(any())).thenReturn(LessonDto.builder().id(1L).subjectId(subjectId).build());

        when(lessonRepository.findAllBySubjectIdAndSchoolClassId(subjectId, classId)).thenReturn(Arrays.asList(
                Lesson.builder().id(1L).subject(subject).build(),
                Lesson.builder().id(2L).subject(subject).build()
        ));

        List<LessonDto> lessons = teacherService.listLessons(teacherId, subjectId, classId);

        assertEquals(2, lessons.size());
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(lessonRepository, times(1)).findAllBySubjectIdAndSchoolClassId(subjectId, classId);
        verify(lessonToLessonDto, times(2)).convert(any());
    }

    @Test
    void getLesson() {
        Long lessonId = 1L;
        Lesson lesson = Lesson.builder().id(lessonId).build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonToLessonDto.convert(any())).thenReturn(LessonDto.builder().id(lesson.getId()).build());

        LessonDto lessonDto = teacherService.getLesson(lessonId);

        assertEquals(lessonId, lessonDto.getId());
        verify(lessonRepository, times(1)).findById(lessonId);
        verify(lessonToLessonDto, times(1)).convert(any());
    }

    @Test
    void initNewTopic() {
        Long subjectId = 25L;
        Subject subject = Subject.builder().id(subjectId).build();

        Long topicNumber = 10L;
        Topic topicReturned = Topic.builder().number(topicNumber).build();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(topicRepository.findBySubjectOrderByNumberDesc(subject)).thenReturn(
                Optional.of(topicReturned));
        when(topicToTopicDto.convert(any())).thenReturn(TopicDto.builder().number(topicReturned.getNumber() + 1).build());

        TopicDto topic = teacherService.initNewTopic(teacherId, subjectId);

        assertEquals(topic.getNumber(),topicNumber + 1);
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(topicRepository, times(1)).findBySubjectOrderByNumberDesc(subject);
        verify(topicToTopicDto, times(1)).convert(any());

    }

    @Test
    void updateTopic() {
        Long topicId = 3L;
        TopicDto topicToSave = TopicDto.builder().id(topicId).build();
        Topic topicReturned = Topic.builder().id(topicId).build();

        when(topicDtoToTopic.convert(topicToSave)).thenReturn(Topic.builder().id(topicId).build());
        when(topicRepository.save(any())).thenReturn(topicReturned);

        Topic topic = teacherService.updateTopic(topicToSave);

        assertEquals(topic.getId(), topicToSave.getId());
        verify(topicDtoToTopic, times(1)).convert(topicToSave);
        verify(topicRepository, times(1)).save(any());
    }

    @Test
    void saveTopic() {
        Long returnedTopicId = 3L;
        TopicDto topicToSave = TopicDto.builder().build();
        Topic topicReturned = Topic.builder().id(returnedTopicId).build();

        when(topicDtoToTopic.convertForSave(topicToSave)).thenReturn(Topic.builder().id(returnedTopicId).build());
        when(topicRepository.save(any())).thenReturn(topicReturned);

        Topic topic = teacherService.saveTopic(topicToSave);

        verify(topicDtoToTopic, times(1)).convertForSave(topicToSave);
        verify(topicRepository, times(1)).save(any());
    }

    @Test
    void deleteTopic() {
        Teacher teacher = Teacher.builder().id(teacherId).build();

        Long subjectId = 25L;
        Subject subject = Subject.builder().id(subjectId).teacher(teacher).build();

        Long topicId = 3L;
        Topic topic = Topic.builder().id(topicId).subject(subject).build();

        when(topicRepository.findById(topicId)).thenReturn(Optional.of(topic));

        Boolean deleteStatus = teacherService.deleteTopic(teacherId,subjectId, topicId);

        assertTrue(deleteStatus);
        verify(topicRepository, times(1)).findById(topicId);
        verify(topicRepository, times(1)).delete(topic);
    }

    @Test
    void listTopics() {
        Long subjectId = 25L;
        Subject subject = Subject.builder().id(subjectId).build();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(topicRepository.findAllBySubjectOrderByNumber(subject)).thenReturn(Arrays.asList(
                Topic.builder().id(1L).build(),
                Topic.builder().id(2L).build()
        ));
        when(topicToTopicDto.convert(any())).thenReturn(TopicDto.builder().id(3L).build());

        List<TopicDto> topic = teacherService.listTopics(teacherId, subjectId);

        assertEquals(topic.size(), 2);
        assertEquals(topic.get(0).getId(), 3L);
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(topicRepository, times(1)).findAllBySubjectOrderByNumber(subject);
        verify(topicToTopicDto, times(2)).convert(any());
    }

    @Test
    void updatePatchTopic() {

        TopicDto topic = TopicDto.builder()
                .id(1L)
                .number(3L)
                .name("name before")
                .description("desc")
                .build();

        TopicDto topicToUpdate = TopicDto.builder()
                .number(4L)
                .name("name after")
                .build();

        Topic topicDB = Topic.builder().build();
        Topic savedTopic = Topic.builder().build();

        when(topicRepository.findById(topicToUpdate.getId())).thenReturn(Optional.of(topicDB));
        when(topicToTopicDto.convert(topicDB)).thenReturn(topic);

        when(topicRepository.save(any())).thenReturn(savedTopic);
        when(topicToTopicDto.convert(savedTopic)).thenReturn(topic);

        TopicDto topicDto = teacherService.updatePatchTopic(topicToUpdate);

        assertEquals(topicDto.getId(), topic.getId());
        assertEquals(topicDto.getNumber(), topicToUpdate.getNumber());
        assertEquals(topicDto.getName(), topicToUpdate.getName());
        assertEquals(topicDto.getDescription(), topic.getDescription());

        verify(topicRepository, times(1)).findById(topicToUpdate.getId());
        verify(topicToTopicDto, times(2)).convert(any());
        verify(topicDtoToTopic, times(1)).convert(any());
        verify(topicRepository, times(1)).save(any());
    }

    @Test
    void listSubjects() {
        Long subjectId = 2L;

        Subject subject = Subject.builder()
                .id(subjectId)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .build();

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        when(subjectToSubjectDto.convert(any())).thenReturn(SubjectDto.builder().id(1L).build());

        when(subjectRepository.findAllByTeacher(teacher)).thenReturn(Arrays.asList(
                Subject.builder().id(1L).teacher(teacher).build(),
                Subject.builder().id(2L).teacher(teacher).build()
        ));

        List<SubjectDto> subjects = teacherService.listSubjects(teacherId);

        assertEquals(2, subjects.size());
        assertEquals(1L, subjects.get(0).getId());
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(subjectRepository, times(1)).findAllByTeacher(teacher);
        verify(subjectToSubjectDto, times(2)).convert(any());
    }

    @Test
    void getSubjectById() {
        Long subjectId = 2l;

        Teacher teacher = Teacher.builder().id(teacherId).build();
        Subject subject = Subject.builder().id(subjectId).teacher(teacher).build();

        when(teacherRepository.findById(any())).thenReturn(Optional.of(teacher));
        when(subjectRepository.findById(any())).thenReturn(Optional.of(subject));
        when(subjectToSubjectDto.convert(subject)).thenReturn(SubjectDto.builder().id(subject.getId()).build());

        SubjectDto subjectDto = teacherService.getSubjectById(teacherId, subjectId);

        assertEquals(subjectDto.getId(), subjectId);
        verify(subjectToSubjectDto, times(1)).convert(any());
        verify(teacherRepository, times(1)).findById(any());
        verify(subjectRepository, times(1)).findById(any());
    }

    @Test
    void initNewSubject() {
        Teacher teacher = Teacher.builder().id(1L).build();

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(subjectToSubjectDto.convert(any())).thenReturn(SubjectDto.builder().teacherId(teacherId).build());

        SubjectDto subject = teacherService.initNewSubject(teacherId);

        assertEquals(subject.getTeacherId(), teacher.getId());
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(subjectToSubjectDto, times(1)).convert(any());

    }

    @Test
    void updatePatchSubject() {

        SubjectDto subject = SubjectDto.builder()
                .id(1L)
                .name("first")
                .build();

        SubjectDto subjectToUpdate = SubjectDto.builder()
                .name("second")
                .build();

        Subject subjectDB = Subject.builder().build();
        Subject savedSubject = Subject.builder().build();

        when(subjectRepository.findById(subjectToUpdate.getId())).thenReturn(Optional.of(subjectDB));
        when(subjectToSubjectDto.convert(subjectDB)).thenReturn(subject);

        when(subjectRepository.save(any())).thenReturn(savedSubject);
        when(subjectToSubjectDto.convert(savedSubject)).thenReturn(subject);

        SubjectDto subjectDto = teacherService.updatePatchSubject(subjectToUpdate);

        assertEquals(subjectDto.getId(), subject.getId());
        assertEquals(subjectDto.getName(), subjectToUpdate.getName());

        verify(subjectRepository, times(1)).findById(subjectToUpdate.getId());
        verify(subjectToSubjectDto, times(2)).convert(any());
        verify(subjectDtoToSubject, times(1)).convert(any());
        verify(subjectRepository, times(1)).save(any());
    }

    @Test
    void saveAttendances() {
        Long attendanceId = 1L;

        AttendanceDto attendanceDto = AttendanceDto.builder().id(attendanceId).build();
        Attendance attendance = Attendance.builder().id(attendanceId).build();

        when(attendanceDtoToAttendance.convert(attendanceDto)).thenReturn(Attendance.builder().id(attendanceId).build());
        when(attendanceRepository.save(any())).thenReturn(attendance);

        Attendance returnedAttendance = teacherService.saveAttendance(attendanceDto);

        assertEquals(returnedAttendance.getId(), attendance.getId());
        verify(attendanceDtoToAttendance, times(1)).convert(any());
        verify(attendanceRepository, times(1)).save(any());

    }

    @Test
    void listAttendances() {
        Long subjectId = 2L;
        Long lessonId = 1L;

        Lesson lesson = Lesson.builder().id(lessonId).build();
        Class schoolClass = Class.builder().id(1L).lessons(Collections.singleton(lesson)).build();

        Subject subject = Subject.builder()
                .id(subjectId)
                .schoolClass(schoolClass)
                .build();

        Teacher teacher = Teacher.builder()
                .id(1L)
                .subjects(Collections.singleton(subject))
                .build();

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

        when(attendanceToAttendanceDto.convert(any())).thenReturn(AttendanceDto.builder().id(1L).build());

        when(attendanceRepository.findAllByLesson_Id(lessonId))
                .thenReturn(new HashSet<>(Collections.singleton(Attendance.builder().build())));

        List<AttendanceDto> attendances = teacherService.listAttendances(teacherId, subjectId, 1L, 1L);

        assertEquals(1, attendances.size());
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(attendanceRepository, times(1)).findAllByLesson_Id(lessonId);
        verify(attendanceToAttendanceDto, times(1)).convert(any());
    }

    @Test
    void saveOrUpdateSubject() {
        Long subjectId = 3L;
        SubjectDto subjecToSave = SubjectDto.builder().id(subjectId).build();
        Subject subjectReturned = Subject.builder().id(subjectId).build();

        when(subjectDtoToSubject.convert(subjecToSave)).thenReturn(Subject.builder().id(subjectId).build());
        when(subjectRepository.save(any())).thenReturn(subjectReturned);

        Subject subject = teacherService.saveOrUpdateSubject(subjecToSave);

        assertEquals(subject.getId(), subjecToSave.getId());
        verify(subjectDtoToSubject, times(1)).convert(subjecToSave);
        verify(subjectRepository, times(1)).save(any());
    }

    @Test
    void deleteSubject() {
        Teacher teacher = Teacher.builder().id(teacherId).build();

        Long subjectId = 2L;
        Subject subject = Subject.builder().id(subjectId).teacher(teacher).build();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        Boolean deleteStatus = teacherService.deleteSubject(teacherId,subjectId);

        assertTrue(deleteStatus);
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(subjectRepository, times(1)).delete(any());
    }

    @Test
    void deleteSubjectNotOwner() {
        Teacher teacher = Teacher.builder().id(teacherId).build();

        Long subjectId = 2L;
        Subject subject = Subject.builder().id(subjectId).teacher(Teacher.builder().id(teacherId + 1L).build()).build();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));

        Boolean deleteStatus = teacherService.deleteSubject(teacherId,subjectId);

        assertFalse(deleteStatus);
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(subjectRepository, times(0)).delete(any());
    }

    @Test
    void deleteSubjectNotFound() {

        Long subjectId = 2L;

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());

        Boolean deleteStatus = teacherService.deleteSubject(teacherId,subjectId);

        assertFalse(deleteStatus);
        verify(subjectRepository, times(1)).findById(subjectId);
        verify(subjectRepository, times(0)).delete(any());
    }

    @Test
    void listGradesBySubject() {
        Long teacherId = 1L;
        Long subjectId = 1L;

        Teacher teacher = Teacher.builder()
                .id(teacherId)
                .build();

        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(Subject.builder().id(subjectId).build()));
        when(gradeToGradeDto.convert(any())).thenReturn(GradeDto.builder().id(1L).build());
        when(gradeRepository.findAllBySubjectIdAndTeacherId(subjectId, teacherId)).thenReturn(Arrays.asList(
                Grade.builder().id(1L).teacher(teacher).build(),
                Grade.builder().id(2L).teacher(teacher).build()
        ));

        List<GradeDto> grades = teacherService.listGradesBySubject(teacherId, subjectId);

        assertEquals(2, grades.size());
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(gradeRepository, times(1)).findAllBySubjectIdAndTeacherId(subjectId, teacherId);
        verify(gradeToGradeDto, times(2)).convert(any());

    }

    @Test
    void updatePutGrade() {

        Long gradeId = 1L;
        String description = "description";
        String updatedDescription = "updated description";

        GradeDto grade = GradeDto.builder()
                .id(gradeId).description(description).build();

        Grade updatedGrade = Grade.builder()
                .id(gradeId).description(updatedDescription).build();

        when(gradeDtoToGrade.convert(any())).thenReturn(updatedGrade);
        when(gradeToGradeDto.convert(any())).thenReturn(GradeDto.builder().id(gradeId).description(updatedDescription).build());
        when(gradeRepository.save(any())).thenReturn(Grade.builder().build());

        GradeDto gradeDto = teacherService.updatePutGrade(grade);


        assertEquals(gradeDto.getDescription(), updatedGrade.getDescription());
        verify(gradeDtoToGrade, times(1)).convert(any());
        verify(gradeRepository, times(1)).save(any());
    }

    @Test
    void updatePatchGrade() {
        Long gradeId = 1L;
        String description = "description";
        String updatedDescription = "updated description";

        GradeDto gradeDto = GradeDto.builder()
                .id(gradeId).description(description)
                .build();

        GradeDto updatedGradeDto = GradeDto.builder()
                .id(gradeId).description(updatedDescription)
                .value("2").weight(5)
                .build();

        Grade grade = Grade.builder()
                .id(gradeId).description(updatedDescription).build();

        when(gradeDtoToGrade.convert(any())).thenReturn(grade);
        when(gradeToGradeDto.convert(any())).thenReturn(updatedGradeDto);
        when(gradeRepository.save(any())).thenReturn(Grade.builder().build());
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.ofNullable(Grade.builder().build()));

        GradeDto savedGrade = teacherService.updatePatchGrade(gradeDto);


        assertEquals(savedGrade.getDescription(), updatedGradeDto.getDescription());
        verify(gradeDtoToGrade, times(1)).convert(any());
        verify(gradeRepository, times(1)).save(any());
    }

    @Test
    void deleteGrade() {
        Long teacherId = 1L;
        Long subjectId = 1L;
        Long gradeId = 1L;

        Student student = Student.builder().build();

        Grade gradeToDelete = Grade.builder()
                .id(gradeId)
                .student(student)
                .subject(Subject.builder().id(subjectId).build())
                .teacher(Teacher.builder().id(teacherId).build())
                .build();

        when(gradeRepository.findById(any())).thenReturn(Optional.ofNullable(gradeToDelete));
        when(studentRepository.findById(any())).thenReturn(Optional.ofNullable(student));

        Boolean result = teacherService.deleteGrade(1L, gradeId);

        assertTrue(result);
        verify(gradeRepository, times(1)).findById(gradeId);
        verify(gradeRepository, times(1)).delete(any());
    }

    @Test
    void saveOrUpdateGrade() {
        Grade grade = Grade.builder()
                .id(1L)
                .build();

        GradeDto gradeDto = GradeDto.builder().id(1L).build();

        when(gradeRepository.save(any())).thenReturn(grade);
        when(gradeDtoToGrade.convert(any())).thenReturn(grade);

        Grade savedGrade = teacherService.saveOrUpdateGrade(gradeDto);

        assertNotNull(savedGrade);
        verify(gradeRepository, times(1)).save(grade);

    }

    @Test
    void initNewGrade() {
        Long teacherId = 1L;
        Long subjectId = 1L;

        Grade grade = Grade.builder()
                .teacher(Teacher.builder().id(teacherId).build())
                .subject(Subject.builder().id(subjectId).build())
                .build();

        GradeDto gradeDto = GradeDto.builder()
                .teacherId(1L)
                .subjectId(1L)
                .build();

        when(teacherRepository.findById(any())).thenReturn(Optional.ofNullable(Teacher.builder().id(teacherId).build()));
        when(subjectRepository.findById(any())).thenReturn(Optional.ofNullable(Subject.builder().id(subjectId).build()));
        when(gradeToGradeDto.convert(any())).thenReturn(gradeDto);

        GradeDto genGrade = teacherService.initNewGrade(teacherId, subjectId);

        assertNotNull(genGrade);
        assertEquals(grade.getTeacher().getId(), gradeDto.getTeacherId());
        assertEquals(grade.getSubject().getId(), gradeDto.getSubjectId());
        verify(teacherRepository, times(1)).findById(any());
        verify(subjectRepository, times(1)).findById(any());

    }

    @Test
    void listClassesByTeacherAndSubject() {
        Long teacherId = 1L;
        Long subjectId = 1L;

        Subject subject = Subject.builder().id(subjectId).build();

        Teacher teacher = Teacher.builder()
                .id(teacherId)
                .subjects(Collections.singleton(subject))
                .build();


        when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(classToClassDto.convert(any())).thenReturn(SchoolClassDto.builder().id(1L).build());
        when(classRepository.findAllBySubjects(any())).thenReturn(Arrays.asList(
                Class.builder().id(1L).teacher(teacher).subjects(Collections.singleton(subject)).build(),
                Class.builder().id(2L).teacher(teacher).subjects(Collections.singleton(subject)).build()
        ));

        List<SchoolClassDto> classes = teacherService.listClassesByTeacherAndSubject(teacherId, subjectId);

        assertEquals(2, classes.size());
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(classRepository, times(1)).findAllBySubjects(any());
        verify(classToClassDto, times(2)).convert(any());

    }


    @Test
    void getSchoolClassByTeacherAndSubject() {
        Long subjectId = 1L;
        Long teacherId = 1L;
        Long classId = 1L;

        Class schoolClass = Class.builder().id(classId).build();
        Subject subject = Subject.builder().id(subjectId).schoolClass(schoolClass).build();
        Teacher teacher = Teacher.builder().id(teacherId)
                .subjects(Collections.singleton(subject))
                .build();


        when(teacherRepository.findById(any())).thenReturn(Optional.ofNullable(teacher));
        when(classRepository.findById(any())).thenReturn(Optional.ofNullable(schoolClass));
        when(classToClassDto.convert(any())).thenReturn(SchoolClassDto.builder().id(classId).build());

        SchoolClassDto foundSchoolClass = teacherService.getSchoolClassByTeacherAndSubject(classId, subjectId, teacherId);

        assertNotNull(foundSchoolClass);
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(classRepository, times(1)).findById(any());
        verify(classToClassDto, times(1)).convert(any());

    }

}