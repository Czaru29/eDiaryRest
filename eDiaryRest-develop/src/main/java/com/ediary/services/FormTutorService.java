package com.ediary.services;

import com.ediary.DTO.*;
import com.ediary.domain.*;
import com.ediary.domain.helpers.TimeInterval;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


public interface FormTutorService {

    StudentCouncilDto initNewStudentCouncil(Long teacherId);
    StudentCouncil saveStudentCouncil(Long teacherId, StudentCouncilDto studentCouncilDto, List<Long> studentsId);
    StudentCouncilDto findStudentCouncil(Long teacherId);
    StudentCouncilDto removeStudentFromCouncil(StudentCouncilDto studentCouncilDto,Long teacherId, Long studentId);

    ParentCouncilDto initNewParentCouncil(Long teacherId);
    ParentCouncil saveParentCouncil(Long teacherId, ParentCouncilDto parentCouncilDto, List<Long> parentsId);
    ParentCouncilDto findParentCouncil(Long teacherId);
    ParentCouncilDto removeParentFromCouncil(ParentCouncilDto parentCouncilDto, Long teacherId,  Long parentId);


    StudentCard findStudentCard(Long teacherId, Long studentId);
    Boolean createStudentCard(HttpServletResponse response, Long studentId, Date startTime, Date endTime) throws Exception;

    Map<StudentDto, GradeDto> listBehaviorGrades(Long teacherId);
    Map<Long, Map<String, Long>> listBehaviorInfo(Long teacherId);
    GradeDto initNewBehaviorFinalGrade(Long teacherId);
    Grade saveBehaviorGrade(Long teacher, GradeDto gradeDto);

    List<StudentDto> listClassStudents(Long teacherId);
    List<StudentDto> listClassStudentsStudentCouncil(Long teacherId);
    List<ParentDto> listClassParentsParentCouncil(Long teacherId);


    TimeInterval initNewTimeInterval();
    TimeInterval setTimeInterval(LocalDate startTime, LocalDate endTime);

    List<SubjectDto> listAllSubjectsByClass(Long teacherId);
    Map<StudentDto, List<GradeDto>> listStudentsGrades(Long teacherId, Long subjectId);
    Map<Long, GradeDto> listStudentsFinalGrades(Long teacherId, Long subjectId);

    List<String> getBehaviorGradeValues();

    List<Long> maxGradesCountBySubject(Long teacherId, Long subjectId);
}
