package com.ediary.DTO;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopicDto {

    private Long id;

    @NotNull
    @Min(0)
    private Long number;

    @NotNull
    @Size(min = 3, max = 255)
    private String name;
    private String description;

    //Subject
    private String subjectName;

    @NotNull
    private Long subjectId;
}
