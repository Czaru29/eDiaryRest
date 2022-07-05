package com.ediary.DTO;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BehaviorDto {

    private Long id;

    private Date date;

    @Size(max = 400)
    private String content;

    @NotNull
    private boolean positive;

    //Student
    @NotNull
    private Long studentId;
    private String studentName;

    //Teacher
    @NotNull
    private Long teacherId;
    private String teacherName;
}
