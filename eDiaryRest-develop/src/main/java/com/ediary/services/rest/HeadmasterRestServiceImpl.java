package com.ediary.services.rest;

import com.ediary.DTO.EndYearReportDtoRequest;
import com.ediary.DTO.EndYearReportDtoShort;
import com.ediary.DTO.TeacherDto;
import com.ediary.DTO.TeacherReportDtoRequest;
import com.ediary.converters.EndYearReportToEndYearReportDto;
import com.ediary.converters.TeacherToTeacherDto;
import com.ediary.domain.Class;
import com.ediary.domain.*;
import com.ediary.domain.helpers.GradeWeight;
import com.ediary.domain.helpers.Report;
import com.ediary.domain.helpers.TimeInterval;
import com.ediary.exceptions.NotFoundException;
import com.ediary.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class HeadmasterRestServiceImpl implements HeadmasterRestService {

    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;
    private final GradeRepository gradeRepository;
    private final EventRepository eventRepository;
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ExtenuationRepository extenuationRepository;
    private final EndYearReportRepository endYearReportRepository;
    private final SchoolPeriodRepository schoolPeriodRepository;
    private final StudentCouncilRepository studentCouncilRepository;
    private final ParentCouncilRepository parentCouncilRepository;
    private final TopicRepository topicRepository;
    private final ParentRepository parentRepository;
    private final AttendanceRepository attendanceRepository;
    private final BehaviorRepository behaviorRepository;
    private final NoticeRepository noticeRepository;

    private final TeacherToTeacherDto teacherToTeacherDto;
    private final EndYearReportToEndYearReportDto endYearReportToEndYearReportDto;

    private final PdfRestService pdfRestService;

    @Override
    public List<TeacherDto> listAllTeachers(Pageable pageable) {
        return teacherRepository.findAll(pageable).stream()
                .map(teacherToTeacherDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> createTeacherReports(TeacherReportDtoRequest teacherReportDtoRequest) {
        if (!teacherReportDtoRequest.getTeachersId().isEmpty()) {
            List<Report> reports = new ArrayList<>();
            TimeInterval timeInterval = new TimeInterval(teacherReportDtoRequest.getStartTime(), teacherReportDtoRequest.getEndTime());

            teacherReportDtoRequest.getTeachersId().forEach(
                    teacherId -> reports.add(createTeacherReport(teacherId, timeInterval)));

            return reports;
        }

        throw new IllegalStateException("Cannot generate reports");
    }

    private Report createTeacherReport(Long teacherId, TimeInterval timeInterval)  {
        validateTimeInterval(timeInterval);

        Teacher teacher = teacherRepository
                .findById(teacherId).orElseThrow(() -> new NotFoundException("Teacher not found"));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        String timeIntervalAsString = simpleDateFormat.format(
                timeInterval.getStartTime()) + " - " + simpleDateFormat.format(timeInterval.getEndTime());

        LocalDate localStartTime = timeInterval.getStartTime().toLocalDate();

        java.util.Date startOfDayDate;

        Date correctedEndTime = Date.valueOf(timeInterval.getEndTime().toLocalDate().plusDays(1));
        startOfDayDate = new java.util.Date(Timestamp.valueOf(LocalDateTime.of(localStartTime, LocalTime.MIDNIGHT)).getTime());

        try {
            byte[] reportPdf = pdfRestService.createReportPdf(teacher, timeIntervalAsString,
                    getTeacherLessonsNumber(teacher, startOfDayDate, correctedEndTime).intValue(),
                    getTeacherSubjectsNames(teacher), getTeacherGradesNumber(teacher, startOfDayDate, correctedEndTime),
                    getTeacherEventsNumber(teacher, startOfDayDate, correctedEndTime));

            return new Report(teacher.getUser().getLastName() + "_report_" + timeIntervalAsString, reportPdf);
        } catch (Exception e) {
            log.error("Error while creating report: " + teacherId);

            return null;
        }
    }

    private void createEndYearReportStudent(Student student, Integer year) {
        byte[] endYearReportInBytes = pdfRestService.createEndYearReportStudent(listSubjectsGrades(student.getId()),
                listSubjectsFinalGrades(student.getId()),
                student, getAttendancesNumber(student.getId()),
                getBehaviorGrade(student.getId()), year);

        EndYearReport endYearReport = EndYearReport.builder()
                .endYearPdf(endYearReportInBytes)
                .userType(EndYearReport.Type.STUDENT)
                .year(year.toString())
                .student(student)
                .build();

        if (endYearReportInBytes.length != 0) {
            endYearReportRepository.save(endYearReport);
        }
    }


    private void createEndYearReportTeacher(Teacher teacher, Integer year) {
        byte[] endYearReportInBytes = pdfRestService.createEndYearReportTeacher(
                listSubjectsStudentsWithGrades(teacher),
                listSubjectsStudentsWithFinalGrade(teacher),
                teacher, year);

        EndYearReport endYearReport = EndYearReport.builder()
                .endYearPdf(endYearReportInBytes)
                .userType(EndYearReport.Type.TEACHER)
                .year(year.toString())
                .teacher(teacher)
                .build();

        if (endYearReportInBytes.length != 0) {
            endYearReportRepository.save(endYearReport);
        }
    }

    private void validateTimeInterval(TimeInterval timeInterval) {
        if (timeInterval.getStartTime().toLocalDate().isAfter(timeInterval.getEndTime().toLocalDate())) {
            throw new IllegalStateException("Invalid time interval: " + timeInterval.getStartTime() + " is after " + timeInterval.getEndTime());
        }
    }

    private Map<Subject, Map<Student, List<Grade>>> listSubjectsStudentsWithGrades(Teacher teacher) {

        if (teacher == null) {
            return null;
        }

        Map<Subject, Map<Student, List<Grade>>> subjectsStudentWithGrades = new LinkedHashMap<>();

        if (teacher.getSubjects() != null) {

            List<Subject> subjects = subjectRepository.findAllByTeacherIdOrderByName(teacher.getId());

            subjects.forEach(subject -> {
                Map<Student, List<Grade>> studentsWithGrades = new LinkedHashMap<>();

                List<Student> students = studentRepository.findAllBySchoolClassIdOrderByUserLastName(subject.getSchoolClass().getId());

                students.forEach(student -> {
                    studentsWithGrades.put(student, gradeRepository.findAllByTeacherIdAndSubjectIdAndStudentIdAndWeightNotIn(
                            teacher.getId(), subject.getId(), student.getId(),
                            Arrays.asList(GradeWeight.BEHAVIOR_GRADE.getWeight(), GradeWeight.FINAL_GRADE.getWeight())
                    ));

                    subjectsStudentWithGrades.put(subject, studentsWithGrades);
                });
            });
            return subjectsStudentWithGrades;
        }

        return null;
    }


    private Map<Long, Map<Student, Grade>> listSubjectsStudentsWithFinalGrade(Teacher teacher) {
        if (teacher == null) {
            return null;
        }

        Map<Long, Map<Student, Grade>> subjectsStudentWithFinalGrade = new HashMap<>();

        if (teacher.getSubjects() != null) {
            teacher.getSubjects().forEach(subject -> {
                Map<Student, Grade> studentsWithGrades = new HashMap<>();
                subject.getSchoolClass().getStudents().forEach(student -> {
                    studentsWithGrades.put(student, gradeRepository.findBySubjectIdAndStudentIdAndWeight(
                            subject.getId(), student.getId(),
                            GradeWeight.FINAL_GRADE.getWeight()
                    ));

                    subjectsStudentWithFinalGrade.put(subject.getId(), studentsWithGrades);
                });
            });
            return subjectsStudentWithFinalGrade;
        }

        return null;
    }

    private Map<Subject, List<Grade>> listSubjectsGrades(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student with given id not found:" + studentId));


        Map<Subject, List<Grade>> gradesWithSubjects = new HashMap<>();


        if (student.getSchoolClass() != null) {
            student.getSchoolClass().getSubjects()
                    .forEach(subject -> gradesWithSubjects.put(subject,
                            gradeRepository.findAllByStudentIdAndSubjectId(studentId, subject.getId())));
        }

        if (gradesWithSubjects.keySet().isEmpty()) {
            return null;
        }

        return gradesWithSubjects;
    }


    private Map<Long, Grade> listSubjectsFinalGrades(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student with given id not found:" + studentId));

        Map<Long, Grade> studentFinalGradesListMap = new LinkedHashMap<>();

        if (student.getSchoolClass() != null) {
            student.getSchoolClass().getSubjects()
                    .forEach(subject -> studentFinalGradesListMap.put(subject.getId(),
                            gradeRepository.findBySubjectIdAndStudentIdAndWeight(
                                    subject.getId(), student.getId(), GradeWeight.FINAL_GRADE.getWeight())));
        }

        if (studentFinalGradesListMap.keySet().isEmpty()) {
            return null;
        }

        return studentFinalGradesListMap;
    }

    private Map<String, Long> getAttendancesNumber(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student with given id not found:" + studentId));

        Map<String, Long> attendancesNumber = new HashMap<>();

        List<Attendance> attendanceList = student.getAttendance()
                .stream()
                .filter(attendance -> attendance.getStatus().equals(Attendance.Status.ABSENT)
                        || attendance.getStatus().equals(Attendance.Status.UNEXCUSED)
                        || attendance.getStatus().equals(Attendance.Status.EXCUSED))
                .collect(Collectors.toList());

        Long excusedAttendances = attendanceList
                .stream()
                .filter(attendance -> attendance.getStatus().equals(Attendance.Status.EXCUSED))
                .count();


        attendancesNumber.put("total", (long) attendanceList.size());
        attendancesNumber.put("excused", excusedAttendances);

        return attendancesNumber;
    }


    private String getBehaviorGrade(Long studentId) {
        Grade behaviorGrade = gradeRepository.findByStudentIdAndWeight(studentId, GradeWeight.BEHAVIOR_GRADE.getWeight());
        String behaviorGradeValue = "nie wystawiono";
        if (behaviorGrade != null && behaviorGrade.getValue() != null) {
            behaviorGradeValue = behaviorGrade.getValue();
        }

        return behaviorGradeValue;
    }

    private Long getTeacherLessonsNumber(Teacher teacher, java.util.Date startTime, Date endTime) {
        Long[] sumOfLessons = {0L};

        List<List<Lesson>> lessons = teacher.getSubjects().
                stream()
                .map(subject ->
                        lessonRepository.findAllBySubjectAndDateAfterAndDateBefore(subject, startTime, endTime))
                .collect(Collectors.toList());

        lessons.forEach(lessonList -> sumOfLessons[0] += (long) lessonList.size());

        return sumOfLessons[0];
    }

    private String getTeacherSubjectsNames(Teacher teacher) {

        StringBuilder subjectsNames = new StringBuilder();

        teacher.getSubjects()
                .forEach(subject -> subjectsNames.append(subject.getName()).append(", "));

        if (subjectsNames.length() != 0) {
            subjectsNames.delete(subjectsNames.length() - 2, subjectsNames.length() - 1);
        }

        return subjectsNames.toString();
    }

    private Long getTeacherGradesNumber(Teacher teacher, java.util.Date startTime, Date endTime) {
        return (long) gradeRepository.findAllByTeacherIdAndDateAfterAndDateBefore(teacher.getId(), startTime, endTime).size();
    }

    private Long getTeacherEventsNumber(Teacher teacher, java.util.Date startTime, Date endTime) {
        return (long) eventRepository.findAllByTeacherIdAndDateAfterAndDateBefore(teacher.getId(), startTime, endTime).size();
    }

    @Override
    public Boolean performYearClosing() {

        //GENERATING REPORT
        Integer year =  LocalDate.now().atStartOfDay().getYear();

        //Reports for each teacher
        List<Teacher> teachers = teacherRepository.findAll();
        if (teachers.size() > 0) {
            teachers.forEach(teacher -> createEndYearReportTeacher(teacher, year));
        }

        //Reports for each student
        List<Student> students = studentRepository.findAll();
        if (students.size() > 0) {
            students.forEach(student -> createEndYearReportStudent(student, year));
        }


        //Classes
        List<Class> classes = classRepository.findAll();
        List<Class> classesToSave = new ArrayList<>();

        if (classes.size() > 0) {
            classes.forEach(schoolClass -> {

                //creating new classes with same sklad
                classesToSave.add(Class.builder()
                        .name(changeSchoolClassName(schoolClass.getName()))
                        .students(schoolClass.getStudents())
                        .teacher(schoolClass.getTeacher())
                        .build());

                //deleting class
                deleteClass(schoolClass);

            });
        }

        clearTeacher();
        clearParent();
        clearStudents();

        //StudentCouncil
        studentCouncilRepository.deleteAll();

        //ParentCouncil
        parentCouncilRepository.deleteAll();

        //Events
        List<Event> events = eventRepository.findAll();
        if (!events.isEmpty()) {
            eventRepository.deleteAll(events);
        }

        //Extenuations
        List<Extenuation> extenuations = extenuationRepository.findAll();
        if (!extenuations.isEmpty()) {
            extenuations.forEach(this::deleteExtenuation);
        }

        //Attendance
        List<Attendance> attendances = attendanceRepository.findAll();
        if (!attendances.isEmpty()) {
            attendances.forEach(this::deleteAttendance);
        }

        //Behaviors
        List<Behavior> behaviors = behaviorRepository.findAll();
        if (!behaviors.isEmpty()) {
            behaviorRepository.deleteAll(behaviors);
        }

        //Grade
        List<Grade> grades = gradeRepository.findAll();
        if (!grades.isEmpty()) {
            gradeRepository.deleteAll(grades);
        }

        //Lesson
        List<Lesson> lessons = lessonRepository.findAll();
        if (!lessons.isEmpty()) {
            lessonRepository.deleteAll(lessons);
        }

        //SchoolPerion
        List<SchoolPeriod> schoolPeriods = schoolPeriodRepository.findAll();
        if (!schoolPeriods.isEmpty()) {
            schoolPeriodRepository.deleteAll(schoolPeriods);
        }

        //Topics
        List<Topic> topics = topicRepository.findAll();
        if (!topics.isEmpty()) {
            topics.forEach(this::deleteTopic);
        }

        //Subjects
        List<Subject> subjects = subjectRepository.findAll();
        if (!subjects.isEmpty()) {
            subjects.forEach(this::deleteSubject);
        }

        //Notices
        List<Notice> notices = noticeRepository.findAll();
        if (!notices.isEmpty()) {
            noticeRepository.deleteAll(notices);
        }

        //Saving newly created classes
        classesToSave.forEach(schoolClass -> addStudentToClass(schoolClass, new ArrayList<>(schoolClass.getStudents())));

        classRepository.saveAll(classesToSave);

        return true;
    }

    @Override
    public List<EndYearReportDtoShort> listEndYearStudentsReports(String year, Pageable pageable) {
        return endYearReportRepository.findAllByUserTypeAndYearOrderByIdDesc(EndYearReport.Type.STUDENT, year, pageable)
                .stream()
                .map(endYearReportToEndYearReportDto::convertShort)
                .collect(Collectors.toList());
    }

    @Override
    public List<EndYearReportDtoShort> listEndYearTeachersReports(String year, Pageable pageable) {
        return endYearReportRepository.findAllByUserTypeAndYearOrderByIdDesc(EndYearReport.Type.TEACHER, year,pageable)
                .stream()
                .map(endYearReportToEndYearReportDto::convertShort)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllReportsYears() {
        return endYearReportRepository.findAllYears();
    }

    @Override
    public List<Report> getEndYearReportPdf(EndYearReportDtoRequest endYearReportDtoRequest) {
        if (!endYearReportDtoRequest.getReportIds().isEmpty()) {
            List<Report> reports = new ArrayList<>();
            endYearReportDtoRequest.getReportIds().forEach(reportId -> reports.add(getEndYearReport(reportId)));

            return reports;
        }

        throw new IllegalStateException("Cannot generate reports");
    }

    private Report getEndYearReport(Long reportId) {
        EndYearReport report = endYearReportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report with given id not found:" + reportId));

        String filename = (report.getStudent() != null ?
                report.getStudent().getUser().getLastName() : report.getTeacher().getUser().getLastName()) + "_end_year_report";

        return new Report(filename, report.getEndYearPdf());
    }


    public Boolean deleteClass(Class schoolClass ) {

        //Students - null
        List<Student> students = studentRepository.findAllById(schoolClass.getStudents()
                .stream()
                .map(Student::getId)
                .collect(Collectors.toList()));
        students.forEach(s -> s.setSchoolClass(null));
        studentRepository.saveAll(students);

        //Teacher - null
        Teacher teacher = teacherRepository.findById(schoolClass.getTeacher().getId()).orElse(null);

        if (teacher != null) {
            teacher.setSchoolClass(null);
            teacherRepository.save(teacher);
        }

        //Lessons
        List<Lesson> lessons = lessonRepository.findAllBySchoolClassId(schoolClass.getId());
        lessons.forEach(l -> l.setSchoolClass(null));
        lessonRepository.saveAll(lessons);

        //School periods
        List<SchoolPeriod> schoolPeriods = schoolPeriodRepository.findAllBySchoolClassId(schoolClass.getId());
        schoolPeriods.forEach(s -> s.setSchoolClass(null));
        schoolPeriodRepository.saveAll(schoolPeriods);

        //StudentCouncil - delete
        StudentCouncil studentCouncil = studentCouncilRepository.findBySchoolClassId(schoolClass.getId());
        if (studentCouncil != null) {
            studentCouncil.setStudents(null);
            studentCouncil.setSchoolClass(null);
            studentCouncilRepository.save(studentCouncil);
        }

        //ParentCouncil - delete
        ParentCouncil parentCouncil = parentCouncilRepository.findBySchoolClassId(schoolClass.getId());
        if (parentCouncil != null) {
            parentCouncil.setParents(null);
            parentCouncil.setSchoolClass(null);
            parentCouncilRepository.save(parentCouncil);
        }

        //Events - null
        List<Event> events = eventRepository.findAllBySchoolClassId(schoolClass.getId());
        events.forEach(event -> event.setSchoolClass(null));
        eventRepository.saveAll(events);

        //Subjects - null
        List<Subject> subjects = subjectRepository.findAllBySchoolClassId(schoolClass.getId());
        subjects.forEach(subject -> subject.setSchoolClass(null));
        subjectRepository.saveAll(subjects);

        schoolClass.setName(null);

        classRepository.delete(schoolClass);

        return true;
    }

    public void deleteTopic(Topic topic) {
        topic.setSubject(null);
        topicRepository.save(topic);
        topicRepository.delete(topic);
    }

    public void deleteExtenuation(Extenuation extenuation) {
        extenuation.setAttendances(null);
        extenuationRepository.save(extenuation);
        extenuationRepository.delete(extenuation);
    }

    public void deleteAttendance(Attendance attendance) {
        attendance.setLesson(null);
        attendanceRepository.save(attendance);
        attendanceRepository.delete(attendance);
    }

    public void deleteSubject(Subject subject) {
        subject.setTopics(null);
        subject.setLessons(null);
        subjectRepository.save(subject);
        subjectRepository.delete(subject);
    }

    public void clearTeacher() {
        List<Teacher> teachers = teacherRepository.findAll();
        if (!teachers.isEmpty()) {
            for (Teacher teacher : teachers) {
                teacher.setGrades(null);
                teacher.setSubjects(null);
                teacher.setSchoolPeriods(null);
                teacher.setStudentCards(null);
                teacher.setBehaviors(null);
                teacher.setEvents(null);
                teacherRepository.save(teacher);
            }
        }
    }

    public void clearParent() {
        List<Parent> parents = parentRepository.findAll();
        if (!parents.isEmpty()) {
            for (Parent parent : parents) {
                parent.setParentCouncils(null);
                parent.setExtenuations(null);
                parentRepository.save(parent);
            }
        }
    }

    public void clearStudents() {
        List<Student> students = studentRepository.findAll();
        if (!students.isEmpty()) {
            for (Student student : students) {
                student.setStudentCouncils(null);
                student.setAttendance(null);
                student.setGrades(null);
                student.setBehaviors(null);
                studentRepository.save(student);
            }
        }
    }

    private String changeSchoolClassName(String schoolClassName) {
        StringBuilder newSchoolClassName = new StringBuilder();

        try {
            int schoolClassNumber = Character.getNumericValue(schoolClassName.charAt(0));
            schoolClassNumber += 1;

            newSchoolClassName.append(schoolClassNumber).append(schoolClassName, 1, schoolClassName.length());

        } catch (Exception ex) {
            //should not happen, but just in case
            newSchoolClassName.append(newSchoolClassName).append(" - kolejny rok");
        }


        return newSchoolClassName.toString();
    }

    private void addStudentToClass(Class schoolClass, List<Student> students) {
        students.forEach(student -> student.setSchoolClass(schoolClass));
    }
}
