package com.ediary.services.rest;

import com.ediary.DTO.SchoolClassDto;
import com.ediary.DTO.StudentDto;
import com.ediary.DTO.TeacherDto;
import com.ediary.domain.Class;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeputyHeadRestService {

    Class saveSchoolClass(SchoolClassDto schoolClassDto);
    Boolean deleteClass(Long schoolClassId);
    List<SchoolClassDto> listAllSchoolClasses(Pageable pageable);
    SchoolClassDto getSchoolClassWithTeacher(Long classId);

    SchoolClassDto removeStudentFromClass(Long classId, Long studentId);
    SchoolClassDto addFormTutorToSchoolClass(Long classId, Long teacherId);
    SchoolClassDto addStudentToSchoolClass(Long classId, Long studentId);

    List<StudentDto> listAllStudentsWithoutClass(Pageable pageable);
    List<TeacherDto> listAllTeachersWithoutSchoolClass(Pageable pageable);
}
