package com.ediary.services;

import com.ediary.DTO.*;
import com.ediary.converters.*;
import com.ediary.domain.*;
import com.ediary.domain.Class;
import com.ediary.domain.helpers.GradeWeight;
import com.ediary.domain.security.User;
import com.ediary.domain.timetable.Timetable;
import com.ediary.exceptions.NoAccessException;
import com.ediary.exceptions.NotFoundException;
import com.ediary.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeacherServiceImpl implements TeacherService {

    private final EventService eventService;

    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;
    private final EventRepository eventRepository;
    private final BehaviorRepository behaviorRepository;
    private final LessonRepository lessonRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final TopicRepository topicRepository;
    private final SchoolPeriodRepository schoolPeriodRepository;
    private final ExtenuationRepository extenuationRepository;
    private final ParentRepository parentRepository;
    private final EndYearReportRepository endYearReportRepository;

    private final EventToEventDto eventToEventDto;
    private final EventDtoToEvent eventDtoToEvent;
    private final ClassToClassDto classToClassDto;
    private final BehaviorToBehaviorDto behaviorToBehaviorDto;
    private final BehaviorDtoToBehavior behaviorDtoToBehavior;
    private final LessonToLessonDto lessonToLessonDto;
    private final LessonDtoToLesson lessonDtoToLesson;
    private final SubjectToSubjectDto subjectToSubjectDto;
    private final SubjectDtoToSubject subjectDtoToSubject;
    private final GradeToGradeDto gradeToGradeDto;
    private final GradeDtoToGrade gradeDtoToGrade;
    private final TeacherToTeacherDto teacherToTeacherDto;
    private final AttendanceDtoToAttendance attendanceDtoToAttendance;
    private final AttendanceToAttendanceDto attendanceToAttendanceDto;
    private final StudentToStudentDto studentToStudentDto;
    private final TopicToTopicDto topicToTopicDto;
    private final TopicDtoToTopic topicDtoToTopic;
    private final ExtenuationToExtenuationDto extenuationToExtenuationDto;
    private final ExtenuationDtoToExtenuation extenuationDtoToExtenuation;
    private final EndYearReportToEndYearReportDto endYearReportToEndYearReportDto;

    private final TimetableService timetableService;

    @Override
    public TeacherDto findByUser(User user) {
        Optional<Teacher> teacherOptional = teacherRepository.findByUser(user);
        if (teacherOptional.isEmpty()) {
            throw new NotFoundException("Teacher Not Found.");
        }

        return teacherToTeacherDto.convert(teacherOptional.get());
    }

    public List<StudentDto> listStudentsBySchoolClassId(Long schoolClassId) {
        return studentRepository.findAllBySchoolClassId(schoolClassId)
                .stream()
                .map(studentToStudentDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDto> listLessonStudents(Long teacherId, Long subjectId, Long classId, Long lessonId) {
        Teacher teacher = getTeacherById(teacherId);

        teacher.getSubjects().stream()
                .filter(s -> s.getSchoolClass().getLessons()
                        .stream()
                        .anyMatch(lesson -> lesson.getId().equals(lessonId)))
                .findAny().orElseThrow(() -> new AccessDeniedException("Class -> Lesson"));

        return studentRepository.findAllBySchoolClassId(classId).stream()
                .map(studentToStudentDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDto initNewLesson(Long subjectId) {

        Subject subject = getSubjectById(subjectId);

        Class schoolClass = classRepository.findAllBySubjects(subject)
                .stream()
                .findFirst()
                .orElse(null);

        if (schoolClass == null) {
            return null;
        }

        return lessonToLessonDto.convert(Lesson.builder()
                .subject(subject)
                .schoolClass(schoolClass)
                .build());
    }

    @Override
    public Lesson saveLesson(LessonDto lesson) {

        //If lesson with same date exist, dont save it
        if (lessonRepository.findAllByDateAndSubjectId(lesson.getDate(), lesson.getSubjectId()) != null) {
            return null;
        }

        return lessonRepository.save(lessonDtoToLesson.convert(lesson));
    }

    @Override
    public Boolean deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Lesson not found"));


        //"Deleting" lesson from class
        Class schoolClass = classRepository
                .findById(lesson.getSchoolClass().getId()).orElseThrow(() -> new NotFoundException("School Class not found"));
        schoolClass.setLessons(lessonRepository
                .findAllBySubjectId(lesson.getSubject().getId())
                .stream()
                .filter(l -> !l.equals(lesson))
                .collect(Collectors.toSet()));
        classRepository.save(schoolClass);

        //"deleting" lesson from subjects
        List<Subject> subjects = subjectRepository.findAllByLessonsIn(Collections.singleton(lesson));
        subjects.forEach(subject -> subject.setLessons(lessonRepository
                .findAllBySubjectId(lesson.getSubject().getId())
                .stream()
                .filter(l -> !l.equals(lesson))
                .collect(Collectors.toSet())));
        subjectRepository.saveAll(subjects);

        //null in attendances
        List<Attendance> attendances = new ArrayList<>(attendanceRepository.findAllByLesson_Id(lessonId));
        attendances.forEach(attendance -> attendance.setLesson(null));
        attendanceRepository.saveAll(attendances);

        //null in topic
        lesson.setTopic(null);
        lessonRepository.save(lesson);


        List<Grade> grades = gradeRepository.findAllByTeacherIdAndDate(lesson.getSubject().getTeacher().getId(), lesson.getDate());
        grades.forEach(grade -> deleteLessonGrade(grade.getId()));

        lessonRepository.delete(lesson);

        return true;
    }

    private void deleteLessonGrade(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId).orElse(null);

        if (grade != null) {
            grade.setTeacher(null);
            grade.setStudent(null);
            grade.setSubject(null);
            gradeRepository.save(grade);

            gradeRepository.delete(grade);
        }
    }


    @Override
    public List<LessonDto> listLessons(Integer page, Integer size, Long teacherId, Long subjectId) {
        if (page < 0) {
            return null;
        }

        Pageable pageable = PageRequest.of(page, size);

        Teacher teacher = getTeacherById(teacherId);

        Subject teacherSubject = teacher.getSubjects().stream()
                .filter(subject -> subject.getId().equals(subjectId))
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("No such subject for teacher"));


        return lessonRepository.findAllBySubjectIdOrderByDateDesc(teacherSubject.getId(), pageable)
                .stream()
                .map(lessonToLessonDto::convert)
                .collect(Collectors.toList());
    }


    @Override
    public List<LessonDto> listLessons(Long teacherId, Long subjectId, Long classId) {

        Teacher teacher = getTeacherById(teacherId);

        checkIfTeacherHasSubject(subjectId, teacher);

        checkIfSubjectHasClass(classId, teacher);

        return lessonRepository.findAllBySubjectIdAndSchoolClassId(subjectId, classId).stream()
                .map(lessonToLessonDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDto getLesson(Long lessonId) {
        return lessonToLessonDto.convert(lessonRepository
                .findById(lessonId).orElseThrow(() -> new NotFoundException("Lesson not found")));
    }

    @Override
    public TopicDto initNewTopic(Long teacherId, Long subjectId) {
        Subject subject = getSubjectById(subjectId);

        final Topic topic = new Topic();
        topic.setSubject(subject);

        Optional<Topic> optionalTopic = topicRepository.findBySubjectOrderByNumberDesc(subject);
        if (optionalTopic.isPresent()) {
            topic.setNumber(optionalTopic.get().getNumber() + 1);
        }

        return topicToTopicDto.convert(topic);
    }

    @Override
    public Topic updateTopic(TopicDto topicDto) {
        return topicRepository.save(topicDtoToTopic.convert(topicDto));
    }

    @Override
    public Topic saveTopic(TopicDto topicDto) {
        return topicRepository.save(topicDtoToTopic.convertForSave(topicDto));
    }

    @Override
    @Transactional
    public Boolean deleteTopic(Long teacherId, Long subjectId, Long topicId) {

        Optional<Topic> topicOptional = topicRepository.findById(topicId);

        if (!topicOptional.isPresent()) {
            return false;
        }

        Topic topic = topicOptional.get();
        if (topic.getSubject().getId().equals(subjectId) && topic.getSubject().getTeacher().getId().equals(teacherId)) {

            // Nullable in Lessons
            List<Lesson> lessons = lessonRepository.findAllByTopic(topic);
            lessons.forEach(lesson -> lesson.setTopic(null));
            lessonRepository.saveAll(lessons);

            topicRepository.delete(topic);
            return true;
        }

        return false;
    }

    @Override
    public List<TopicDto> listTopics(Long teacherId, Long subjectId) {
        Subject subject = getSubjectById(subjectId);

        return topicRepository.findAllBySubjectOrderByNumber(subject)
                .stream()
                .map(topicToTopicDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public TopicDto updatePatchTopic(TopicDto topicUpdated) {
        Optional<Topic> topicOptional = topicRepository.findById(topicUpdated.getId());
        if (!topicOptional.isPresent()) {
            throw new NotFoundException("Topic Not Found.");
        }

        TopicDto topic = topicToTopicDto.convert(topicOptional.get());

        if (topicUpdated.getNumber() != null) {
            topic.setNumber(topicUpdated.getNumber());
        }

        if (topicUpdated.getName() != null) {
            topic.setName(topicUpdated.getName());
        }

        if (topicUpdated.getDescription() != null) {
            topic.setDescription(topicUpdated.getDescription());
        }


        Topic savedTopic = topicRepository.save(topicDtoToTopic.convert(topic));

        return topicToTopicDto.convert(savedTopic);
    }

    @Override
    public SubjectDto getSubjectById(Long teacherId, Long subjectId) {
        Teacher teacher = getTeacherById(teacherId);
        Subject subject = getSubjectById(subjectId);

        if (subject.getTeacher().getId() == teacher.getId()) {
            return subjectToSubjectDto.convert(subject);
        } else {
            throw new NoAccessException("Teacher -> Subject");
        }
    }

    @Override
    public Subject saveOrUpdateSubject(SubjectDto subject) {
        return subjectRepository.save(subjectDtoToSubject.convert(subject));
    }

    @Override
    @Transactional
    public Boolean deleteSubject(Long teacherId, Long subjectId) {
        Optional<Subject> subjectOptional = subjectRepository.findById(subjectId);
        if (!subjectOptional.isPresent()) {
            return false;
        }
        Subject subject = subjectOptional.get();

        if (!subject.getTeacher().getId().equals(teacherId)) {
            return false;
        } else {
            // Nullable in Topics
            List<Topic> topics = topicRepository.findAllBySubjectOrderByNumber(subject);
            topics.forEach(topic -> topic.setSubject(null));
            topicRepository.saveAll(topics);

            // Nullable in Grades
            List<Grade> grades = gradeRepository.findAllBySubject(subject);
            grades.forEach(grade -> grade.setSubject(null));
            gradeRepository.saveAll(grades);

            // Nullable in SchoolPeriods
            List<SchoolPeriod> schoolPeriods = schoolPeriodRepository.findAllBySubject(subject);
            schoolPeriods.forEach(schoolPeriod -> schoolPeriod.setSubject(null));
            schoolPeriodRepository.saveAll(schoolPeriods);

            // Nullable in Lessons
            List<Lesson> lessons = lessonRepository.findAllBySubject(subject);
            lessons.forEach(lesson -> lesson.setSubject(null));
            lessonRepository.saveAll(lessons);

            subjectRepository.delete(subject);
            return true;
        }
    }

    @Override
    public List<SubjectDto> listSubjects(Long teacherId) {

        Teacher teacher = getTeacherById(teacherId);

        return subjectRepository.findAllByTeacher(teacher).stream()
                .map(subjectToSubjectDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public SubjectDto initNewSubject(Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);
        return subjectToSubjectDto.convert(Subject.builder().teacher(teacher).build());
    }

    @Override
    public SubjectDto updatePatchSubject(SubjectDto subjectUpdated) {
        Optional<Subject> subjectOptional = subjectRepository.findById(subjectUpdated.getId());
        if (!subjectOptional.isPresent()) {
            throw new NotFoundException("Subject Not Found.");
        }

        SubjectDto subject = subjectToSubjectDto.convert(subjectOptional.get());

        if (subjectUpdated.getName() != null) {
            subject.setName(subjectUpdated.getName());
        }

        if (subjectUpdated.getClassId() != subject.getClassId()) {
            subject.setClassId(subjectUpdated.getClassId());
        }

        Subject savedSubject = subjectRepository.save(subjectDtoToSubject.convert(subject));

        return subjectToSubjectDto.convert(savedSubject);
    }

    @Override
    public Attendance saveAttendance(AttendanceDto attendance) {

        Attendance existingAtt = attendanceRepository.findDistinctByStudentIdAndLessonId(attendance.getStudentId(),
                attendance.getLessonId());

        if (existingAtt != null) {

            //if clicked same again, delete attendance
            if (attendance.getStatus().equals(existingAtt.getStatus())) {
                deleteLessonAttendance(existingAtt);
            }

            if (!existingAtt.getStatus().equals(Attendance.Status.EXCUSED)
                    && !existingAtt.getStatus().equals(Attendance.Status.UNEXCUSED)) {
                existingAtt.setStatus(attendance.getStatus());
            }

            return attendanceRepository.save(existingAtt);
        }

        return attendanceRepository.save(attendanceDtoToAttendance.convert(attendance));
    }

    @Override
    public List<Attendance> saveAttendancesForClass(AttendanceDto attendanceDto, Long classId, Long teacherId) {
        Class schoolClass = getClassById(classId);
        Lesson lesson = lessonRepository.findById(attendanceDto.getLessonId()).orElse(null);

        if (lesson == null) {
            return null;
        }


        List<Student> students = new ArrayList<>(schoolClass.getStudents());

        for (Student s : students) {
            Attendance attendanceToSave = attendanceRepository.findByStudentIdAndLessonId(s.getId(), lesson.getId());

            if (attendanceToSave == null) {
                attendanceToSave = new Attendance();
                attendanceToSave.setStudent(s);
                attendanceToSave.setLesson(lessonRepository.findById(attendanceDto.getLessonId()).orElse(null));
                attendanceToSave.setStatus(attendanceDto.getStatus());

            }

//            Unexcused and excused attendances stay same
            Attendance attendance = attendanceRepository.findByStudentIdAndLessonId(s.getId(), lesson.getId());
            if ((attendance != null && attendance.getStatus() != null) && (attendance.getStatus() != Attendance.Status.UNEXCUSED)
                    && (attendance.getStatus() != Attendance.Status.EXCUSED)) {
                attendanceToSave.setStatus(attendanceDto.getStatus());
            }

            attendanceRepository.save(attendanceToSave);
        }

        return new ArrayList<>(attendanceRepository.findAllByLesson_Id(attendanceDto.getLessonId()));
    }

    private void deleteLessonAttendance(Attendance existingAtt) {

        if (existingAtt != null && !existingAtt.getStatus().equals(Attendance.Status.EXCUSED)
                && !existingAtt.getStatus().equals(Attendance.Status.UNEXCUSED)) {

            existingAtt.setExtenuations(null);
            existingAtt.setLesson(null);
            existingAtt.setStudent(null);
            attendanceRepository.save(existingAtt);

            attendanceRepository.delete(existingAtt);
        }
    }


    @Override
    public List<AttendanceDto> listAttendances(Long teacherId, Long subjectId, Long classId, Long lessonId) {
        Teacher teacher = getTeacherById(teacherId);

        teacher.getSubjects().stream()
                .filter(s -> s.getSchoolClass().getLessons()
                        .stream()
                        .anyMatch(lesson -> lesson.getId().equals(lessonId)))
                .findAny().orElseThrow(() -> new AccessDeniedException("Class -> Lesson"));

        return attendanceRepository.findAllByLesson_Id(lessonId).stream()
                .map(attendanceToAttendanceDto::convert)
                .sorted(Comparator.comparingLong(AttendanceDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDto> listAttendancesByStudent(Long studentId) {
        return attendanceRepository.findAllByStudentId(studentId)
                .stream()
                .map(attendanceToAttendanceDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceDto initNewLessonAttendance(Long teacherId, Long subjectId, Long lessonId) {
        return attendanceToAttendanceDto.convert(Attendance.builder()
                .lesson(lessonRepository
                        .findById(lessonId).orElseThrow(() -> new NotFoundException("Lesson not found")))
                .build());
    }


    @Override
    public Boolean deleteGrade(Long studentId, Long gradeId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Student not found"));
        Grade grade = gradeRepository.findById(gradeId).orElse(null);

        if (grade != null && grade.getStudent().equals(student)) {

            grade.setTeacher(null);
            grade.setStudent(null);
            grade.setSubject(null);
            gradeRepository.save(grade);

            gradeRepository.delete(grade);

            return true;
        }

        return false;
    }

    @Override
    public List<GradeDto> listGradesBySubject(Long teacherId, Long subjectId) {

        Teacher teacher = getTeacherById(teacherId);

        Subject subject = getSubjectById(subjectId);

        try {
            return gradeRepository.findAllBySubjectIdAndTeacherId(subject.getId(), teacher.getId()).stream()
                    .map(gradeToGradeDto::convert)
                    .sorted(Comparator.comparingLong(GradeDto::getId))
                    .collect(Collectors.toList());
        } catch (NullPointerException ex) {
            return null;
        }
    }

    @Override
    public GradeDto updatePutGrade(GradeDto gradeDto) {
        Grade grade = gradeDtoToGrade.convert(gradeDto);

        if (grade != null) {
            return gradeToGradeDto.convert(gradeRepository.save(grade));
        }

        return null;
    }

    @Override
    public GradeDto updatePatchGrade(GradeDto gradeDto) {

        GradeDto grade = gradeToGradeDto.convert(gradeRepository
                .findById(gradeDto.getId()).orElseThrow(() -> new NotFoundException("Grade not found")));


        if (gradeDto.getDescription() != null) {
            grade.setDescription(gradeDto.getDescription());
        }

        if (gradeDto.getValue() != null) {
            grade.setValue(gradeDto.getValue());
        }

        if (gradeDto.getWeight() != null) {
            grade.setWeight(gradeDto.getWeight());
        }

        return gradeToGradeDto.convert(gradeRepository.save(gradeDtoToGrade.convert(grade)));
    }


    @Override
    public Grade saveOrUpdateGrade(GradeDto grade) {

        if (grade.getId() != null) {
            Grade gradeToUpdate = gradeRepository.findById(grade.getId()).orElse(null);

            if (gradeToUpdate != null) {
                gradeToUpdate.setWeight(grade.getWeight());
                gradeToUpdate.setValue(grade.getValue());
                gradeToUpdate.setDescription(grade.getDescription());
                return gradeRepository.save(gradeToUpdate);
            }
        }

        return gradeRepository.save(gradeDtoToGrade.convert(grade));
    }

    @Override
    public Grade saveOrUpdateFinalGrade(GradeDto grade) {

        if (grade.getWeight() != null && !grade.getWeight().equals(GradeWeight.FINAL_GRADE.getWeight())) {
            grade.setWeight(GradeWeight.FINAL_GRADE.getWeight());
        }

        if (grade.getId() != null) {
            Grade gradeToUpdate = gradeRepository.findById(grade.getId()).orElse(null);
            if (gradeToUpdate != null) {
                gradeToUpdate.setValue(grade.getValue());
                gradeToUpdate.setDescription(grade.getDescription());
            }
        }

        return saveOrUpdateGrade(grade);
    }

    @Override
    public GradeDto initNewGrade(Long teacherId, Long subjectId) {

        Grade grade = Grade.builder()
                .teacher(getTeacherById(teacherId))
                .subject(getSubjectById(subjectId))
                .date(new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()))
                .build();

        return gradeToGradeDto.convert(grade);
    }

    @Override
    public GradeDto initNewFinalGrade(Long teacherId, Long subjectId) {

        Subject subject = getSubjectById(subjectId);


        Grade grade = Grade.builder()
                .teacher(getTeacherById(teacherId))
                .subject(subject)
                .date(new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()))
                .description("Ocena końcowa z przedmiotu: " + subject.getName())
                .weight(GradeWeight.FINAL_GRADE.getWeight())
                .build();

        return gradeToGradeDto.convert(grade);
    }

    @Override
    public GradeDto initNewLessonGrade(Long teacherId, Long subjectId, Long lessonId) {
        return gradeToGradeDto.convert(Grade.builder()
                .teacher(getTeacherById(teacherId))
                .subject(getSubjectById(subjectId))
                .date(lessonRepository
                        .findById(lessonId).orElseThrow(() -> new NotFoundException("Lesson not found")).getDate())
                .build());
    }

    @Override
    public Event saveOrUpdateEvent(EventDto eventDto) {

        Teacher teacher = getTeacherById(eventDto.getTeacherId());
        Event event = eventDtoToEvent.convert(eventDto);

        if (!event.getTeacher().equals(teacher)) {
            return null;
        }

        if (eventDto.getId() == null) {
            return eventRepository.save(eventDtoToEvent.convert(eventDto));
        }

        return eventDtoToEvent.convert(updatePatchEvent(eventDto));

    }

    @Override
    public EventDto initNewEvent(Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);

        Event event = Event.builder()
                .teacher(getTeacherById(teacherId))
                .createDate(new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()))
                .build();

        return eventToEventDto.convert(event);
    }

    @Override
    public EventDto initNewClassEvent(Long teacherId, Long subjectId) {
        Teacher teacher = getTeacherById(teacherId);
        Subject subject = getSubjectById(subjectId);

        checkIfTeacherHasSubject(subjectId, teacher);


        Event event = Event.builder()
                .teacher(getTeacherById(teacherId))
                .createDate(new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()))
                .schoolClass(subject.getSchoolClass())
                .build();

        return eventToEventDto.convert(event);
    }

    @Override
    public Boolean deleteEvent(Long teacherId, Long eventId) {
        Teacher teacher = getTeacherById(teacherId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getTeacher().getId().equals(teacher.getId())) {
            return false;
        }

        event.setSchoolClass(null);
        event.setTeacher(null);

        eventRepository.save(event);

        eventRepository.delete(event);

        return true;
    }

    @Override
    public List<EventDto> listEvents(Long teacherId, Integer page, Integer size, Boolean includeHistory) {
        if (page < 0) {
            return null;
        }

        Teacher teacher = getTeacherById(teacherId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("date"));

        if (includeHistory) {
            pageable = PageRequest.of(page, size, Sort.by("date").descending());
            return eventRepository.findAllByTeacherIdAndDateBefore(teacher.getId(),
                    new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()), pageable)
                    .stream()
                    .map(eventToEventDto::convert)
                    .collect(Collectors.toList());
        } else {
            return eventRepository.findAllByTeacherIdAndDateAfter(teacher.getId(),
                    new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()), pageable)
                    .stream()
                    .map(eventToEventDto::convert)
                    .collect(Collectors.toList());
        }

    }

    @Override
    public List<EventDto> listClassEvents(Long teacherId, Long subjectId, Integer page, Integer size, Boolean includeHistory) {
        if (page < 0) {
            return null;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("date"));

        Teacher teacher = getTeacherById(teacherId);
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found"));

        if (!subject.getTeacher().equals(teacher)) {
            throw new AccessDeniedException("Teacher -> Subject");
        }

        if (subject.getSchoolClass() == null) {
            return null;
        }

        if (includeHistory) {
            pageable = PageRequest.of(page, size, Sort.by("date").descending());
            return eventRepository.findAllByTeacherIdAndSchoolClassIdAndDateBefore(teacherId, subject.getSchoolClass().getId(),
                    new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()), pageable)
                    .stream()
                    .map(eventToEventDto::convert)
                    .collect(Collectors.toList());
        } else {
            return eventRepository.findAllByTeacherIdAndSchoolClassIdAndDateAfter(teacherId, subject.getSchoolClass().getId(),
                    new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()), pageable)
                    .stream()
                    .map(eventToEventDto::convert)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public EventDto updatePatchEvent(EventDto eventUpdated) {

        Event event = eventRepository.findById(eventUpdated.getId()).orElseThrow(() -> new NotFoundException("Event not found"));

        if (eventUpdated.getDescription() != null) {
            event.setDescription(eventUpdated.getDescription());
        }

        if (eventUpdated.getCreateDate() != null) {
            event.setCreateDate(eventUpdated.getCreateDate());
        }

        if (eventUpdated.getDate() != null) {
            event.setDate(eventUpdated.getDate());
        }

        if (eventUpdated.getType() != null) {
            event.setType(eventUpdated.getType());
        }

        Event savedEvent = eventRepository.save(event);

        return eventToEventDto.convert(savedEvent);
    }

    @Override
    public EventDto getEvent(Long teacherId, Long eventId) {

        Teacher teacher = getTeacherById(teacherId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getTeacher().getId().equals(teacher.getId())) {
            return null;
        }

        return eventToEventDto.convert(event);
    }

    @Override
    public Behavior saveBehavior(BehaviorDto behaviorDto) {
        Behavior behavior = behaviorDtoToBehavior.convert(behaviorDto);
        behavior.setDate(new Date());
        return behaviorRepository.save(behavior);
    }

    @Override
    public List<BehaviorDto> listBehaviors(Long teacherId, Integer page, Integer size) {
        if (page < 0) {
            return null;
        }

        Teacher teacher = getTeacherById(teacherId);

        Pageable pageable = PageRequest.of(page, size);

        return behaviorRepository.findAllByTeacherOrderByDateDesc(teacher, pageable)
                .stream()
                .map(behaviorToBehaviorDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<BehaviorDto> listBehaviors(Long teacherId, Long studentId) {
        return null;
    }

    @Override
    public BehaviorDto initNewBehavior(Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);
        Behavior behavior = Behavior.builder().teacher(teacher).build();

        return behaviorToBehaviorDto.convert(behavior);
    }

    @Override
    public BehaviorDto initNewBehavior(Long teacherId, Long studentId) {

        Teacher teacher = getTeacherById(teacherId);
        Student student = getStudentById(studentId);
        Behavior behavior = Behavior.builder().teacher(teacher).student(student).build();

        return behaviorToBehaviorDto.convert(behavior);
    }

    @Override
    public Boolean deleteBehavior(Long teacherId, Long behaviorId) {
        Optional<Behavior> behaviorOptional = behaviorRepository.findById(behaviorId);
        if (!behaviorOptional.isPresent()) {
            return false;
        }
        Behavior behavior = behaviorOptional.get();

        if (!behavior.getTeacher().getId().equals(teacherId)) {
            return false;
        } else {
            behaviorRepository.delete(behavior);
            return true;
        }
    }

    @Override
    public BehaviorDto updatePutBehavior(BehaviorDto behaviorDto) {
        Behavior behavior = behaviorDtoToBehavior.convert(behaviorDto);
        Behavior savedBehavior = behaviorRepository.save(behavior);

        return behaviorToBehaviorDto.convert(savedBehavior);
    }

    @Override
    public BehaviorDto updatePatchBehavior(BehaviorDto behaviorUpdated) {
        Optional<Behavior> behaviorOptional = behaviorRepository.findById(behaviorUpdated.getId());
        if (!behaviorOptional.isPresent()) {
            throw new NotFoundException("Behavior Not Found.");
        }

        BehaviorDto behavior = behaviorToBehaviorDto.convert(behaviorOptional.get());

        if (behaviorUpdated.getDate() != null) {
            behavior.setDate(behaviorUpdated.getDate());
        }

        if (behaviorUpdated.getContent() != null) {
            behavior.setContent(behaviorUpdated.getContent());
        }

        if (behaviorUpdated.isPositive() == !behavior.isPositive()) {
            behavior.setPositive(behaviorUpdated.isPositive());
        }

        Behavior savedBehavior = behaviorRepository.save(behaviorDtoToBehavior.convert(behavior));

        return behaviorToBehaviorDto.convert(savedBehavior);
    }

    @Override
    public List<SchoolClassDto> listAllClasses() {
        return classRepository.findAll().stream()
                .map(classToClassDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<SchoolClassDto> listClassesByTeacherAndSubject(Long teacherId, Long subjectId) {
        Teacher teacher = getTeacherById(teacherId);
        checkIfTeacherHasSubject(subjectId, teacher);


        return classRepository.findAllBySubjects(getSubjectById(subjectId)).stream()
                .map(classToClassDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public SchoolClassDto getSchoolClassByTeacherAndSubject(Long classId, Long subjectId, Long teacherId) {

        Teacher teacher = getTeacherById(teacherId);

        checkIfTeacherHasSubject(subjectId, teacher);

        checkIfSubjectHasClass(classId, teacher);


        return classToClassDto.convert(classRepository
                .findById(classId).orElseThrow(() -> new NotFoundException("Class not found")));
    }

    @Override
    public Boolean subjectHasSchoolClass(Long subjectId, Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);
        Subject subject = getSubjectById(subjectId);

        return subject.getSchoolClass() != null;
    }

    @Override
    public List<SchoolClassDto> listClassByTeacher(Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);


        return classRepository.findAllBySubjectsIn(teacher.getSubjects())
                .stream()
                .distinct()
                .map(classToClassDto::convert)
                .collect(Collectors.toList());

    }

    @Override
    public SchoolClassDto getSchoolClass(Long classId) {
        return classToClassDto.convert(classRepository
                .findById(classId).orElseThrow(() -> new NotFoundException("School class not found")));
    }

    @Override
    public List<ExtenuationDto> listExtenuations(Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);

        List<Extenuation> extenuations = new ArrayList<>();

        if (teacher.getSchoolClass() == null) {
            return null;
        }

        Set<Parent> parents = new HashSet<>(parentRepository.findAllByStudentsIn(teacher.getSchoolClass().getStudents()));

        parents.forEach(parent -> extenuations
                .addAll(extenuationRepository.findAllByParentIdAndStatus(parent.getId(), Extenuation.Status.SENT)));

        return extenuations
                .stream()
                .map(extenuationToExtenuationDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean acceptExtenuation(Long extenuationId) {
        Extenuation extenuation = extenuationRepository
                .findById(extenuationId).orElseThrow(() -> new NotFoundException("Extenuation not found"));

        extenuation.getAttendances().forEach(attendance -> attendance.setStatus(Attendance.Status.EXCUSED));
        extenuation.setStatus(Extenuation.Status.ACCEPT);

        extenuationRepository.save(extenuation);

        return true;
    }

    @Override
    public Boolean rejectExtenuation(Long extenuationId) {
        Extenuation extenuation = extenuationRepository
                .findById(extenuationId).orElseThrow(() -> new NotFoundException("Extenuation not found"));

        extenuation.getAttendances().forEach(attendance -> attendance.setStatus(Attendance.Status.UNEXCUSED));
        extenuation.setStatus(Extenuation.Status.REJECT);

        extenuationRepository.save(extenuation);

        return true;
    }

    @Override
    public List<StudentDto> listClassStudents(Long teacherId, Long classId) {
        Teacher teacher = getTeacherById(teacherId);
        Class schoolClass = getClassById(classId);


        return studentRepository.findAllBySchoolClassIdOrderByUserLastName(schoolClass.getId())
                .stream()
                .map(studentToStudentDto::convert)
                .collect(Collectors.toList());

    }

    @Override
    public StudentDto getStudent(Long studentId) {
        return studentToStudentDto.convert(getStudentById(studentId));
    }

    @Override
    public Boolean isFormTutor(Long teacherId, Long classId) {
        Teacher teacher = getTeacherById(teacherId);

        if (teacher.getSchoolClass() == null) {
            return false;
        }

        return teacher.getSchoolClass().getId().equals(classId);
    }

    private Teacher getTeacherById(Long teacherId) {
        Optional<Teacher> teacherOptional = teacherRepository.findById(teacherId);

        if (!teacherOptional.isPresent()) {
            throw new NotFoundException("Teacher Not Found.");
        }

        return teacherOptional.get();
    }

    @Override
    public Boolean excuseAttendances(List<Long> ids, Long teacherId, Long studentId, Long classId) {
        Teacher teacher = getTeacherById(teacherId);
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Student not found"));

        if (student.getSchoolClass().equals(teacher.getSchoolClass())) {
            List<Attendance> attendances = attendanceRepository.findAllById(ids);
            attendances.forEach(attendance -> attendance.setStatus(Attendance.Status.EXCUSED));
            attendanceRepository.saveAll(attendances);

            return true;
        }

        return false;
    }

    @Override
    public Timetable getTimetableByTeacherId(Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);

        if (teacher.getSubjects() == null) {
            return null;
        }

        return timetableService.getTimetableByTeacherId(teacher.getId());

    }


    @Override
    public Map<StudentDto, AttendanceDto> listStudentsLessonAttendances(Long teacherId, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Lesson not found"));

        Class schoolClass = lesson.getSchoolClass();

        Map<StudentDto, AttendanceDto> studentAttendancesListMap = new LinkedHashMap<>();

        List<Student> students = studentRepository.findAllBySchoolClassIdOrderByUserLastName(schoolClass.getId());

        students.forEach(student -> {
            studentAttendancesListMap.put(
                    studentToStudentDto.convert(student),
                    attendanceToAttendanceDto.convert(attendanceRepository.findDistinctByStudentIdAndLesson(student.getId(), lesson)));
        });


        if (studentAttendancesListMap.keySet().isEmpty()) {
            return null;
        }

        return studentAttendancesListMap;
    }

    @Override
    public Map<StudentDto, List<GradeDto>> listStudentsLessonGrades(Long teacherId, Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Lesson not found"));

        Class schoolClass = lesson.getSchoolClass();

        Map<StudentDto, List<GradeDto>> studentGradesListMap =  new LinkedHashMap<>();

        List<Student> students = studentRepository.findAllBySchoolClassIdOrderByUserLastName(schoolClass.getId());

        students.forEach(student -> {
            studentGradesListMap.put(studentToStudentDto.convert(student),
                    gradeRepository.findAllByStudentIdAndDateAndSubjectId(student.getId(), lesson.getDate(), lesson.getSubject().getId())
                            .stream()
                            .map(gradeToGradeDto::convert)
                            .collect(Collectors.toList()));
        });


        if (studentGradesListMap.keySet().isEmpty()) {
            return null;
        }

        return studentGradesListMap;
    }


    @Override
    public Map<StudentDto, List<GradeDto>> listStudentsGrades(Long teacherId, Long subjectId) {

        Teacher teacher = getTeacherById(teacherId);
        Subject subject = getSubjectById(subjectId);

        Class schoolClass = subject.getSchoolClass();

        checkIfTeacherHasSubject(subjectId, teacher);

        Map<StudentDto, List<GradeDto>> studentGradesListMap = new LinkedHashMap<>();

        List<Student> students = studentRepository.findAllBySchoolClassIdOrderByUserLastName(schoolClass.getId());

        students.forEach(student -> {
            studentGradesListMap.put(studentToStudentDto.convert(student),
                    gradeRepository.findAllByTeacherIdAndSubjectIdAndStudentIdAndWeightNotIn(teacherId, subjectId,
                            student.getId(), Arrays.asList(GradeWeight.FINAL_GRADE.getWeight(), GradeWeight.BEHAVIOR_GRADE.getWeight()))
                            .stream()
                            .map(gradeToGradeDto::convert)
                            .collect(Collectors.toList()));
        });


        if (studentGradesListMap.keySet().isEmpty()) {
            return null;
        }

        return studentGradesListMap;
    }

    @Override
    public Map<Long, GradeDto> listStudentsFinalGrades(Long teacherId, Long subjectId) {
        Teacher teacher = getTeacherById(teacherId);
        Subject subject = getSubjectById(subjectId);

        Class schoolClass = subject.getSchoolClass();

        checkIfTeacherHasSubject(subjectId, teacher);

        Map<Long, GradeDto> studentFinalGradesListMap = new LinkedHashMap<>();

        List<Student> students = studentRepository.findAllBySchoolClassIdOrderByUserLastName(schoolClass.getId());

        students.forEach(student -> {
            studentFinalGradesListMap.put(student.getId(),
                    gradeToGradeDto.convert(gradeRepository
                            .findByTeacherIdAndSubjectIdAndStudentIdAndWeight(teacherId, subjectId, student.getId(),
                                    GradeWeight.FINAL_GRADE.getWeight())));
        });


        if (studentFinalGradesListMap.keySet().isEmpty()) {
            return null;
        }

        return studentFinalGradesListMap;

    }

    @Override
    public List<Long> maxGradesCount(Long teacherId, Long lessonId) {
        Long[] maxCount = {Long.MIN_VALUE};

        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new NotFoundException("Lesson not found"));

        Class schoolClass = lesson.getSchoolClass();

        schoolClass.getStudents().forEach(student -> {
            Long count = gradeRepository.countAllByStudentIdAndDate(student.getId(), lesson.getDate());
            if (count > maxCount[0])
                maxCount[0] = count;
        });

        return new ArrayList<>() {{
            for (int i = 0; i < maxCount[0]; i++) {
                add(null);
            }
        }};
    }

    @Override
    public List<Long> maxGradesCountBySubject(Long teacherId, Long subjectId) {
        Long[] maxCount = {Long.MIN_VALUE};

        Subject subject = getSubjectById(subjectId);

        Class schoolClass = subject.getSchoolClass();

        schoolClass.getStudents().forEach(student -> {
            Long count = gradeRepository.countAllByTeacherIdAndSubjectIdAndStudentId(teacherId, subjectId, student.getId());
            if (count > maxCount[0])
                maxCount[0] = count;
        });

        return new ArrayList<>() {{
            for (int i = 0; i < maxCount[0]; i++) {
                add(null);
            }
        }};
    }

    @Override
    public List<EndYearReportDto> listEndYearReports(Long teacherId) {
        Teacher teacher = getTeacherById(teacherId);

        return teacher.getEndYearReports()
                .stream()
                .map(endYearReportToEndYearReportDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public boolean getEndYearReportPdf(HttpServletResponse response, Long teacherId, Long reportId) throws IOException {

        Teacher teacher = getTeacherById(teacherId);

        Optional<EndYearReport> reportOptional = endYearReportRepository.findByIdAndTeacher(reportId, teacher);
        if (!reportOptional.isPresent()) {
            return false;
        }

        EndYearReport report = reportOptional.get();

        OutputStream out = response.getOutputStream();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + teacher.getUser().getFirstName() + "_" + report.getYear() + ".pdf";
        response.setHeader(headerKey, headerValue);


        out.write(report.getEndYearPdf());
        out.close();

        return true;
    }

    private Subject getSubjectById(Long subjectId) {
        return subjectRepository
                .findById(subjectId).orElseThrow(() -> new NotFoundException("Subject not found"));
    }

    private Class getClassById(Long classId) {
        return classRepository
                .findById(classId).orElseThrow(() -> new NotFoundException("Class not found"));
    }

    private Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElseThrow(() -> new NotFoundException("Student not found"));
    }


    private void checkIfSubjectHasClass(Long classId, Teacher teacher) {
        teacher.getSubjects().stream()
                .filter(s -> s.getSchoolClass().getId().equals(classId))
                .findAny().orElseThrow(() -> new AccessDeniedException("Subject -> Class"));
    }

    private void checkIfTeacherHasSubject(Long subjectId, Teacher teacher) {
        teacher.getSubjects().stream()
                .filter(s -> s.getId().equals(subjectId))
                .findAny().orElseThrow(() -> new AccessDeniedException("Teacher -> Subject"));
    }


}
