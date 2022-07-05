package com.ediary.services;

import com.ediary.DTO.*;
import com.ediary.converters.*;
import com.ediary.domain.*;
import com.ediary.domain.Class;
import com.ediary.domain.helpers.GradeWeight;
import com.ediary.domain.security.User;
import com.ediary.domain.timetable.Timetable;
import com.ediary.exceptions.NotFoundException;
import com.ediary.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {

    private final TimetableService timetableService;
    private final EventService eventService;

    private final GradeRepository gradeRepository;
    private final AttendanceRepository attendanceRepository;
    private final BehaviorRepository behaviorRepository;
    private final StudentRepository studentRepository;
    private final EventRepository eventRepository;
    private final SubjectRepository subjectRepository;
    private final EndYearReportRepository endYearReportRepository;

    private final GradeToGradeDto gradeToGradeDto;
    private final AttendanceToAttendanceDto attendanceToAttendanceDto;
    private final BehaviorToBehaviorDto behaviorToBehaviorDto;
    private final EventToEventDto eventToEventDto;
    private final StudentToStudentDto studentToStudentDto;
    private final SubjectToSubjectDto subjectToSubjectDto;
    private final EndYearReportToEndYearReportDto endYearReportToEndYearReportDto;


    @Override
    public List<GradeDto> listGrades(Long studentId) {
        return gradeRepository.findAllByStudentId(studentId)
                .stream()
                .map(gradeToGradeDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeDto> listGrades(Long studentId, Long subjectId) {
        return gradeRepository.findAllByStudentIdAndSubjectId(studentId, subjectId)
                .stream()
                .map(gradeToGradeDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> listSubjects(Long studentId) {
        Student student = getStudentById(studentId);
        return subjectRepository.findAllBySchoolClass_Students(student)
                .stream()
                .map(subjectToSubjectDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceDto> listAttendances(Long studentId) {
        return attendanceRepository.findAllByStudentId(studentId)
                .stream()
                .map(attendanceToAttendanceDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<BehaviorDto> listBehaviors(Long studentId, Integer page, Integer size) {
        if (page < 0) {
            return null;
        }

        Pageable pageable = PageRequest.of(page, size);


        return behaviorRepository.findAllByStudentIdOrderByDateDesc(studentId, pageable)
                .stream()
                .map(behaviorToBehaviorDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDto> listEvents(Long studentId, Integer page, Integer size, Boolean includeHistory) {

        Student student = getStudentById(studentId);

        if (student.getSchoolClass() == null || page < 0) {
            return null;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("date"));

        if (includeHistory) {
            pageable = PageRequest.of(page, size, Sort.by("date").descending());
            return eventRepository.findAllBySchoolClassIdAndDateBefore(student.getSchoolClass().getId(),
                    new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()), pageable)
                    .stream()
                    .map(eventToEventDto::convert)
                    .collect(Collectors.toList());
        } else {
            return eventRepository.findAllBySchoolClassIdAndDateAfter(student.getSchoolClass().getId(),
                    new Date(Timestamp.valueOf(LocalDateTime.now()).getTime()), pageable)
                    .stream()
                    .map(eventToEventDto::convert)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Timetable getTimetableByStudentId(Long studentId) {

        Student student = getStudentById(studentId);

        if (student.getSchoolClass() == null) {
            return null;
        }

        return timetableService.getTimetableByClassId(student.getSchoolClass().getId());

    }

    @Override
    public StudentDto findByUser(User user) {
        Optional<Student> studentOptional = studentRepository.findByUser(user);
        if (!studentOptional.isPresent()) {
            throw new NotFoundException("Student Not Found");
        }
        return studentToStudentDto.convert(studentOptional.get());
    }


    @Override
    public Map<SubjectDto, List<GradeDto>> listSubjectsGrades(Long studentId) {
        Student student = getStudentById(studentId);

        Map<SubjectDto, List<GradeDto>> studentGradesListMap = new LinkedHashMap<>();

        if (student.getSchoolClass() == null) {
            return null;
        }

        List<Subject> subjects = subjectRepository.findAllBySchoolClassIdOrderByName(student.getSchoolClass().getId());

        subjects.forEach(subject -> {
            studentGradesListMap.put(subjectToSubjectDto.convert(subject),
                    gradeRepository.findAllByStudentIdAndSubjectId(student.getId(), subject.getId())
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
    public GradeDto getBehaviorGrade(Long studentId) {
        Student student = getStudentById(studentId);

        Grade grade = gradeRepository.findByStudentIdAndWeight(student.getId(), GradeWeight.BEHAVIOR_GRADE.getWeight());

        if (grade != null) {
            return gradeToGradeDto.convert(grade);
        }

        return null;
    }

    @Override
    public List<EndYearReportDto> listEndYearReports(Long studentId) {
        Student student = getStudentById(studentId);

        return student.getEndYearReports()
                .stream()
                .map(endYearReportToEndYearReportDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public boolean getEndYearReportPdf(HttpServletResponse response, Long studentId, Long reportId) throws IOException {
        Student student = getStudentById(studentId);

        Optional<EndYearReport> reportOptional = endYearReportRepository.findByIdAndStudent(reportId, student);
        if (!reportOptional.isPresent()) {
            return false;
        }

        EndYearReport report = reportOptional.get();

        OutputStream out = response.getOutputStream();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=" + student.getUser().getFirstName() + "_" + report.getYear() + ".pdf";
        response.setHeader(headerKey, headerValue);



        out.write(report.getEndYearPdf());
        out.close();

        return true;
    }

    private Student getStudentById(Long studentId) {
        Optional<Student> studentOptional = studentRepository.findById(studentId);

        if(!studentOptional.isPresent()) {
            throw new NotFoundException("Student Not Found.");
        }

        return studentOptional.get();
    }


}
