package com.ediary.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDto {

    private Long id;

    //todo: annotation for values (max 6 for normal, enum for behavior grade)
    @NotNull
    private String value;

    @NotNull
    @Min(value = 0)
    private Integer weight;

    @Size(max = 255)
    private String description;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="CET")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date date;

    //Teacher
    @NotNull
    private Long teacherId;
    private String teacherName;

    //Subject
    private Long subjectId;
    private String subjectName;

    //Student
    @NotNull
    private Long studentId;
    private String studentName;
}
