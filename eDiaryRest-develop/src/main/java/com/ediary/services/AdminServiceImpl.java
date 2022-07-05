package com.ediary.services;

import com.ediary.DTO.*;
import com.ediary.bootstrap.DefaultLoader;
import com.ediary.converters.*;
import com.ediary.domain.*;
import com.ediary.domain.security.Role;
import com.ediary.domain.security.User;
import com.ediary.exceptions.NotFoundException;
import com.ediary.repositories.*;
import com.ediary.repositories.security.RoleRepository;
import com.ediary.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SchoolRepository schoolRepository;
    private final AddressRepository addressRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;

    private final UserToUserDto userToUserDto;
    private final UserDtoToUser userDtoToUser;
    private final RoleToRoleDto roleToRoleDto;
    private final RoleDtoToRole roleDtoToRole;
    private final SchoolToSchoolDto schoolToSchoolDto;
    private final SchoolDtoToSchool schoolDtoToSchool;
    private final StudentToStudentDto studentToStudentDto;

    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDto initNewUser() {
        return UserDto.builder().address(AddressDto.builder().build()).build();
    }

    @Override
    public User saveUser(UserDto userDto, List<Long> rolesId, List<Long> selectedStudentsForParent) {


        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new BadCredentialsException("Username already taken");
        }

        userDto.setRoles(rolesId
                .stream()
                .map((roleId) -> RoleDto.builder().id(roleId).build())
                .collect(Collectors.toList()));
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User user = userDtoToUser.convert(userDto);
        Address address = user.getAddress();
        addressRepository.save(address);
        user.setAddress(address);

        if (user == null) {
            return null;
        }

        User savedUser = userRepository.save(user);

        Set<Role> roles = user.getRoles();
        roles.forEach(role -> {
            switch (role.getName()) {
                case DefaultLoader.STUDENT_ROLE:
                    studentRepository.save(Student.builder().user(savedUser).build());
                    break;
                case DefaultLoader.PARENT_ROLE:
                    Parent savedParent = parentRepository.save(Parent.builder().user(savedUser).build());
                    if(selectedStudentsForParent != null) {
                        selectedStudentsForParent.forEach(studentId -> {
                            Student student = studentRepository.findById(studentId).orElse(null);
                            student.setParent(savedParent);
                            studentRepository.save(student);
                        });
                    }
                    break;
                case DefaultLoader.TEACHER_ROLE:
                    teacherRepository.save(Teacher.builder().user(savedUser).build());
                    break;
            }
        });

        return savedUser;
    }

    @Override
    public Boolean deleteUser(Long userId) {

        User user = getUserById(userId);

        user.setUsername(null);
        user.setPassword(null);
        user.setAddress(null);

        userRepository.save(user);

        return true;
    }

    @Override
    public UserDto getUser(Long userId) {
        return userToUserDto.convertForAdmin(getUserById(userId));
    }

    @Override
    public UserDto updateUser(UserDto userUpdated, List<Long> rolesId, List<Long> selectedStudentsForParent) {

        Optional<User> userOptional = userRepository.findById(userUpdated.getId());

        if (!userOptional.isPresent()) {
            throw new NotFoundException("User Not Found.");
        }

        UserDto userDto = userToUserDto.convertForAdmin(userOptional.get());

        if (userUpdated.getName() != null && userUpdated.getName() != userDto.getName())
            userDto.setName(userUpdated.getName());


        if (userUpdated.getAddress() != null) {
            userDto.setAddress(userUpdated.getAddress());
        }

        if (userUpdated.getPassword() != null && passwordEncoder.encode(userUpdated.getPassword()) != userDto.getPassword())
            userDto.setPassword(passwordEncoder.encode(userUpdated.getPassword()));

        User user = userDtoToUser.convert(userDto);

        Set<Role> roles = user.getRoles();


        if (rolesId != null) {
            rolesId.forEach(roleId -> {
                Role role = roleDtoToRole.convert(RoleDto.builder().id(roleId).build());
                roles.add(role);
                switch (role.getName()) {
                    case DefaultLoader.STUDENT_ROLE:
                        studentRepository.save(Student.builder().user(user).build());
                        break;
                    case DefaultLoader.PARENT_ROLE:
                        Parent savedParent = parentRepository.save(Parent.builder().user(user).build());
                        if (selectedStudentsForParent != null) {
                            selectedStudentsForParent.forEach(studentId -> {
                                Student student = studentRepository.findById(studentId).orElse(null);
                                student.setParent(savedParent);
                                studentRepository.save(student);
                            });
                        }
                        break;
                    case DefaultLoader.TEACHER_ROLE:
                        teacherRepository.save(Teacher.builder().user(user).build());
                        break;
                }
            });
        }
        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        return userToUserDto.convert(savedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userToUserDto::convertForAdmin)
                .collect(Collectors.toList());

    }

    @Override
    public List<StudentDto> getAllStudentsWithoutParent() {
        return studentRepository.findAllByParentIsNull()
                .stream()
                .map(studentToStudentDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleToRoleDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean deleteRole(Long userId, String role) {

        User user = getUserById(userId);
        Set<Role> ownedRoles = user.getRoles();

        //student, parent, teacher
        switch (role) {
            case DefaultLoader.STUDENT_ROLE:
                Student student = studentRepository.findByUser(user).orElse(null);
                if (student != null) {
                    User newUser = userRepository.save(User.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .build());
                    student.setUser(newUser);
                    studentRepository.save(student);
                } else {
                    return false;
                }
                break;
            case DefaultLoader.PARENT_ROLE:
                Parent parent = parentRepository.findByUser(user).orElse(null);
                if (parent != null) {
                    if (parent.getStudents() != null) {
                        parent.getStudents().forEach(parentsStudent -> {
                            parentsStudent.setParent(null);
                            studentRepository.save(parentsStudent);
                        });
                    }
                    User newUser = userRepository.save(User.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .build());
                    parent.setUser(newUser);
                    parentRepository.save(parent);
                } else {
                    return false;
                }
                break;
            case DefaultLoader.TEACHER_ROLE:
                Teacher teacher = teacherRepository.findByUser(user).orElse(null);
                if (teacher != null) {
                    User newUser = userRepository.save(User.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .build());
                    teacher.setUser(newUser);
                    teacherRepository.save(teacher);
                } else {
                    return false;
                }
                break;
        }

        //admin, deputy_head, headmaster
        Role roleToDelete = roleRepository.findByName(role).orElse(null);
        if (roleToDelete == null) {
            return false;
        }
        ownedRoles.remove(roleToDelete);
        user.setRoles(ownedRoles);
        userRepository.save(user);

        return true;
    }

    @Override
    public School updateSchool(SchoolDto school) {
        return schoolRepository.save(schoolDtoToSchool.convert(school));
    }

    @Override
    public SchoolDto getSchool() {
        return schoolToSchoolDto.convert(schoolRepository.findAll()
                .stream()
                .findFirst().orElseThrow(() -> new NotFoundException("No school has been found")));
    }


    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

}
