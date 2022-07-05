package com.ediary.domain.helpers;

import com.ediary.DTO.AttendanceDto;
import lombok.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeeklyAttendances {

    private Map<Date, List<AttendanceDto>> attendances;
    private Map<Date, Integer> attendancesNumber;
}
