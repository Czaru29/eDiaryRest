package com.ediary.services;

import com.ediary.DTO.RoleDto;
import com.ediary.DTO.SchoolDto;
import com.ediary.DTO.StudentDto;
import com.ediary.DTO.UserDto;
import com.ediary.domain.School;
import com.ediary.domain.security.User;

import java.util.List;

public interface AdminService {

    UserDto initNewUser();
    User saveUser(UserDto userDto, List<Long> rolesId,List<Long> selectedStudentsForParent);
    Boolean deleteUser(Long userId);
    UserDto getUser(Long userId);
    UserDto updateUser(UserDto userDto, List<Long> rolesId, List<Long> selectedStudentsForParent);
    List<UserDto> getAllUsers();
    List<StudentDto> getAllStudentsWithoutParent();

    List<RoleDto> getAllRoles();
    Boolean deleteRole(Long userId, String role);

    School updateSchool(SchoolDto school);
    SchoolDto getSchool();

}
