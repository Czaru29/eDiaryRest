package com.ediary.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public enum Status {
        @JsonProperty("present")
        PRESENT("Obecny"),

        @JsonProperty("absent")
        ABSENT("Nieobecny"),

        @JsonProperty("late")
        LATE("Spóźniony"),

        EXCUSED("Usprawiedliwiony"),
        UNEXCUSED("Nieusprawiedliwiony");

        private final String translatedName;

        Status(String translatedName) {
            this.translatedName = translatedName;
        }

        public String getTranslatedName() {
            return translatedName;
        }
    }

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "student_id")
    private Student student;

    @Singular
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "Extenuation_has_Attendance",
            joinColumns = {@JoinColumn(name = "Attendance_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "Extenuation_id", referencedColumnName = "id")})
    private Set<Extenuation> extenuations;
}
