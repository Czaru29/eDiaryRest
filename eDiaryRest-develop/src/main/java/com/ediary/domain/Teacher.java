package com.ediary.domain;

import com.ediary.domain.security.User;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;


    @OneToOne(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Class schoolClass;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<Grade> grades;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<Subject> subjects;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<Event> events;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<SchoolPeriod> schoolPeriods;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<StudentCard> studentCards;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<Behavior> behaviors;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<Report> teacherReports;

    @OneToMany(mappedBy = "headmaster", fetch = FetchType.LAZY)
    private Set<Report> headmasterReports;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private Set<EndYearReport> endYearReports;

}
