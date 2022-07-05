package com.ediary.domain.timetable;

import com.ediary.domain.SchoolPeriod;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Duration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int number;
    @DateTimeFormat(pattern = "hh:mm")
    private LocalTime startTime;
    @DateTimeFormat(pattern = "hh:mm")
    private LocalTime endTime;

    @OneToMany(mappedBy = "duration", fetch = FetchType.EAGER)
    private Set<SchoolPeriod> schoolPeriods;
}
