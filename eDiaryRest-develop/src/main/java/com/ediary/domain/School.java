package com.ediary.domain;


import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String headmasterName;
    private String headmasterLastName;
    private String email;
    private String schoolOffice;

    @OneToOne(cascade = CascadeType.MERGE)
    private Address address;

}
