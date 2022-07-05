package com.ediary.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "studentCouncil_id")
    private StudentCouncil studentCouncil;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "parentCouncil_id")
    private ParentCouncil parentCouncil;

    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    private Set<Student> students;

    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    private Set<Event> events;

    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    private Set<SchoolPeriod> schoolPeriods;

    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    private Set<Subject> subjects;

    @OneToMany(mappedBy = "schoolClass", fetch = FetchType.LAZY)
    private Set<Lesson> lessons;
}
