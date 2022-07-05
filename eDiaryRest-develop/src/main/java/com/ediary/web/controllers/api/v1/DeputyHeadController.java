package com.ediary.web.controllers.api.v1;

import com.ediary.DTO.SchoolClassDto;
import com.ediary.DTO.StudentDto;
import com.ediary.DTO.TeacherDto;
import com.ediary.security.perms.DeputyHeadHeadmasterPermission;
import com.ediary.services.rest.DeputyHeadRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.ediary.web.controllers.api.v1.DeputyHeadController.BASE_DEPUTY_HEAD_URL;

@Profile("rest")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = BASE_DEPUTY_HEAD_URL)
public class DeputyHeadController {

    private final DeputyHeadRestService deputyHeadRestService;

    public static final String BASE_DEPUTY_HEAD_URL = "/api/v1/deputy-head";

    @GetMapping("/school-classes/unassigned-teachers")
    public ResponseEntity<List<TeacherDto>> getTeachersWithoutSchoolClass(Pageable pageable) {
        return ResponseEntity.ok(deputyHeadRestService.listAllTeachersWithoutSchoolClass(pageable));
    }

    @GetMapping("/school-classes/unassigned-students")
    public ResponseEntity<List<StudentDto>> getStudentsWithoutSchoolClass(Pageable pageable) {
        return ResponseEntity.ok(deputyHeadRestService.listAllStudentsWithoutClass(pageable));
    }

    @DeputyHeadHeadmasterPermission
    @PostMapping("/school-classes/{teacherId}")
    public ResponseEntity<SchoolClassDto> setClassTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(deputyHeadRestService.getSchoolClassWithTeacher(teacherId));
    }

    @DeputyHeadHeadmasterPermission
    @PostMapping("/school-classes")
    public ResponseEntity<?> createNewSchoolClass(@Valid @RequestBody SchoolClassDto schoolClass) {
        return ResponseEntity.ok(deputyHeadRestService.saveSchoolClass(schoolClass).getId());
    }

    @DeputyHeadHeadmasterPermission
    @GetMapping("/school-classes")
    public ResponseEntity<List<SchoolClassDto>> getSchoolClasses(Pageable pageable) {
        return ResponseEntity.ok(deputyHeadRestService.listAllSchoolClasses(pageable));
    }

    @DeputyHeadHeadmasterPermission
    @GetMapping("/school-classes/{schoolClassId}")
    public ResponseEntity<SchoolClassDto> getOneClass(@PathVariable Long schoolClassId) {
        return ResponseEntity.ok(deputyHeadRestService.getSchoolClassWithTeacher(schoolClassId));
    }

    @PostMapping("/school-classes/{schoolClassId}")
    public ResponseEntity<?> deleteClass(@PathVariable Long schoolClassId) {
        deputyHeadRestService.deleteClass(schoolClassId);

        return ResponseEntity.noContent().build();
    }

    @DeputyHeadHeadmasterPermission
    @PostMapping("/school-classes/{schoolClassId}/student-remove/{studentId}")
    public ResponseEntity<SchoolClassDto> removeStudentFromSchoolClass(@PathVariable Long studentId, @PathVariable Long schoolClassId) {
        return ResponseEntity.ok(deputyHeadRestService.removeStudentFromClass(schoolClassId, studentId));
    }


    @DeputyHeadHeadmasterPermission
    @PostMapping("/school-classes/{schoolClassId}/teacher-add/{teacherId}")
    public ResponseEntity<SchoolClassDto> addFormTutorToClass(@PathVariable Long teacherId, @PathVariable Long schoolClassId) {
        return ResponseEntity.ok(deputyHeadRestService.addFormTutorToSchoolClass(schoolClassId, teacherId));
    }

    @DeputyHeadHeadmasterPermission
    @PostMapping("/school-classes/{schoolClassId}/student-add/{studentId}")
    public ResponseEntity<SchoolClassDto> addStudentToClass(@PathVariable Long studentId, @PathVariable Long schoolClassId) {
        return ResponseEntity.ok(deputyHeadRestService.addStudentToSchoolClass(schoolClassId, studentId));
    }
}

