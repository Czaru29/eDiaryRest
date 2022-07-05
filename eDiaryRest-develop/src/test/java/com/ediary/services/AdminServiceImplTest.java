package com.ediary.services;

import com.ediary.DTO.RoleDto;
import com.ediary.DTO.SchoolDto;
import com.ediary.DTO.UserDto;
import com.ediary.converters.*;
import com.ediary.domain.School;
import com.ediary.domain.security.Role;
import com.ediary.domain.security.User;
import com.ediary.repositories.*;
import com.ediary.repositories.security.RoleRepository;
import com.ediary.repositories.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class AdminServiceImplTest {

    @Mock UserRepository userRepository;
    @Mock RoleRepository roleRepository;
    @Mock SchoolRepository schoolRepository;
    @Mock AddressRepository addressRepository;
    @Mock StudentRepository studentRepository;
    @Mock ParentRepository parentRepository;
    @Mock TeacherRepository teacherRepository;

    @Mock UserToUserDto userToUserDto;
    @Mock UserDtoToUser userDtoToUser;
    @Mock RoleToRoleDto roleToRoleDto;
    @Mock RoleDtoToRole roleDtoToRole ;
    @Mock SchoolToSchoolDto schoolToSchoolDto;
    @Mock SchoolDtoToSchool schoolDtoToSchool;
    @Mock StudentToStudentDto studentToStudentDto;

    @Mock
    PasswordEncoder passwordEncoder;


    AdminService adminService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        adminService = new AdminServiceImpl(userRepository, roleRepository, schoolRepository, addressRepository,
                studentRepository, parentRepository, teacherRepository,
                userToUserDto, userDtoToUser, roleToRoleDto, roleDtoToRole, schoolToSchoolDto, schoolDtoToSchool,studentToStudentDto,
                passwordEncoder);
    }

    @Test
    void initNewUser() {
        UserDto userDto = adminService.initNewUser();

        assertNotNull(userDto);
    }

    @Test
    void saveUser() {
        Long userId = 1L;

        User user = User.builder().id(userId).build();
        UserDto userDto = UserDto.builder().id(userId).build();

        when(userDtoToUser.convert(any())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);

        User savedUser = adminService.saveUser(userDto, List.of(1L, 2L), List.of(1L, 2L));

        assertNotNull(savedUser);
        assertEquals(savedUser, user);
        assertTrue(savedUser.getEnabled());
        assertTrue(savedUser.isAccountNonExpired());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void deleteUser() {
        Long userId = 1L;

        User user = User.builder().enabled(true).id(userId).build();

        when(userRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(user));

        Boolean result = adminService.deleteUser(userId);

        assertTrue(result);
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);

    }

    @Test
    void getUser() {
        Long userId = 1L;

        User user = User.builder().id(userId).build();
        UserDto userDto = UserDto.builder().build();

        when(userToUserDto.convertForAdmin(any())).thenReturn(userDto);
        when(userRepository.findById(any())).thenReturn(java.util.Optional.ofNullable(user));

        UserDto foundUser = adminService.getUser(userId);

        assertNotNull(foundUser);
        assertEquals(foundUser, userDto);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void updateUser() {
        Long userId = 1L;

        User user = User.builder().id(userId).build();
        UserDto userDto = UserDto.builder().id(userId).build();

        when(userDtoToUser.convert(any())).thenReturn(user);
        when(userToUserDto.convertForAdmin(any())).thenReturn(userDto);
        when(userRepository.save(any())).thenReturn(user);

        User savedUser = adminService.saveUser(userDto, List.of(1L, 2L), List.of(1L, 2L));

        assertNotNull(savedUser);
        assertEquals(savedUser, user);
        assertTrue(savedUser.getEnabled());
        assertTrue(savedUser.isAccountNonExpired());
        verify(userRepository, times(1)).save(user);
    }


    @Test
    void getAllUsers() {
        UserDto userDto = UserDto.builder().id(1L).build();

        when(userRepository.findAll()).thenReturn(Collections.singletonList(User.builder().build()));
        when(userToUserDto.convertForAdmin(any())).thenReturn(userDto);

        List<UserDto> userDtoList = adminService.getAllUsers();

        assertNotNull(userDtoList);
        assertEquals(1, userDtoList.size());
    }

    @Test
    void getAllRoles() {
        Long roleId = 1L;
        RoleDto role = RoleDto.builder().id(roleId).build();

        when(roleRepository.findAll()).thenReturn(Collections.singletonList(Role.builder().build()));
        when(roleToRoleDto.convert(any())).thenReturn(role);

        List<RoleDto> roles = adminService.getAllRoles();

        assertNotNull(roles);
        assertTrue(roles.contains(role));
    }

    @Test
    void updateSchool() {
        Long schoolId = 1L;

        School school = School.builder().id(schoolId).build();
        SchoolDto schoolDto = SchoolDto.builder().id(schoolId).build();

        when(schoolDtoToSchool.convert(any())).thenReturn(school);
        when(schoolRepository.save(any())).thenReturn(school);

        School updatedSchool = adminService.updateSchool(schoolDto);

        assertNotNull(updatedSchool);
        assertEquals(updatedSchool, school);
        verify(schoolRepository, times(1)).save(school);
    }

    @Test
    void getSchool() {
        Long schoolId = 1L;

        School school = School.builder().id(schoolId).build();
        SchoolDto schoolDto = SchoolDto.builder().id(schoolId).build();

        when(schoolToSchoolDto.convert(any())).thenReturn(schoolDto);
        when(schoolRepository.findAll()).thenReturn(Collections.singletonList(school));

        SchoolDto getSchool = adminService.getSchool();

        assertNotNull(getSchool);
        assertEquals(getSchool, schoolDto);
        verify(schoolRepository, times(1)).findAll();
    }

}
