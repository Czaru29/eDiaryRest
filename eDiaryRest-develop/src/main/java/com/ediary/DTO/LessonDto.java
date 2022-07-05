package com.ediary.DTO;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDto {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 255)
    private String name;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm")
    private Date date;

    //Class
    @NotNull
    private Long classId;
    private String className;

    //Topic
    @NotNull
    private Long topicId;
    private String topicName;

    //Subject
    @NotNull
    private Long subjectId;
    private String subjectName;


}
