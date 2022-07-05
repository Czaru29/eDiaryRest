package com.ediary.services;

import com.ediary.domain.Subject;
import com.ediary.repositories.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SubjectServiceImplTest {

    private final Long subjectId = 2L;
    private final String subjectName = "Maths";

    @Mock
    SubjectRepository subjectRepository;

    SubjectService subjectService;

    Subject subjectBuild;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        subjectService = new SubjectServiceImpl(subjectRepository);

        subjectBuild = Subject.builder().id(subjectId).name(subjectName).build();
    }

    @Test
    void getNameById() {
        Optional<Subject> subjectOptional = Optional.of(subjectBuild);

        when(subjectRepository.findById(anyLong())).thenReturn(subjectOptional);

        String name = subjectService.getNameById(subjectId);

        verify(subjectRepository, times(1)).findById(subjectId);
        assertEquals(subjectName, subjectService.getNameById(subjectId));

    }
}