package com.ediary.domain;

import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long number;
    private String name;
    @Lob
    private String description;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

}
