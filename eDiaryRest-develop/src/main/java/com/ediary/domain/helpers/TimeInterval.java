package com.ediary.domain.helpers;

import com.ediary.util.DateHelper;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeInterval {

    @DateTimeFormat(pattern = DateHelper.DATETIME_FORMAT)
    private Date startTime;

    @DateTimeFormat(pattern = DateHelper.DATETIME_FORMAT)
    private Date endTime;
}
