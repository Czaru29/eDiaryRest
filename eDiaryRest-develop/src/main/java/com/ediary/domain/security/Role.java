package com.ediary.domain.security;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "Authority_has_Role",
            joinColumns = {@JoinColumn(name = "Authority_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "Role_id", referencedColumnName = "id")})
    private Set<Authority> authorities;
}
