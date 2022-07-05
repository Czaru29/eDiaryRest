package com.ediary.bootstrap;

import com.ediary.domain.Class;
import com.ediary.domain.*;
import com.ediary.domain.security.Role;
import com.ediary.domain.security.User;
import com.ediary.domain.timetable.Classroom;
import com.ediary.domain.timetable.Day;
import com.ediary.domain.timetable.Duration;
import com.ediary.repositories.*;
import com.ediary.repositories.security.RoleRepository;
import com.ediary.repositories.security.UserRepository;
import com.ediary.repositories.timetable.ClassroomRepository;
import com.ediary.repositories.timetable.DayRepository;
import com.ediary.repositories.timetable.DurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
//@Component
public class NotDefaultLoader implements CommandLineRunner {

    private final AddressRepository addressRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final ParentCouncilRepository parentCouncilRepository;
    private final StudentCouncilRepository studentCouncilRepository;
    private final ExtenuationRepository extenuationRepository;
    private final AttendanceRepository attendanceRepository;
    private final MessageRepository messageRepository;
    private final NoticeRepository noticeRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final BehaviorRepository behaviorRepository;
    private final EventRepository eventRepository;
    private final ReportRepository reportRepository;
    private final StudentCardRepository studentCardRepository;
    private final DayRepository dayRepository;
    private final ClassroomRepository classroomRepository;
    private final DurationRepository durationRepository;
    private final SchoolPeriodRepository schoolPeriodRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private Long firstUserAsStudentId;
    private Long firstUserAsParentId;
    private Long firstUserAsTeacherId;
    private final int classSize = 15;

    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String STUDENT_ROLE = "ROLE_STUDENT";
    public static final String PARENT_ROLE = "ROLE_PARENT";
    public static final String TEACHER_ROLE = "ROLE_TEACHER";
    public static final String FORM_TUTOR_ROLE = "ROLE_FORM_TUTOR";
    public static final String DEPUTY_HEAD_ROLE = "ROLE_DEPUTY_HEAD";
    public static final String HEADMASTER_ROLE = "ROLE_HEADMASTER";

    private final String[] streetNames = {"Odrodzenia", "Jagiellońska", "Kaszubska", "Jana Pawła", "Borysza", "Wielkopolska", "Podhalańska", "Odrodzenia",
            "Borysza", "Mazowiecka", "Kujawska", "Mazowiecka", "Wolności", "Wysoka", "Borysza", "Kameralna", "Opolska", "Boczna", "Boczna", "Kasztanowa",
            "Barwna", "Nurkowa", "Basenowa", "Stokrotki", "Biała", "Pawia", "Celna", "Kozia", "Kameralna", "Wolności", "Opolska", "Solna", "Leszczynowa",
            "Sucha", "Cieszyńska", "Cmentarna", "Jaskółcza", "Klińskiego Jana", "Piłusudskiego", "Kowalska", "Bydgoska", "Urocza", "Radosna", "Topolowa",
            "Powstańców śląskich", "Sobieskiego Jana", "Podwale", "Spółdzielcza", "Częstochowska", "Polna"
    };

    private final String[] phoneNumber = {"502571821", "515556095", "505735402", "575120854", "455558291", "785152102", "735592154", "509124854", "571943111",
            "710430235", "509570183", "730852128", "515152532", "593561943", "920153853", "699921194", "609521531", "521042153", "681251023", "501545145",
            "795550352", "535555325", "605555033", "885552452", "535555657", "695555751", "735552429", "665556237", "455550134", "455559076", "521531995",
            "500617215", "961703062", "556573327", "886479270", "701039358", "639104598", "670333055", "963549255", "501383396", "631766249", "588396694",
            "552074340", "729422227", "926420432", "699142980", "518555027", "708933176", "500204467", "682366324"

    };

    private final String[] studentNames = {"Dagmara", "Daniel", "Alicja", "Olgierd", "Kamil", "Olga", "Michał", "Piotr", "Adrian", "Adam", "Dominika", "Kinga",
            "Weronika", "Daniel", "Damian"
    };

    private final String[] studentLastNames = {"Lis", "Mazur", "Skrzeczkowska", "Szymczak", "Kubiak", "Woźniak", "Kalinowski", "Michalak", "Kołodziej", "Baran",
            "Mróz", "Włodarczyk", "Kaczmarek", "Kowalski", "Zawadzki"
    };

    private final String[] parentNames = {"Marian", "Ludwik", "Konrad", "Amir", "Irena", "Rafał", "Jan", "Barbara", "Agnieszka", "Wanda", "Krystyna", "Jakub",
            "Józef", "Izabela", "Zuzanna", "Oskar", "Rafał", "Diana", "Liliana", "Błażej", "Kuba", "Daniel", "Andrzej", "Jarosław", "Cyprian", "Eryk", "Albert",
            "Marcel", "Kamil", "Alan", "Joachim", "Leszek", "Robert", "Filip", "Ksawery", "Anita", "Marcelina", "Roksana", "Eleonora", "Bogda", "Helena", "Pamela",
            "Lila", "Marysia", "Andżelika", "Daria", "Eliza", "Klementyna", "Urszula", "Izabella"
    };

    private final String[] parentLastNames = {"Lis", "Mazur", "Skrzeczkowski", "Szymczak", "Kubiak", "Woźniak", "Kalinowski", "Michalak", "Kołodziej", "Baran",
            "Mróz", "Włodarczyk", "Kaczmarek", "Kowalska", "Zawadzka", "Nowak", "Kubiak", "Mazurek", "Kaźmierczuk", "Głowacki", "Woźniak", "Lis", "Rutkowski",
            "Rutkowski", "Jasiński", "Cieślak", "Mróz", "Lis", "Woźniak", "Przybylski", "Wróblewski", "Zakrzewski", "Nowak", "Górski", "Mazurek",
            "Urbańska", "Górecka", "Zawadzka", "Sikorska", "Wiśniewska", "Gajewska", "Czerwińska", "Chmielewska", "Kucharska", "Laskowska", "Kołodzieczyk",
            "Kowalska", "Wiśniewska", "Szczepańska", "Mazurek"
    };

    private final String[] teacherNames = {"Czesław", "Rafał", "Maciej", "Magdalena", "Krzysztoł", "Katarzyna", "Roman", "Gabriel", "Marcin", "Paweł", "Ryszard",
            "Frydek", "Miron", "Ryszard", "Maurycy", "Robert", "Remigiusz", "Marcin", "Karol", "Ksawery", "Alojzy", "Czesław", "Kacper", "Marcel", "Igor",
            "Marian", "Denis", "Borys", "Joachim", "Milena", "Żaneta", "Jola", "Anna", "Barbara", "Eliza", "Wiktoria", "Kaja", "Lucyna", "Marlena", "Klaudia",
            "Julita", "Alina", "Wanda", "Ida", "Diana", "Kinga", "Natalia", "Monika", "Halina", "Karolina"
    };

    private final String[] teacherLastNames = {"Wójcik", "Marciniak", "Jankowski", "Kowalska", "Michalak", "Sikora", "Tomaszewski", "Andrzejewski", "Borkowski",
            "Wróblewski", "Kubiak", "Bąk", "Kowalski", "Dąbrowski", "Głowacki", "Kaczmarczyk", "Lis", "Sobczak", "Wasilewski", "Mazur", "Cieślak", "Czarnecki", "Sadowski",
            "Wysocki", "Krupa", "Makowski", "Szymański", "Kozłowski", "Nowak", "Wójcik", "Sadowska", "Szczepańska", "Lewandowska", "Kubiak", "Kowalczyk", "Baran",
            "Jaworska", "Pietrzak", "Kuchniczak", "Bąk", "Głowacka", "Sawicka", "Zielińska", "Mróz", "Pawlak", "Duda", "Gajewska", "Szulc", "Stępień", "Ostrowska"
    };

    private final String[] classNames = {"1a", "1b", "1c", "1d", "1e", "1f", "1g", "2a", "2b", "2c", "2d", "2e", "2f", "2g",
            "3a", "3b", "3c", "3d", "3e", "3f", "3g", "4a", "4b", "4c", "4d", "4e", "4f", "4g", "5a", "5b", "5c", "5d", "5e", "5f", "5g",
            "6a", "6b", "6c", "6d", "6e", "6f", "6g", "7a", "7b", "7c", "7d", "7e", "7f", "7g", "8a"
    };

    private final String[] subjectNames = {"Matematyka", "Historia", "Informatyka", "Plastyka", "Muzyka", "Język angielski", "Wychowanie fizyczne",
            "Przyroda", "Biologia", "Technika", "Język Polski", "Plastyka", "Religia", "Wychowanie do życia w rodzinie", "Godzina Wychowawcza", "Geografia",
            "Chemia", "Fizyka", "Język Niemiecki"
    };

    private final String[] topicNamesMath = {"Dodawanie", "Dzielenie", "Mnożenie", "Odejmowanie"};


    @Override
    public void run(String... args) {
        log.debug("-------------- Bootstrap --------------");

        createRole();
        createAddresses();
        createSchool();
        createUsers();
        createStudentsAndParents();
        createTeachers();
//        createParentCouncils();
//        createStudentCouncils();
//        createExtenuations();
//        createAttendances();
        createMessages();
        createNotices();
        createClasses();
        addStudentsToClasses();
        createSubjects();
//        Uncomment for grades (takes time)
        createGrades();
        createTopics();
        //createLessons();
        createBehaviors();
        createEvents();
//        createReports();
//        createStudentCards();
//        Uncomment all below for school periods
        createDays();
        createClassrooms();
        createDurations();
        //createSchoolPeriods();

        log.debug("-------------- Bootstrap is done --------------");
    }

    private void createRole() {
        Role adminRole = roleRepository.save(Role.builder().name(ADMIN_ROLE).build());
        Role studentRole = roleRepository.save(Role.builder().name(STUDENT_ROLE).build());
        Role parentRole = roleRepository.save(Role.builder().name(PARENT_ROLE).build());
        Role teacherRole = roleRepository.save(Role.builder().name(TEACHER_ROLE).build());
        Role formTutorRole = roleRepository.save(Role.builder().name(FORM_TUTOR_ROLE).build());
        Role deputyHeadRole = roleRepository.save(Role.builder().name(DEPUTY_HEAD_ROLE).build());
        Role headmasterRole = roleRepository.save(Role.builder().name(HEADMASTER_ROLE).build());
    }

    private void createAddresses() {
        String state = "Poland";
        String[] cityNames = {"Szczecin"};
        String zip = "70-670";


        for(int i = 0; i < streetNames.length; i++) {
            addressRepository.save(Address.builder()
                    .state(state)
                    .city(cityNames[0])
                    .zip(zip)
                    .street(streetNames[i])
                    .phoneNumber(phoneNumber[i])
                    .build());
        }

        log.debug("-------- Created addresses: " + addressRepository.count());
    }



    private void createSchool() {
        schoolRepository.save(School.builder()
                .name("Darwin College")
                .headmasterName("Danuta")
                .headmasterLastName("Adamska")
                .email("d.adamska@dc.com")
                .schoolOffice("")
                .address(addressRepository.findByStreetAndPhoneNumber(streetNames[0], phoneNumber[0]))
                .build());
    }

    private void createUsers() {

        userRepository.save(User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .role(roleRepository.findByName(ADMIN_ROLE).orElse(null))
                .username("user")
                .password(passwordEncoder.encode("user")).build());


        //Students
        for (int i = 0; i < studentNames.length; i++) {
            userRepository.save(User.builder()
                    .firstName(studentNames[i])
                    .lastName(studentLastNames[i])
                    .username(studentNames[i])
                    .password(passwordEncoder.encode(studentLastNames[i]))
                    .build());
        }


        //Parents
        Long sizeOfAddressTable = addressRepository.count();
        Long addressId = addressRepository.findByStreetAndPhoneNumber(streetNames[sizeOfAddressTable.intValue()],
                phoneNumber[sizeOfAddressTable.intValue()]).getId();
        for (int i = 0; i < 20; i++, addressId++) {
            userRepository.save(User.builder()
                    .firstName(parentNames[i])
                    .lastName(parentLastNames[i])
                    .username(parentNames[i])
                    .password(passwordEncoder.encode(parentLastNames[i]))
                    .address(addressRepository.findById(addressId).orElse(null))
                    .build());

        }

        //Teachers
        sizeOfAddressTable = addressRepository.count();
        addressId = addressRepository.findByStreetAndPhoneNumber(streetNames[sizeOfAddressTable.intValue()], phoneNumber[sizeOfAddressTable.intValue()]).getId();
        for (int i = 0; i < 20; i++) {
            userRepository.save(User.builder()
                    .firstName(teacherNames[i])
                    .lastName(teacherLastNames[i])
                    .username(teacherNames[i])
                    .password(passwordEncoder.encode(teacherLastNames[i]))
                    .address(addressRepository.findById(addressId++).orElse(null))
                    .build());
        }

        //DeputyHead + Headmaster
        userRepository.save(User.builder()
                .firstName("DeputyHead")
                .lastName("DeputyHead")
                .role(roleRepository.findByName(DEPUTY_HEAD_ROLE).orElse(null))
                .username("deputy")
                .password(passwordEncoder.encode("deputy")).build());

        userRepository.save(User.builder()
                .firstName("Headmaster")
                .lastName("Headmaster")
                .role(roleRepository.findByName(HEADMASTER_ROLE).orElse(null))
                .username("headmaster")
                .password(passwordEncoder.encode("headmaster")).build());


        firstUserAsStudentId = userRepository.findByFirstNameAndLastName(studentNames[0], studentLastNames[0]).getId();
        firstUserAsParentId = userRepository.findByFirstNameAndLastName(parentNames[0], parentLastNames[0]).getId();
        firstUserAsTeacherId = userRepository.findByFirstNameAndLastName(teacherNames[0], teacherLastNames[0]).getId();


        log.debug("-------- Created users: " + userRepository.count());

    }

    //todo: classes for student
    private void createStudentsAndParents() {

        long userAsStudentId = firstUserAsStudentId;
        long userAsParentId = firstUserAsParentId;

        for (; userAsStudentId < firstUserAsStudentId + studentNames.length; userAsStudentId++, userAsParentId++) {
            User user = userRepository.findById(userAsStudentId).orElse(null);

            Set<Role> roles = user.getRoles();
            roles.add(roleRepository.findByName(STUDENT_ROLE).orElse(null));

            user.setRoles(roles);
            userRepository.save(user);

            studentRepository.save(Student.builder()
                    .user(userRepository.findById(userAsStudentId).orElse(null))
                    .parent(createParent(userAsParentId))
                    .build());
        }

        log.debug("-------- Created students: " + studentRepository.count());
        log.debug("-------- Created parents: " + parentRepository.count());

    }


    private Parent createParent(Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        Set<Role> roles = user.getRoles();
        roles.add(roleRepository.findByName(PARENT_ROLE).orElse(null));

        user.setRoles(roles);
        userRepository.save(user);

        return parentRepository.save(Parent.builder()
                .user(userRepository.findById(userId).orElse(null))
                .build());
    }


    private void createTeachers() {

        for (long userAsTeacherId = firstUserAsTeacherId; userAsTeacherId < 20; userAsTeacherId++) {

            User user = userRepository.findByFirstNameAndLastName(teacherNames[(int)userAsTeacherId-95], teacherLastNames[(int)userAsTeacherId-95]);

            Set<Role> roles = user.getRoles();
            roles.add(roleRepository.findByName(TEACHER_ROLE).orElse(null));

            user.setRoles(roles);
            userRepository.save(user);

            teacherRepository.save(Teacher.builder()
                    .user(userRepository.findByFirstNameAndLastName(teacherNames[(int)userAsTeacherId-95], teacherLastNames[(int)userAsTeacherId-95])).build());
        }

        log.debug("-------- Created teachers: " + teacherRepository.count());

    }


    private void createParentCouncils() {
        for(int i = 0; i < classNames.length; i++) {
            parentCouncilRepository.save(ParentCouncil.builder()
                    .build());
        }
    }


    private void createStudentCouncils() {
        for(int i = 0; i < classNames.length; i++) {
            studentCouncilRepository.save(StudentCouncil.builder()
                    .build());
        }
    }



    private void createExtenuations() {

        extenuationRepository.save(Extenuation.builder()
                .parent(parentRepository
                        .findByUserId(userRepository.findByFirstNameAndLastName(parentNames[0], parentLastNames[0]).getId()))
                .status(Extenuation.Status.SENT)
                .build());


        extenuationRepository.save(Extenuation.builder()
                .parent(parentRepository
                        .findByUserId(userRepository.findByFirstNameAndLastName(parentNames[1], parentLastNames[1]).getId()))
                .status(Extenuation.Status.SENT)
                .build());

        log.debug("Created extenuations: " + extenuationRepository.count());
    }



    //todo: lesson
    private void createAttendances() {

        //Attendances for 1st class students
        Attendance.Status[] statuses1stClass = {
                Attendance.Status.ABSENT, Attendance.Status.ABSENT, Attendance.Status.PRESENT, Attendance.Status.LATE,
                Attendance.Status.PRESENT, Attendance.Status.EXCUSED, Attendance.Status.ABSENT, Attendance.Status.PRESENT,
                Attendance.Status.PRESENT, Attendance.Status.PRESENT, Attendance.Status.PRESENT, Attendance.Status.LATE,
                Attendance.Status.PRESENT, Attendance.Status.LATE, Attendance.Status.PRESENT, Attendance.Status.PRESENT,
                Attendance.Status.PRESENT, Attendance.Status.PRESENT, Attendance.Status.PRESENT, Attendance.Status.LATE
        };


        for (int i = 0; (i < (i + classSize) && i < studentNames.length); i++) {
            attendanceRepository.save(Attendance.builder()
                    .student(studentRepository
                            .findByUserId(userRepository.findByFirstNameAndLastName(studentNames[i], studentLastNames[i]).getId()))
                    .status(statuses1stClass[i])
                    .build());

        }

        for (int i = 0; i < 5; i++) {
            attendanceRepository.save(Attendance.builder()
                    .student(studentRepository
                            .findByUserId(userRepository.findByFirstNameAndLastName(studentNames[0], studentLastNames[0]).getId()))
                    .status(statuses1stClass[i])
                    .build());

        }


        //Each student in 2nd class is present
        Attendance.Status statuses2ndClass = Attendance.Status.PRESENT;

        for (int i = classSize; (i < (i + classSize) && i < studentNames.length) ; i++) {
            attendanceRepository.save(Attendance.builder()
                    .student(studentRepository
                            .findByUserId(userRepository.findByFirstNameAndLastName(studentNames[i], studentLastNames[i]).getId()))
                    .status(statuses2ndClass)
                    .build());

        }


        log.debug("Created attendances: " + attendanceRepository.count());
    }


    private void createMessages() {
        int[] senderIndexes = {0, 0, 1};
        int[] readerIndexes = {3, 3, 3};
        String[] messageTitles = {"Cześć", "Dzień dobry", "Kolejna wiad"};
        String[] messageContens = {"To jest testowa wiadomość", "Dzień dobry wiadomość", "Kolejna wiadomość testowa"};
        Message.Status[] statuses = {Message.Status.SENT, Message.Status.READ, Message.Status.SENT};

        for (int j = 0; j < 10; j++) {
            for (int i = 0; i < senderIndexes.length; i++) {

                messageRepository.save(Message.builder()
                        .title(messageTitles[i])
                        .content(messageContens[i])
                        .status(statuses[i])
                        .date(new java.util.Date())
                        .senders(userRepository.findByFirstNameAndLastName(teacherNames[senderIndexes[i]], teacherLastNames[senderIndexes[i]]))
                        .reader(userRepository.findByFirstNameAndLastName(teacherNames[readerIndexes[i]], teacherLastNames[readerIndexes[i]]))
                        .build());
            }
        }

        for (int j = 0; j < 20; j++) {
            messageRepository.save(Message.builder()
                    .title(messageTitles[0])
                    .content(messageContens[0])
                    .status(Message.Status.READ)
                    .date(new java.util.Date())
                    .senders(userRepository.findByFirstNameAndLastName(teacherNames[1], teacherLastNames[1]))
                    .reader(userRepository.findByFirstNameAndLastName(teacherNames[0], teacherLastNames[0]))
                    .build());
        }


        String loremIpsum = " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean convallis urna " +
                "egestas dapibus condimentum. Ut vestibulum sem non ultricies elementum. Pellentesque accumsan tellus " +
                "a libero mollis, et ornare mi maximus. Donec aliquet tempor posuere. Pellentesque laoreet tempus massa, " +
                "eget auctor augue pharetra quis. Vivamus eu magna ligula. Mauris eget eros quis nulla faucibus auctor. " +
                "Phasellus ac mi nec diam dapibus auctor.In hac habitasse platea dictumst. Sed tristique, ipsum sit amet " +
                "pharetra laoreet, elit odio euismod dui, vel finibus diam eros non neque. Pellentesque habitant morbi tristique " +
                "senectus et netus et malesuada fames ac turpis egestas. Proin vel ornare mi, luctus efficitur nisl. " +
                "Suspendisse ligula dui, dictum eu lorem ac, blandit faucibus urna. Nam at fringilla turpis. " +
                "Quisque eget diam vel magna fermentum ultrices. Vestibulum ornare, lacus sed mattis semper, " +
                "urna erat sagittis nibh, sagittis dictum nisl ex sit amet nisi. Donec egestas eu urna id pretium. " +
                "Aliquam diam erat, lobortis sed ante in, tincidunt viverra mi. Nunc pretium sagittis finibus. " +
                "Aliquam eget sodales enim. Sed suscipit sed quam et scelerisque. Vivamus pharetra vulputate " +
                "magna quis sollicitudin. Curabitur congue lectus sodales mollis ornare. Etiam arcu purus, fermentum " +
                "et gravida et, porttitor in elit. Sed turpis mauris, egestas ut elit in, tincidunt hendrerit augue. " +
                "Mauris tincidunt tempus tempus. Donec lacinia lorem ac tristique pharetra. Vestibulum dictum " +
                "nisi ut sodales imperdiet. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere " +
                "cubilia curae; ";

        messageRepository.save(Message.builder()
                .title("Long")
                .content(loremIpsum)
                .status(Message.Status.SENT)
                .date(new java.util.Date())
                .senders(userRepository.findByFirstNameAndLastName(teacherNames[0], teacherLastNames[0]))
                .reader(userRepository.findByFirstNameAndLastName(teacherNames[3], teacherLastNames[3]))
                .build());
    }


    private void createNotices() {
        int[] teacherIndexes = {3, 2, 5, 1};
        String[] noticeTitle = {"Nowa wiadomość", "Nowe wydarzenie", "Nowe wydarzenie", "Nowa wiadomość"};
        String[] noticeContent = {"Ogłoszenie 1 ", "Zaplanowano nowe wydarzenie",
                "Zaplanowano nowe wydarzenie", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam tincidunt, purus a egestas porttitor, ante nunc aliquam erat, at accumsan lorem odio id arcu. Vestibulum placerat gravida neque vel aliquam. Aliquam erat volutpat. Praesent ut consequat erat, ac dignissim odio. Quisque commodo arcu ipsum, non elementum ligula rutrum ac. Nunc a mattis lectus. Curabitur pulvinar dignissim egestas. Sed in leo vitae nibh egestas pulvinar ut egestas ligula. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla lorem dolor, commodo eu vestibulum vitae, varius id lorem. Morbi eleifend quam a sapien rutrum, eu commodo sem maximus. Vivamus non tellus luctus, aliquam tortor non, ultrices dui. Praesent rutrum nisl id ex sagittis, sed bibendum erat scelerisque. "};


        for (int i = 0; i < teacherIndexes.length; i++) {
            noticeRepository.save(Notice.builder()
                    .title(noticeTitle[i])
                    .content(noticeContent[i])
                    .date(new java.util.Date())
                    .user(userRepository.findByFirstNameAndLastName(teacherNames[teacherIndexes[i]], teacherLastNames[teacherIndexes[i]]))
                    .build());
        }

    }


    private void createClasses() {
        for (int i = 0; i < 20; i++) {
            classRepository.save(Class.builder()
                    .name(classNames[i])
                    .teacher(teacherRepository.findByUserId(userRepository
                            .findByFirstNameAndLastName(teacherNames[i], teacherLastNames[i]).getId()))
//                    .parentCouncil(parentCouncilRepository.findAll().get(i))
//                    .studentCouncil(studentCouncilRepository.findAll().get(i))
                    .build());

            User user = userRepository.findByFirstNameAndLastName(teacherNames[i], teacherLastNames[i]);

            Set<Role> roles = user.getRoles();
            roles.add(roleRepository.findByName(FORM_TUTOR_ROLE).orElse(null));

            user.setRoles(roles);
            userRepository.save(user);
        }

    }


    private void createSubjects() {
        //indexes of teachers associated with given subjects
        int[] teacherSubjectIndexes = {0, 2, 0, 3, 4, 5, 2, 1, 1, 3};

        for (int i = 0; i < subjectNames.length; i++) {
            subjectRepository.save(Subject.builder()
                    .name(subjectNames[i])
                    .build());

        }

        //Adding teachers to subjects
        List<Subject> savedSubjects = subjectRepository.findAll();

        for (int i = 0; i < 10; i++) {
            savedSubjects.get(i).setTeacher(
                    teacherRepository.findByUserId(userRepository
                            .findByFirstNameAndLastName(
                                    teacherNames[teacherSubjectIndexes[i]],
                                    teacherLastNames[teacherSubjectIndexes[i]]).getId())
            );

            savedSubjects.get(i).setSchoolClass(classRepository.findByName(classNames[0]));
        }


        subjectRepository.saveAll(savedSubjects);

    }

    private void createGrades() {

        log.debug("Creating grades...");

        //Grades for first subject -> Matematyka
        int[][] gradesPerStudent = {
                {2, 4, 6, 2, 4, 1, 2, 2},
                {3, 3, 4, 5, 3, 2, 4, 6, 3, 1, 4, 5},
                {5, 2, 5, 1, 5, 2, 5, 5},
                {2, 4, 4, 2, 4},
                {4, 2, 1, 2, 4, 2, 1, 3, 3},
                {6, 5, 5, 5, 5},
                {2, 5, 2, 5, 1, 2, 2},
                {5, 6, 5, 4, 4, 5, 6},
                {2, 4, 5, 3, 2, 3, 2},
                {4, 4, 4, 4, 3, 4, 6},
                {2, 3, 3, 4, 2, 3},
                {5, 5, 5, 5, 6, 5, 6},
                {1, 2, 2},
                {3, 4, 4, 4, 3, 5, 4},
                {1, 5, 3, 3},
        };

        int[] gradeWeight = {1,2,3};

        String[] descriptions = {"Kartkówka", "Kartkówka", "Sprawdzian", "Aktywność", "Aktywność",
                "Kartkówka", "Sprawdzian", "Aktywność", "Aktywność", "Kartkówka", "Aktywność", "Aktywność"};


        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < gradesPerStudent[i].length; j++) {

                gradeRepository.save(Grade.builder()
                        .value(gradesPerStudent[i][j] + "")
                        .weight(gradeWeight[j%3])
                        .description(descriptions[j])
                        .date(Date.valueOf(LocalDate.now()))
                        .student(studentRepository.findByUserId(userRepository
                                .findByFirstNameAndLastName(studentNames[i], studentLastNames[i]).getId()))
                        .teacher(teacherRepository.findByUserId(userRepository
                                .findByFirstNameAndLastName(
                                        teacherNames[i % teacherNames.length],
                                        teacherLastNames[i % teacherNames.length]).getId()))
                        .subject(subjectRepository.findByName(subjectNames[0]))
                        .build());
            }
        }

        gradeRepository.save(Grade.builder()
                .value(2 + "")
                .weight(gradeWeight[0])
                .description("Kartkówka")
                .date(Date.valueOf(LocalDate.now()))
                .student(studentRepository.findByUserId(userRepository
                        .findByFirstNameAndLastName(studentNames[0], studentLastNames[0]).getId()))
                .teacher(teacherRepository.findByUserId(userRepository
                        .findByFirstNameAndLastName(
                                teacherNames[1],
                                teacherLastNames[1]).getId()))
                .subject(subjectRepository.findByName(subjectNames[2]))
                .build());


        //Time consuming, commented by default, uncomment for more data
//        log.debug("Just a few more..");
//        createRandomGrades();
        log.debug("Created grades: " + gradeRepository.count());
    }

    private void createRandomGrades() {

        Random random = new Random(System.currentTimeMillis());
        String[] descriptions = {"Kartkówka", "Sprawdzian", "Aktywność"};

        for (int k = 0; k < subjectNames.length; k++) {
            for (int i = 0; i < studentNames.length; i++) {
                for (int j = 0; j < random.nextInt(5); j++) {
                    gradeRepository.save(Grade.builder()
                            .value((random.nextInt(5) + 1) + "")
                            .description(descriptions[random.nextInt(3)])
                            .date(Date.valueOf(LocalDate.now()))
                            .student(studentRepository.findByUserId(userRepository
                                    .findByFirstNameAndLastName(studentNames[i], studentLastNames[i]).getId()))
                            .teacher(teacherRepository.findByUserId(userRepository
                                    .findByFirstNameAndLastName(
                                            teacherNames[i % teacherNames.length],
                                            teacherLastNames[i % teacherNames.length]).getId()))
                            .subject(subjectRepository.findByName(subjectNames[k]))
                            .build());
                }
            }
        }
    }

    /** Creating maths' topics **/
    private void createTopics() {
        String[] descriptions = {"Jak dodawać liczby", "Jak dzielić liczby", "Jak mnożyć liczby", "Jak odejmować liczby"};


        for (int i = 0; i < topicNamesMath.length; i++) {
            topicRepository.save(Topic.builder()
                    .number((topicRepository.count() + 1))
                    .name(topicNamesMath[i])
                    .description(descriptions[i])
                    .subject(subjectRepository.findByName(subjectNames[0]))
                    .build());
        }

        topicRepository.save(Topic.builder()
                .number((topicRepository.count() + 1))
                .name("Test Topic")
                .description("How to in JPA")
                .subject(subjectRepository.findByName(subjectNames[2]))
                .build());
    }

    /** Creating maths' lessons : class id: 0 **/
    private void createLessons() {
        String[] lessonNames = {"Dodawanie", "Dzielenie", "Mnożenie", "Odejmowanie"};
        int[] schoolClassIndexes = {0, 0, 0, 0};
        Attendance.Status status[] = {Attendance.Status.PRESENT, Attendance.Status.PRESENT, Attendance.Status.LATE, Attendance.Status.UNEXCUSED};

        Long studentId = studentRepository
                .findByUserId(userRepository.findByFirstNameAndLastName(studentNames[0], studentLastNames[0]).getId()).getId();

        List<Attendance> attendances = attendanceRepository.findAllByStudentId(studentId);

        for (int i = 0; i < lessonNames.length; i++) {
            lessonRepository.save(Lesson.builder()
                    .name(lessonNames[i])
                    .date(Date.valueOf(LocalDate.now().minusDays(i)))
                    .subject(subjectRepository.findByName(subjectNames[0]))
                    .schoolClass(classRepository.findByName(classNames[schoolClassIndexes[i]]))
                    .topic(topicRepository.findByName(topicNamesMath[i]))
                    .attendances(new HashSet<>(attendances))
                    .build());

        }
        for (int i = 0; i < lessonNames.length; i++) {
            lessonRepository.save(Lesson.builder()
                    .name(lessonNames[i])
                    .date(Date.valueOf(LocalDate.now().minusDays(i)))
                    .subject(subjectRepository.findByName(subjectNames[3]))
                    .schoolClass(classRepository.findByName(classNames[schoolClassIndexes[i]]))
                    .topic(topicRepository.findByName(topicNamesMath[i]))
                    .attendances(new HashSet<>(attendances))
                    .build());
        }


//        //Adding attendances to lesson (all students)
//        for (int j = 0; j < studentNames.length; j++) {
//            for (int i = 0; i < 4; i++) {
//                attendanceRepository.save(Attendance.builder()
//                        .student(studentRepository
//                                .findByUserId(userRepository.findByFirstNameAndLastName(studentNames[j], studentLastNames[j]).getId()))
//                        .status(Attendance.Status.ABSENT)
//                        .lesson(lessonRepository.findAllBySchoolClassId(classRepository.findByName(classNames[0]).getId()).get(i))
//                        .build());
//            }
//        }


        //Adding attendances to lesson (studentId = 0)
        for (int i = 0; i < 4; i++) {
            attendanceRepository.save(Attendance.builder()
                    .student(studentRepository
                            .findByUserId(userRepository.findByFirstNameAndLastName(studentNames[0], studentLastNames[0]).getId()))
                    .status(status[i])
                    .lesson(lessonRepository.findAllBySchoolClassId(classRepository.findByName(classNames[0]).getId()).get(i))
                    .build());
        }


    }

    /** Creating behavior : teacher -> id:2, student: id: 1-5 **/
    private void createBehaviors() {
        String[] content = {"", "Uczeń śpiewał na lekcji i bujał się na krześle. Nie reagował na prośby nauczyciela, " +
                "Następnie uczeń, wziął kredę spod tablicy, starł ją na własnym biurku i ustawil w kreskę " +
                "po czym wyjął banknot z portfela i zaczął wciągać kredę nosem. ", "złe zachowanie", "złe zachowanie", "złe zachowanie", "", "", "złe zachowanie"};
        boolean[] positive = {true, false, false, false, false, true, true, false};
        int[] studentIndexes = {1, 2, 2, 2, 2, 4, 5, 3};

        for (int i = 0; i < content.length; i++) {
            behaviorRepository.save(Behavior.builder()
                    .date(Date.valueOf(LocalDate.now()))
                    .content(content[i])
                    .positive(positive[i])
                    .teacher(teacherRepository.findByUserId(userRepository
                            .findByFirstNameAndLastName(teacherNames[2], teacherLastNames[2]).getId()))
                    .student(studentRepository.findByUserId(userRepository
                            .findByFirstNameAndLastName(studentNames[studentIndexes[i]], studentLastNames[studentIndexes[i]]).getId()))
                    .build());
        }
    }

    private void createEvents() {
        Event.Type[] types = {Event.Type.EXAM, Event.Type.HOMEWORK, Event.Type.HOMEWORK, Event.Type.HOMEWORK, Event.Type.TEST,
                Event.Type.EXAM, Event.Type.OTHER, Event.Type.HOMEWORK, Event.Type.HOMEWORK, Event.Type.HOMEWORK,
                Event.Type.OTHER};
        String[] description = {"Sprawdzian z matematyki", "Praca domowa - ćwiczenia 1-3, str 2",
                "Praca domowa - ćwiczenia 5-12, str 4", "Praca domowa ", "Test - operacje dodawania i odejmowania",
                "Egzamin", "Prezentacja ustna", "Praca domowa - ćw 1 - 2, str 10", "Praca domowa - ćw 1- 4, str 20",
                "Praca domowa - ćw  1 - 10, str 21", "Prezentacja ustna"
        };
        int[] teachersIndexes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] schoolClassIndexes = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < types.length; i++) {

            LocalDateTime dateToConvert = LocalDate.now().atStartOfDay().plusMinutes(400 + i);

            Date date = new Date(java.util.Date
                    .from(dateToConvert.atZone(ZoneId.systemDefault())
                            .toInstant()).getTime());

            eventRepository.save(Event.builder()
                    .type(types[i])
                    .description(description[i])
                    .createDate(Date.valueOf(LocalDate.now()))
                    .date(date)
                    .teacher(teacherRepository.findByUserId(userRepository
                            .findByFirstNameAndLastName(
                                    teacherNames[teachersIndexes[i]],
                                    teacherLastNames[teachersIndexes[i]]).getId()))
                    .schoolClass(classRepository.findByName(classNames[schoolClassIndexes[i]]))
                    .build());
        }
    }

    private void createReports() {
        int headmasterIndex = 0;
        int[] teacherIndexes = {2, 3, 5, 1, 4};

        for (int i = 0; i < teacherIndexes.length; i++) {
            reportRepository.save(Report.builder()
                    .startPeriod(Date.valueOf(LocalDate.now()))
                    .endPeriod(Date.valueOf(LocalDate.now().plusDays(10)))
                    .content("to be added soon")
                    .headmaster(teacherRepository.findByUserId(userRepository
                            .findByFirstNameAndLastName(
                                    teacherNames[headmasterIndex],
                                    teacherLastNames[headmasterIndex]).getId()))
                    .teacher(teacherRepository.findByUserId(userRepository
                            .findByFirstNameAndLastName(
                                    teacherNames[teacherIndexes[i]],
                                    teacherLastNames[teacherIndexes[i]]).getId()))
                    .build());

        }
    }

    private void addStudentsToClasses() {

        //Adding 20 students to 1st class
        for (int i = 0; (i < (i + classSize) && i < studentNames.length) ; i++) {
            Student student = studentRepository.findByUserId(userRepository
                    .findByFirstNameAndLastName(studentNames[i], studentLastNames[i]).getId());

            student.setSchoolClass(classRepository.findByName(classNames[0]));
            studentRepository.save(student);
        }

        //Adding rest of students to 2nd class
        for (int i = classSize; (i < (i + classSize) && i < studentNames.length) ; i++) {
            Student student = studentRepository.findByUserId(userRepository
                    .findByFirstNameAndLastName(studentNames[i], studentLastNames[i]).getId());

            student.setSchoolClass(classRepository.findByName(classNames[1]));
            studentRepository.save(student);
        }

    }


    private void createStudentCards() {

        for (int i = 0; i < studentNames.length; i++) {
            Student student = studentRepository.findByUserId(userRepository
                    .findByFirstNameAndLastName(studentNames[i], studentLastNames[i]).getId());

            Teacher teacher = student.getSchoolClass().getTeacher();

            studentCardRepository.save(StudentCard.builder()
                    .date(Date.valueOf(LocalDate.now()))
                    .content("to be added soon")
                    .student(student)
                    .teacher(teacher)
                    .build());
        }
    }


    private void createDays() {
        String[] days = {"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};

        for (int i = 0; i < days.length; i++) {
            dayRepository.save(Day.builder()
                    .name(days[i])
                    .number(i)
                    .build());
        }
    }

    private void createClassrooms() {
        String[] classroomsName = {"Przyrodnicza", "Matematyczna", "Sala gimnastyczna", "Historyczna", "Geograficzna"};

        for (int i = 0; i < classroomsName.length; i++) {
            classroomRepository.save(Classroom.builder()
                    .name(classroomsName[i])
                    .number(i + "")
                    .build());
        }
    }

    private void createDurations() {
        LocalTime startTime = LocalTime.now();
        LocalTime endTime = LocalTime.now().plusMinutes(45L);

        for (int i = 0; i < 8; i++) {
            durationRepository.save(Duration.builder()
                    .number(i)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build());

            startTime = endTime.plusMinutes(45L);
            endTime = endTime.plusMinutes(90L);
        }
    }


    /**Creating school periods for 1st class**/
    private void createSchoolPeriods() {
        int numberOfDays = 5;
        int[][] durationNumbers = {
                {0,3,5},
                {1,2,3,4,5,6},
                {0,2,3,4,5},
                {1,2,3},
                {1,3,4}
        };

        String[][] subjects = {
                {subjectNames[0], subjectNames[1], subjectNames[3]},
                {subjectNames[0], subjectNames[1], subjectNames[3], subjectNames[3], subjectNames[5], subjectNames[2], subjectNames[5]},
                {subjectNames[4], subjectNames[8], subjectNames[1], subjectNames[2], subjectNames[0]},
                {subjectNames[0], subjectNames[3], subjectNames[5]},
                {subjectNames[0], subjectNames[1], subjectNames[7]}
        };

        String schoolClass = classRepository.findAll().get(0).getName();

        Teacher teacher1 = teacherRepository.findByUserId(userRepository.findByFirstNameAndLastName(teacherNames[0],
                teacherLastNames[0]).getId());
        Teacher teacher2 = teacherRepository.findByUserId(userRepository.findByFirstNameAndLastName(teacherNames[1],
                teacherLastNames[1]).getId());

        for (int i = 0; i < numberOfDays; i++) {
            for (int j = 0; j < durationNumbers[i].length; j++)
                schoolPeriodRepository.save(SchoolPeriod.builder()
                        .descripton("")
                        .day(dayRepository.findByNumber(i))
                        .duration(durationRepository.findByNumber(durationNumbers[i][j]))
                        .subject(subjectRepository.findByName(subjects[i][j]))
                        //todo: add rep method
                        .teacher(j % 2 == 0 ? teacher1 : teacher2)
                        .classroom(classroomRepository.findByNumber(i +""))
                        .schoolClass(classRepository.findByName(schoolClass))
                        .build());
        }

        log.debug("Created school periods: " + schoolPeriodRepository.count());
    }

}




