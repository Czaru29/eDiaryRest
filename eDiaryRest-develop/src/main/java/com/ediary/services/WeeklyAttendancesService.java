package com.ediary.services;

import com.ediary.domain.helpers.WeeklyAttendances;

import java.sql.Date;

public interface WeeklyAttendancesService {
    WeeklyAttendances getAttendancesByWeek(Long studentId, Integer daysNumber, Date date);
}
