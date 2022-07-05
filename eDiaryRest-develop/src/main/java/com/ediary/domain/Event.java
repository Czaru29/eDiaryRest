package com.ediary.domain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public enum Type {
        HOMEWORK("Praca domowa"),
        EXAM("Sprawdzian"),
        TEST("Kartkówka/Test"),
        OTHER("Inne");

        private final String translatedName;

        Type(String name) {
            this.translatedName = name;
        }

        public String getTranslatedName() {
            return translatedName;
        }
    }

    @Lob
    private String description;
    private Date createDate;
    private Date date;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "class_id")
    private Class schoolClass;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
}
