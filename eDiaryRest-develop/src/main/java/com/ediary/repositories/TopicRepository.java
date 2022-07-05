package com.ediary.repositories;

import com.ediary.domain.Subject;
import com.ediary.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    Topic findByName(String name);
    List<Topic> findAllBySubjectOrderByNumber(Subject subject);
    Optional<Topic> findBySubjectOrderByNumberDesc(Subject subject);
}
