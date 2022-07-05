package com.ediary.converters;


import com.ediary.DTO.SchoolPeriodDto;
import com.ediary.domain.SchoolPeriod;
import com.ediary.repositories.ClassRepository;
import com.ediary.repositories.SubjectRepository;
import com.ediary.repositories.TeacherRepository;
import com.ediary.repositories.timetable.ClassroomRepository;
import com.ediary.repositories.timetable.DayRepository;
import com.ediary.repositories.timetable.DurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SchoolPeriodDtoToSchoolPeriod implements Converter<SchoolPeriodDto, SchoolPeriod> {

    private final DayRepository dayRepository;
    private final DurationRepository durationRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final ClassRepository classRepository;

    @Override
    public SchoolPeriod convert(SchoolPeriodDto source) {

        if (source == null) {
            return null;
        }

        SchoolPeriod schoolPeriod = new SchoolPeriod();

        schoolPeriod.setId(source.getId());
        schoolPeriod.setDescripton(source.getDescription());

        //Day
        schoolPeriod.setDay(dayRepository.findById(source.getDayId()).orElse(null));

        //Duration
        schoolPeriod.setDuration(durationRepository.findById(source.getDurationId()).orElse(null));

        //Subject
        schoolPeriod.setSubject(subjectRepository.findById(source.getSubjectId()).orElse(null));

        //Teacher
        schoolPeriod.setTeacher(teacherRepository.findById(source.getTeacherId()).orElse(null));

        //Classroom
        schoolPeriod.setClassroom(classroomRepository.findById(source.getClassroomId()).orElse(null));

        //Class
        schoolPeriod.setSchoolClass(classRepository.findById(source.getSchoolClassId()).orElse(null));

        return schoolPeriod;
    }
}
