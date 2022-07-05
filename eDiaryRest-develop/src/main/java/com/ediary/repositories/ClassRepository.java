package com.ediary.repositories;

import com.ediary.domain.Class;
import com.ediary.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ClassRepository extends JpaRepository<Class, Long> {

    Class findByName(String name);
    Long countAllByName(String name);
    Boolean existsByName(String name);


    //Not sure if works
    List<Class> findAllBySubjects(Subject subject);
    List<Class> findAllByTeacherId(Long teacherId);
    List<Class> findAllBySubjectsIn(Set<Subject> subjects);

}
