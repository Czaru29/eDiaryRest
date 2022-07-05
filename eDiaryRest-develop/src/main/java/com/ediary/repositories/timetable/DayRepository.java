package com.ediary.repositories.timetable;

import com.ediary.domain.timetable.Day;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DayRepository extends JpaRepository<Day, Long> {

    List<Day> findAllByOrderByNumberAsc();
    Day findByNumber(int number);
}
