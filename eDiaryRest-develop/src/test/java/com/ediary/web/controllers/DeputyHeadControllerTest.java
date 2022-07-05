package com.ediary.web.controllers;

import com.ediary.DTO.*;
import com.ediary.converters.UserToUserDto;
import com.ediary.domain.Class;
import com.ediary.services.DeputyHeadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class DeputyHeadControllerTest {


    @Mock
    UserToUserDto userToUserDto;

    @Mock
    DeputyHeadService deputyHeadService;

    DeputyHeadController deputyHeadController;

    MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        deputyHeadController = new DeputyHeadController(deputyHeadService, userToUserDto);
        mockMvc = MockMvcBuilders.standaloneSetup(deputyHeadController).build();
    }

    @Test
    void newClass() throws Exception {
        when(deputyHeadService.initNewClass()).thenReturn(SchoolClassDto.builder().build());
        when(deputyHeadService.listAllStudentsWithoutClass(any(), any())).thenReturn(Collections.singletonList(StudentDto.builder().build()));
        when(deputyHeadService.listAllTeachersWithoutClass(any(), any())).thenReturn(Collections.singletonList(TeacherDto.builder().build()));


        mockMvc.perform(get("/deputyHead/newClass/formTutor"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("schoolClass"))
                .andExpect(model().attributeExists("teachers"))
                .andExpect(view().name("deputyHead/newClass"));

        assertNotNull(deputyHeadService.listAllStudentsWithoutClass(any(), any()));
        assertNotNull(deputyHeadService.listAllTeachersWithoutClass(any(), any()));
        assertNotNull(deputyHeadService.initNewClass());

    }

    @Test
    void processNewClass() throws Exception {
        when(deputyHeadService.saveSchoolClass(any(), any())).thenReturn(Class.builder().id(1L).build());

        mockMvc.perform(post("/deputyHead/newClass/formTutor")
                .contentType(MediaType.APPLICATION_JSON)
                .content(AbstractAsJsonControllerTest.asJsonString(SchoolClassDto.builder().build())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("schoolClass"))
                .andExpect(view().name("deputyHead/newClassName"));

    }

    @Test
    void processNewClassName() throws Exception {
        when(deputyHeadService.saveSchoolClass(any())).thenReturn(Class.builder().id(1L).build());

        mockMvc.perform(post("/deputyHead/newClass/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(AbstractAsJsonControllerTest.asJsonString(SchoolClassDto.builder().build())))
                .andExpect(status().isOk())
                .andExpect(view().name( "deputyHead/newClassName"));

    }

    @Test
    void getClasses() throws Exception {
        when(deputyHeadService.listAllSchoolClasses()).thenReturn(Collections.singletonList(SchoolClassDto.builder().build()));


        mockMvc.perform(get("/deputyHead/classes"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("classes"))
                .andExpect(view().name("deputyHead/classes"));

        assertNotNull(deputyHeadService.listAllSchoolClasses());
    }

    @Test
    void getOneClass() throws Exception {
        when(deputyHeadService.getSchoolClass(any())).thenReturn(SchoolClassDto.builder().build());


        mockMvc.perform(get("/deputyHead/classes/" + 1L))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("schoolClass"))
                .andExpect(view().name("deputyHead/oneClass"));

        assertNotNull(deputyHeadService.getSchoolClass(1L));
    }

    @Test
    void deleteClass() throws Exception {
        mockMvc.perform(post("/deputyHead/classes/" + 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/deputyHead/classes"));

        verify(deputyHeadService, times(1)).deleteClass(any());
    }

    @Test
    void removeStudentFromClass() throws Exception {
        when(deputyHeadService.removeStudentFromClass(any(), any())).thenReturn(SchoolClassDto.builder().id(1L).build());


        mockMvc.perform(post("/deputyHead/classes/" + 1L + "/removeStudent/" + 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/deputyHead/classes/" + 1L));

        assertNotNull(deputyHeadService.removeStudentFromClass(1L, 1L));
    }

    @Test
    void addFormTutorToClass() throws Exception {
        when(deputyHeadService.addFormTutorToClass(any(), any())).thenReturn(SchoolClassDto.builder().id(1L).build());


        mockMvc.perform(post("/deputyHead/classes/" + 1L + "/addTeacher/" + 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/deputyHead/classes/" + 1L));

        assertNotNull(deputyHeadService.addFormTutorToClass(1L, 1L));
    }

    @Test
    void addStudentToClass() throws Exception {
        when(deputyHeadService.addStudentToClass(any(), any())).thenReturn(SchoolClassDto.builder().id(1L).build());


        mockMvc.perform(post("/deputyHead/classes/" + 1L + "/addStudent/" + 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/deputyHead/classes/" + 1L + "/addStudents?page=" + 0));

        assertNotNull(deputyHeadService.addStudentToClass(1L, 1L));
    }

}
