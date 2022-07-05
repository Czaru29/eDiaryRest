package com.ediary.repositories;

import com.ediary.domain.Notice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByDateDesc(Pageable pageable);
    List<Notice> findAllByOrderByDateDesc();
}
