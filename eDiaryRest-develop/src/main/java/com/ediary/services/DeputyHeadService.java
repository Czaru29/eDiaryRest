package com.ediary.services;

import com.ediary.DTO.SchoolClassDto;
import com.ediary.DTO.StudentDto;
import com.ediary.DTO.TeacherDto;
import com.ediary.domain.Class;

import java.util.List;

public interface DeputyHeadService {

    SchoolClassDto initNewClass();
    Class saveSchoolClass(SchoolClassDto schoolClassDto, List<Long> studentsId);
    Class saveSchoolClass(SchoolClassDto schoolClassDto);
    Boolean deleteClass(Long schoolClassId);
    List<SchoolClassDto> listAllSchoolClasses();
    SchoolClassDto getSchoolClass(Long classId);

    Boolean schoolClassNameIsUnique(String schoolClassName);


    SchoolClassDto removeFormTutorFromClass(Long classId, Long teacherId);
    SchoolClassDto removeStudentFromClass(Long classId, Long studentId);
    SchoolClassDto addFormTutorToClass(Long classId, Long teacherId);
    SchoolClassDto addStudentToClass(Long classId, Long studentId);

    List<StudentDto> listAllStudentsWithoutClass(Integer page, Integer size);

    List<TeacherDto> listAllTeachersWithoutClass(Integer page, Integer size);
    TeacherDto getSchoolClassWithTeacher(Long teacherId, Long classId);
    SchoolClassDto getSchoolClassWithTeacher(Long teacherId);

    Integer countStudentsWithoutClass();
}
