package com.ediary.repositories.timetable;

import com.ediary.domain.timetable.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    Classroom findByNumber(String number);
}
