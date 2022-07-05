package com.ediary.services.rest;

import com.ediary.domain.Grade;
import com.ediary.domain.Student;
import com.ediary.domain.Subject;
import com.ediary.domain.Teacher;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface PdfRestService {

    Boolean createStudentCardPdf(HttpServletResponse response,
                                 Map<String, List<Grade>> gradesWithSubjects, Student student,
                                 Map<String, Long> attendanceNumber, String timeInterval, String behaviorGrade) throws Exception;

    byte[] createReportPdf(Teacher teacher, String timeInterval, Integer lessonsNumber,
                            String subjectsNames, Long gradesNumber, Long eventsNumber) throws Exception;

    byte[] createEndYearReportStudent(Map<Subject, List<Grade>> gradesWithSubjects,
                                      Map<Long, Grade> finalGrades,
                                      Student student,
                                      Map<String, Long> attendanceNumber, String behaviorGrade, Integer year);

    byte[] createEndYearReportTeacher(Map<Subject, Map<Student, List<Grade>>> listSubjectsStudentsWithGrades,
                                      Map<Long, Map<Student, Grade>> finalGrades,
                                      Teacher teacher, Integer year);
}
