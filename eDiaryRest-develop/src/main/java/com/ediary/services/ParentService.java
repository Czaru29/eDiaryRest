package com.ediary.services;

import com.ediary.DTO.*;
import com.ediary.domain.*;
import com.ediary.domain.security.User;

import java.util.List;

public interface ParentService {

    List<StudentDto> listStudents(Long parentId);
    StudentDto findStudent(Long parentId, Long studentId);
    StudentDto findStudent(User user, Long studentId);
    List<SubjectDto> getAllStudentSubjectNames(Long parentId, Long studentId);
    Attendance saveAttendance(AttendanceDto attendance);
    ParentDto findByUser(User user);
    ExtenuationDto initNewExtenuation(List<Long> attendancesId, ExtenuationDto extenuationDto, Long parentId);
    Extenuation saveExtenuation(ExtenuationDto extenuation, Long parentId, List<Long> attId);
    List<ExtenuationDto> getAllExtenuations(Long parentId);
}
