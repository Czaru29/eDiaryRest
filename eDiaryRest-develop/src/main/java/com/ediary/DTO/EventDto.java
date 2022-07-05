package com.ediary.DTO;

import com.ediary.domain.Event;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {

    private Long id;

    @Size(max = 255)
    private String description;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm")
    private Date createDate;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy HH:mm")
    private Date date;

    @NotNull
    private Event.Type type;

    //Class
    @NotNull
    private Long classId;
    private String className;

    //Teacher
    @NotNull
    private Long teacherId;
    private String teacherName;
}
