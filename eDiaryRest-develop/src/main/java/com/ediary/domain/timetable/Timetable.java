package com.ediary.domain.timetable;

import com.ediary.domain.SchoolPeriod;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Timetable {

    private List<Duration> durations;
    private TreeMap<Duration, List<SchoolPeriod>> schedule;
}
