package com.ediary.domain;

import com.ediary.domain.timetable.Classroom;
import com.ediary.domain.timetable.Day;
import com.ediary.domain.timetable.Duration;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class SchoolPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String descripton;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "day_id")
    private Day day;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "duration_id")
    private Duration duration;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "class_id")
    private Class schoolClass;

}
