package com.ediary.services;

import com.ediary.domain.SchoolPeriod;
import com.ediary.domain.timetable.Day;
import com.ediary.domain.timetable.Duration;
import com.ediary.domain.timetable.Timetable;
import com.ediary.repositories.SchoolPeriodRepository;
import com.ediary.repositories.timetable.DayRepository;
import com.ediary.repositories.timetable.DurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimetableServiceImplTest {

    private Long classId = 1L;

    @Mock
    TimetableService timetableService;

    @Mock
    DurationRepository durationRepository;

    @Mock
    DayRepository dayRepository;

    @Mock
    SchoolPeriodRepository schoolPeriodRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getTimetableByClassId() {

        when(durationRepository.findAllByOrderByNumberAsc()).thenReturn(Arrays.asList(
                Duration.builder().id(1L).number(0).build(),
                Duration.builder().id(2L).number(1).build(),
                Duration.builder().id(3L).number(2).build()
        ));

        when(dayRepository.findAllByOrderByNumberAsc()).thenReturn(Arrays.asList(
                Day.builder().id(1L).number(0).build(),
                Day.builder().id(2L).number(1).build()
        ));

        when(schoolPeriodRepository.findAllBySchoolClassId(classId)).thenReturn(Arrays.asList(
                SchoolPeriod.builder().id(1L).build(),
                SchoolPeriod.builder().id(2L).build(),
                SchoolPeriod.builder().id(3L).build(),
                SchoolPeriod.builder().id(4L).build()
        ));

        Timetable timetable = timetableService.getTimetableByClassId(classId);

        assertEquals(3,durationRepository.findAllByOrderByNumberAsc().size());
        assertEquals(2,dayRepository.findAllByOrderByNumberAsc().size());
        assertEquals(4,schoolPeriodRepository.findAllBySchoolClassId(classId).size());
        verify(durationRepository, times(1)).findAllByOrderByNumberAsc();
        verify(dayRepository, times(1)).findAllByOrderByNumberAsc();
        verify(schoolPeriodRepository, times(1)).findAllBySchoolClassId(classId);

    }
}