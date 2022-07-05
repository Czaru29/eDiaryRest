package com.ediary.domain.security;

import com.ediary.domain.Address;
import com.ediary.domain.Message;
import com.ediary.domain.Notice;
import lombok.*;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;

    private String firstName;
    private String lastName;

    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "Role_has_User",
            joinColumns = {@JoinColumn(name = "User_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "Role_id", referencedColumnName = "id")})
    private Set<Role> roles;


    @OneToOne(cascade = CascadeType.MERGE)
    private Address address;

    @ManyToMany(mappedBy = "readers", fetch = FetchType.EAGER)
    private Set<Message> readMessages;

    @OneToMany(mappedBy = "senders", fetch = FetchType.EAGER)
    private Set<Message> sendMessages;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<Notice> notices;


    @Transient
    public Set<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = this.getRoles();
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    private Boolean enabled = true;

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

}
