package com.ediary.domain;

import com.ediary.domain.security.User;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public enum Status {
        SENT, READ
    }

    private String title;
    @Lob
    private String content;
    private Date date;

    @Enumerated(value = EnumType.STRING)
    private Status status;


    @Singular
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "Message_has_User",
            joinColumns = {@JoinColumn(name = "Message_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "User_id", referencedColumnName = "id")})
    private Set<User> readers;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User senders;

}
