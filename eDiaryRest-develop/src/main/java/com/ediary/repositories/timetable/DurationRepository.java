package com.ediary.repositories.timetable;

import com.ediary.domain.timetable.Duration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DurationRepository extends JpaRepository<Duration, Long> {

    List<Duration> findAllByOrderByNumberAsc();
    Duration findByNumber(int number);
}
