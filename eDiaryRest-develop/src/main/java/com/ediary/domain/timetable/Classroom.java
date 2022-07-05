package com.ediary.domain.timetable;

import com.ediary.domain.SchoolPeriod;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String number;

    @OneToMany(mappedBy = "classroom", fetch = FetchType.EAGER)
    private Set<SchoolPeriod> schoolPeriods;
}
