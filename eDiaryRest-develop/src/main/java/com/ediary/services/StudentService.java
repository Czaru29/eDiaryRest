package com.ediary.services;

import com.ediary.DTO.*;
import com.ediary.domain.EndYearReport;
import com.ediary.domain.security.User;
import com.ediary.domain.timetable.Timetable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface StudentService {

    List<GradeDto> listGrades(Long studentId);
    List<GradeDto> listGrades(Long studentId, Long subjectId);
    List<SubjectDto> listSubjects(Long studentId);
    List<AttendanceDto> listAttendances(Long studentId);
    List<BehaviorDto> listBehaviors(Long studentId, Integer page, Integer size);
    List<EventDto> listEvents(Long studentId, Integer page, Integer size, Boolean includeHistory);
    Timetable getTimetableByStudentId(Long studentId);
    StudentDto findByUser(User user);

    Map<SubjectDto, List<GradeDto>> listSubjectsGrades(Long studentId);
    GradeDto getBehaviorGrade(Long studentId);

    List<EndYearReportDto> listEndYearReports(Long studentId);
    boolean getEndYearReportPdf(HttpServletResponse response, Long studentId, Long reportId) throws IOException;

}
