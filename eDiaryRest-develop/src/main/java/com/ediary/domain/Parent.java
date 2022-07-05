package com.ediary.domain;

import com.ediary.domain.security.User;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<Student> students;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<Extenuation> extenuations;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "ParentCouncil_has_Parent",
            joinColumns = {@JoinColumn(name = "Parent_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "ParentCouncil_id", referencedColumnName = "id")})
    private Set<ParentCouncil> parentCouncils;

}
