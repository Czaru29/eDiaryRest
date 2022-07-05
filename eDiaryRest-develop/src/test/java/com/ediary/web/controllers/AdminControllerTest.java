package com.ediary.web.controllers;

import com.ediary.DTO.RoleDto;
import com.ediary.DTO.StudentDto;
import com.ediary.DTO.UserDto;
import com.ediary.converters.UserToUserDto;
import com.ediary.domain.security.User;
import com.ediary.services.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AdminControllerTest {

    @Mock
    AdminService adminService;
    @Mock
    UserToUserDto userToUserDto;

    AdminController adminController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        adminController = new AdminController(adminService, userToUserDto);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();

    }

    @Test
    void getAllUsers() throws Exception {

        when(adminService.getAllUsers()).thenReturn(Collections.singletonList(UserDto.builder().build()));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("admin/allUsers"));

        assertNotNull(adminService.getAllUsers());
    }


    @Test
    void initNewUser() throws Exception {
        when(adminService.initNewUser()).thenReturn(UserDto.builder().build());

        mockMvc.perform(get("/admin/newUser"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("newUser"))
                .andExpect(view().name("admin/newUser"));

        assertNotNull(adminService.initNewUser());
    }

    @Test
    void processNewUser() throws Exception {

        when(adminService.saveUser(any(), any(), anyList())).thenReturn(User.builder().build());

        mockMvc.perform(post("/admin/newUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(AbstractAsJsonControllerTest.asJsonString(UserDto.builder().build()))
                .param("selectedRoles", "1")
                .param("selectedStudents", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/newUser"));

        assertNotNull(adminService.saveUser(any(), any(), anyList()));

    }

    @Test
    void getUser() throws Exception {
        Long userId = 1L;

        when(adminService.getUser(any())).thenReturn(UserDto.builder().build());

        mockMvc.perform(get("/admin/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("admin/user"));

        verify(adminService, times(1)).getUser(userId);
        assertNotNull(adminService.getUser(userId));
    }

    @Test
    void deleteUser() throws Exception {
        Long userId = 1L;

        when(adminService.deleteUser(any())).thenReturn(true);

        mockMvc.perform(post("/admin/users/" + userId + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users"));

        verify(adminService, times(1)).deleteUser(userId);
        assertTrue(adminService.deleteUser(userId));
    }


    @Test
    void deleteRole() throws Exception {
        Long userId = 1L;
        String roleStudent = "ROLE_STUDENT";

        when(adminService.deleteRole(userId, roleStudent)).thenReturn(true);

        mockMvc.perform(post("/admin/users/" + userId + "/role/delete")
                .param("roleToDelete", roleStudent))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/users/" + userId + "/edit"));

        verify(adminService, times(1)).deleteRole(userId, roleStudent);
    }



}
