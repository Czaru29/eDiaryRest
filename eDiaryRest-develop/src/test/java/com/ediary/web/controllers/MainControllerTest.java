package com.ediary.web.controllers;

import com.ediary.DTO.UserDto;
import com.ediary.converters.UserToUserDto;
import com.ediary.services.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MainControllerTest {

    @Mock UserToUserDto userToUserDto;
    @Mock AdminService adminService;

    MockMvc mockMvc;

    MainController mainController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mainController = new MainController(userToUserDto, adminService);
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();
    }

    @Test
    void main() throws Exception {
        when(userToUserDto.convert(any())).thenReturn(UserDto.builder().build());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("user"));
    }
}