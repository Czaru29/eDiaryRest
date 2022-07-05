package com.ediary.services.rest;

import com.ediary.DTO.SchoolClassDto;
import com.ediary.DTO.StudentDto;
import com.ediary.DTO.TeacherDto;
import com.ediary.bootstrap.DefaultLoader;
import com.ediary.converters.ClassDtoToClass;
import com.ediary.converters.ClassToClassDto;
import com.ediary.converters.StudentToStudentDto;
import com.ediary.converters.TeacherToTeacherDto;
import com.ediary.domain.Class;
import com.ediary.domain.*;
import com.ediary.domain.helpers.GradeWeight;
import com.ediary.domain.security.Role;
import com.ediary.domain.security.User;
import com.ediary.exceptions.NotFoundException;
import com.ediary.repositories.*;
import com.ediary.repositories.security.RoleRepository;
import com.ediary.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DeputyHeadRestServiceImpl implements DeputyHeadRestService {

    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;
    private final SchoolPeriodRepository schoolPeriodRepository;
    private final StudentCouncilRepository studentCouncilRepository;
    private final ParentCouncilRepository parentCouncilRepository;
    private final EventRepository eventRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GradeRepository gradeRepository;

    private final StudentToStudentDto studentToStudentDto;
    private final TeacherToTeacherDto teacherToTeacherDto;
    private final ClassToClassDto classToClassDto;
    private final ClassDtoToClass classDtoToClass;


    @Override
    public Class saveSchoolClass(SchoolClassDto schoolClassDto) {

        if (schoolClassNameIsUnique(schoolClassDto.getName())) {
            throw new IllegalStateException("School class name is not unique: " + schoolClassDto.getName());
        }

        Class schoolClass = classDtoToClass.convertForNewClass(schoolClassDto);

        Teacher teacher = teacherRepository.findById(schoolClass.getTeacher().getId())
                .orElseThrow(() -> new NotFoundException("Teacher not found"));

        if (Objects.nonNull(teacher.getSchoolClass())) {
            throw new IllegalStateException("Teacher already has school class");
        }

        User user = userRepository.findById(teacher.getUser().getId())
                .orElseThrow(() -> new NotFoundException("User with given id not found:" + teacher.getUser().getId()));

        Set<Role> roles = user.getRoles();
        roles.add(roleRepository.findByName(DefaultLoader.FORM_TUTOR_ROLE).orElse(null));

        return classRepository.save(schoolClass);
    }

    @Transactional
    @Override
    public Boolean deleteClass(Long schoolClassId) {
        Class schoolClass = getSchoolCLass(schoolClassId);

        //Students - null
        List<Student> students = studentRepository.findAllById(schoolClass.getStudents()
                .stream()
                .map(Student::getId)
                .collect(Collectors.toList()));
        students.forEach(s -> s.setSchoolClass(null));
        studentRepository.saveAll(students);


        //Teacher - null
        Teacher teacher = teacherRepository.findById(schoolClass.getTeacher().getId()).orElse(null);

        if (teacher != null) {
            teacher.setSchoolClass(null);
            teacherRepository.save(teacher);
        }

        //Lessons
        List<Lesson> lessons = lessonRepository.findAllBySchoolClassId(schoolClass.getId());
        lessons.forEach(l -> l.setSchoolClass(null));
        lessonRepository.saveAll(lessons);


        //School periods
        List<SchoolPeriod> schoolPeriods = schoolPeriodRepository.findAllBySchoolClassId(schoolClass.getId());
        schoolPeriods.forEach(s -> s.setSchoolClass(null));
        schoolPeriodRepository.saveAll(schoolPeriods);

        //StudentCouncil - delete
        StudentCouncil studentCouncil = studentCouncilRepository.findBySchoolClassId(schoolClass.getId());
        if (studentCouncil != null) {
            studentCouncil.setStudents(null);
            studentCouncil.setSchoolClass(null);
            studentCouncilRepository.save(studentCouncil);
        }

        //ParentCouncil - delete
        ParentCouncil parentCouncil = parentCouncilRepository.findBySchoolClassId(schoolClass.getId());
        if (parentCouncil != null) {
            parentCouncil.setParents(null);
            parentCouncil.setSchoolClass(null);
            parentCouncilRepository.save(parentCouncil);
        }

        //Events - null
        List<Event> events = eventRepository.findAllBySchoolClassId(schoolClass.getId());
        events.forEach(event -> event.setSchoolClass(null));
        eventRepository.saveAll(events);

        //Subjects - null
        List<Subject> subjects = subjectRepository.findAllBySchoolClassId(schoolClass.getId());
        subjects.forEach(subject -> subject.setSchoolClass(null));
        subjectRepository.saveAll(subjects);

        schoolClass.setName(null);

        classRepository.delete(schoolClass);

        return true;
    }

    @Override
    public List<SchoolClassDto> listAllSchoolClasses(Pageable pageable) {
        return classRepository.findAll(pageable)
                .stream()
                .map(classToClassDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public SchoolClassDto getSchoolClassWithTeacher(Long classId) {
        return classToClassDto.convert(getSchoolCLass(classId));
    }

    private boolean schoolClassNameIsUnique(String schoolClassName) {
        return classRepository.existsByName(schoolClassName);
    }

    @Override
    public SchoolClassDto removeStudentFromClass(Long classId, Long studentId) {
        Class schoolClass = getSchoolCLass(classId);
        Student studentToRemove = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        if (schoolClass.getStudents().stream().anyMatch(student -> student.getId().equals(studentId))) {
            schoolClass.setStudents(schoolClass.getStudents()
                    .stream()
                    .filter((s) -> !(s.getId().equals(studentId)))
                    .collect(Collectors.toSet()));

            studentToRemove.setSchoolClass(null);
            Grade behaviorGrade = gradeRepository.findByStudentIdAndWeight(studentToRemove.getId(), GradeWeight.BEHAVIOR_GRADE.getWeight());

            if (behaviorGrade != null) {
                behaviorGrade.setStudent(null);
                gradeRepository.save(behaviorGrade);
            }

            studentRepository.save(studentToRemove);

            classRepository.save(schoolClass);
        }

        return classToClassDto.convert(schoolClass);
    }

    @Override
    public SchoolClassDto addFormTutorToSchoolClass(Long classId, Long teacherId) {
        Class schoolClass = getSchoolCLass(classId);

        Teacher currentTeacher = schoolClass.getTeacher();
        currentTeacher.setSchoolClass(null);
        currentTeacher.getUser().setRoles(currentTeacher.getUser().getRoles()
                .stream()
                .filter(role -> !role.getName().equals(DefaultLoader.FORM_TUTOR_ROLE))
                .collect(Collectors.toSet()));
        teacherRepository.save(currentTeacher);

        Teacher teacher = teacherRepository.
                findById(teacherId).orElseThrow(() -> new NotFoundException("Teacher with given id not found: " + teacherId));

        if (teacher.getSchoolClass() != null) {
            throw new NotFoundException("Teacher is already form tutor");
        } else {
            schoolClass.setTeacher(teacher);
            User user = userRepository.findById(teacher.getUser().getId())
                    .orElseThrow(() -> new NotFoundException("User with given idnot found: " + teacher.getUser().getId()));
            Set<Role> roles = user.getRoles();
            roles.add(roleRepository.findByName(DefaultLoader.FORM_TUTOR_ROLE).orElse(null));
        }

        return classToClassDto.convert(classRepository.save(schoolClass));
    }

    @Override
    public SchoolClassDto addStudentToSchoolClass(Long classId, Long studentId) {
        Class schoolClass = getSchoolCLass(classId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student with given id not found: " + studentId));

        if (student.getSchoolClass() != null) {
            throw new NotFoundException("Student has class already");
        }

        if (schoolClass.getStudents().stream().noneMatch(s -> s.getId().equals(studentId))) {
            Set<Student> newStudentSet = new HashSet<>() {{
                addAll(schoolClass.getStudents());
                add(student);
            }};

            schoolClass.setStudents(newStudentSet);
            student.setSchoolClass(schoolClass);

            classRepository.save(schoolClass);

            return classToClassDto.convert(schoolClass);
        }

        return null;
    }

    @Override
    public List<StudentDto> listAllStudentsWithoutClass(Pageable pageable) {

        return studentRepository.findAllBySchoolClassIsNull(pageable)
                .stream()
                .sorted(Comparator.comparing(student -> student.getUser().getLastName()))
                .map(studentToStudentDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<TeacherDto> listAllTeachersWithoutSchoolClass(Pageable pageable) {
        List<Teacher> availableTeachers =
                new ArrayList<>(teacherRepository.findAllBySchoolClassIsNullOrderByUser_LastName(pageable));

        if (availableTeachers.size() != 0) {
            return availableTeachers.stream()
                    .map(teacherToTeacherDto::convert)
                    .collect(Collectors.toList());
        }

        return null;
    }

    private Class getSchoolCLass(Long classId) {
        return classRepository
                .findById(classId).orElseThrow(() -> new NotFoundException("School class not found"));
    }
}
