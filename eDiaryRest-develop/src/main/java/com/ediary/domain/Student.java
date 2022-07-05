package com.ediary.domain;


import com.ediary.domain.security.Role;
import com.ediary.domain.security.User;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "class_id")
    private Class schoolClass;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "Student_has_StudentCouncil",
            joinColumns = {@JoinColumn(name = "student_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "studentCouncil_id", referencedColumnName = "id")})
    private Set<StudentCouncil> studentCouncils;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Attendance> attendance;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Grade> grades;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<Behavior> behaviors;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private Set<EndYearReport> endYearReports;


}
