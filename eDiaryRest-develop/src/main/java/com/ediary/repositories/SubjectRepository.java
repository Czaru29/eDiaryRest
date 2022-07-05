package com.ediary.repositories;

import com.ediary.domain.Lesson;
import com.ediary.domain.Student;
import com.ediary.domain.Subject;
import com.ediary.domain.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Subject findByName(String name);
    List<Subject> findAllBySchoolClass_Students(Student student);
    List<Subject> findAllByTeacher(Teacher teacher);
    List<Subject> findAllByTeacherIdOrderByName(Long teacherId);
    List<Subject> findAllBySchoolClassIdOrderByName(Long classId);
    List<Subject> findAllBySchoolClassId(Long classId);
    List<Subject> findAllByLessonsIn(Set<Lesson> lessons);
}
