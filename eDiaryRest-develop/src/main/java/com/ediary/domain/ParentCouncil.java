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
public class ParentCouncil {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "parentCouncil")
    private Class schoolClass;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "ParentCouncil_has_Parent",
            joinColumns = {@JoinColumn(name = "ParentCouncil_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "Parent_id", referencedColumnName = "id")})
    private Set<Parent> parents;

}
