package com.ediary.services;

import com.ediary.domain.SchoolPeriod;
import com.ediary.domain.timetable.Duration;
import com.ediary.domain.timetable.Timetable;
import com.ediary.repositories.SchoolPeriodRepository;
import com.ediary.repositories.timetable.DurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class TimetableServiceImpl implements TimetableService {

    private final SchoolPeriodRepository schoolPeriodRepository;
    private final DurationRepository durationRepository;


    @Override
    public Timetable getTimetableByClassId(Long classId) {

        List<SchoolPeriod> schoolPeriods = schoolPeriodRepository.findAllBySchoolClassId(classId);

        return createNewTimetable(schoolPeriods);

    }

    @Override
    public Timetable getTimetableByTeacherId(Long teacherId) {

        List<SchoolPeriod> schoolPeriods = schoolPeriodRepository.findAllByTeacherId(teacherId);

       return createNewTimetable(schoolPeriods);
    }


    private Timetable createNewTimetable(List<SchoolPeriod> schoolPeriods) {
        List<Duration> durations = durationRepository.findAllByOrderByNumberAsc();

        Set<Duration> durs = new HashSet<>(durationRepository.findAllByOrderByNumberAsc());


        int durationsSize = durations.size();

        TreeMap<Duration, List<SchoolPeriod>> schedule = getSchedule();

        durs.forEach(duration -> {
            List<SchoolPeriod> arrayList = Stream
                    .generate(SchoolPeriod::new)
                    .limit(durationsSize)
                    .collect(Collectors.toList());

            schedule.put(duration, arrayList);
        });

        schoolPeriods.forEach(schoolPeriod -> {
            schedule.get(schoolPeriod.getDuration()).add(schoolPeriod.getDay().getNumber(), schoolPeriod);
        });

        return Timetable.builder().durations(durations).schedule(schedule).build();
    }


    private TreeMap<Duration, List<SchoolPeriod>> getSchedule() {
        return new TreeMap<>((o1, o2) -> {
            if (o1.getNumber() == o2.getNumber())
                return 0;
            else if (o1.getNumber() > o2.getNumber())
                return 1;

            return -1;
        });
    }
}
