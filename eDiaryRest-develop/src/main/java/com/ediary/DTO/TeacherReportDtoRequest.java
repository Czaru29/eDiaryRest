package com.ediary.DTO;

import com.ediary.util.DateHelper;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherReportDtoRequest {

    @JsonFormat(pattern = DateHelper.DATETIME_FORMAT)
    private Date startTime;

    @JsonFormat(pattern = DateHelper.DATETIME_FORMAT)
    private Date endTime;

    @Size(min = 1)
    private List<Long> teachersId;
}
