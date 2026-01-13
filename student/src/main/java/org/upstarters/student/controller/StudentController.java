package org.upstarters.student.controller;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upstarters.student.dtos.ExternalCourseDTO;
import org.upstarters.student.dtos.StudentDTO;
import org.upstarters.student.services.IStudentService;

import jakarta.validation.Valid;
import org.yaml.snakeyaml.Yaml;

@RestController
@RefreshScope
@RequestMapping(path = "/students")
public class StudentController {

    @Autowired
    private IStudentService studentService;

    @Value("${student.test-message:Valoare Default}")
    private String testMessage;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = studentService.addStudent(studentDTO);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<StudentDTO> fetchStudent(@Valid @PathVariable String email) {
        StudentDTO fetchedStudent = studentService.fetchStudent(email);
        return new ResponseEntity<>(fetchedStudent, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getStudents")
    public ResponseEntity<List<StudentDTO>> fetchStudent() {
        List<StudentDTO> fetchedStudents = studentService.fetchStudents();
        return new ResponseEntity<>(fetchedStudents, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/countStudents")
    public ResponseEntity<Long> countStudents() {
        Long numberOfStudents = studentService.countStudents();
        return new ResponseEntity<>(numberOfStudents, HttpStatus.FOUND);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getStudentsByMajor/{major}")
    public ResponseEntity<List<StudentDTO>> fetchStudentByMajor(@Valid @PathVariable String major) {
        List<StudentDTO> fetchedStudents = studentService.fetchStudentsByMajor(major);
        return new ResponseEntity<>(fetchedStudents, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{email}")
    public ResponseEntity<StudentDTO> updateStudent(@Valid @PathVariable String email, @Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(email, studentDTO);
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<Boolean> deleteStudent(@Valid @PathVariable String email) {
        boolean deleted = studentService.deleteStudent(email);
        return ResponseEntity.ok(deleted);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getId/{email}")
    public ResponseEntity<Long> fetchStudentIdFromEmail(@Valid @PathVariable String email) {
        Long id = studentService.fetchStudentIdFromEmail(email);
        return ResponseEntity.ok(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getEmail/{id}")
    public ResponseEntity<String> fetchStudentEmailFromId(@Valid @PathVariable Long id) {
        String email = studentService.fetchStudentEmailFromId(id);
        return ResponseEntity.ok(email);
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @GetMapping("/recommendations/{email}")
    public ResponseEntity<List<ExternalCourseDTO>> getRecommendedCourses(@PathVariable String email) {
        List<ExternalCourseDTO> recommendedCourses = studentService.getRecommendedCourses(email);
        return new ResponseEntity<>(recommendedCourses, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update-major-from-course/{email}/{courseTitle}")
    public ResponseEntity<StudentDTO> updateMajorFromCourse(
            @PathVariable String email,
            @PathVariable String courseTitle) {

        StudentDTO updatedStudent = studentService.updateMajorFromCourse(email, courseTitle);
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    @GetMapping("/message")
    public ResponseEntity<String> getMessageFromProperties() {
        return ResponseEntity.ok(testMessage);
    }
}
