package com.ediary.repositories;

import com.ediary.domain.Extenuation;
import com.ediary.domain.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ExtenuationRepository extends JpaRepository<Extenuation, Long> {
    Extenuation findByParent(Parent parent);

//    @Query("select e from Extenuation e join Attendance a where a.id = ?1")
//    Set<Extenuation> findAllByAttendancesId(Long attendanceId);

    List<Extenuation> findAllByAttendancesId(Long attId);
    List<Extenuation> findAllByParentId(Long parentId);
    List<Extenuation> findAllByParentIdAndStatus(Long parentId, Extenuation.Status status);
}
