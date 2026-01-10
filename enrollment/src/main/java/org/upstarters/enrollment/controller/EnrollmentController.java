package org.upstarters.enrollment.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.dto.EnrollmentRequestDTO;
import org.upstarters.enrollment.dto.EnrollmentUpdateDTO;
import org.upstarters.enrollment.entity.Enrollment;
import org.upstarters.enrollment.mapper.EnrollmentMapper;
import org.upstarters.enrollment.service.enrollment.EnrollmentService;
import org.upstarters.enrollment.service.course.CourseAPIService;
import org.upstarters.enrollment.service.student.StudentAPIService;
import org.upstarters.enrollment.dto.StudentDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;

    EnrollmentController( EnrollmentService enrollmentServiceInstance,
                          EnrollmentMapper enrollmentMapperInstance) {
        enrollmentService = enrollmentServiceInstance;
        enrollmentMapper = enrollmentMapperInstance;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @PostMapping("/create")
    public ResponseEntity<String> createEnrollment(@Valid @RequestBody EnrollmentRequestDTO enrollment) {

        if(enrollment.getStudentName() == null || enrollment.getStudentName().isEmpty() ||
           enrollment.getCourseName() == null || enrollment.getCourseName().isEmpty()) {
            return new ResponseEntity<>( "Student and course must be provided" ,HttpStatus.BAD_REQUEST);
        }

        try{
            enrollmentService.enrollStudentInCourse(enrollment.getStudentName(), enrollment.getCourseName());
        } catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Enrollment created successfully", HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @GetMapping("/all")
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();

        List<EnrollmentDTO> enrollmentDTOs = enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(enrollmentDTOs, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @GetMapping("/enrollment/{id}")
    public ResponseEntity<EnrollmentDTO> getEnrollmentById(@Valid @PathVariable Long id) {

        try{
            EnrollmentDTO enrollment = enrollmentService.getEnrollment(id);
            return new ResponseEntity<>(enrollment,HttpStatus.FOUND);
        } catch(Exception e) {
            return new ResponseEntity<>( null , HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateEnrollment(@Valid @PathVariable Long id, @Valid @RequestBody EnrollmentUpdateDTO enrollmentUpdateDTO) {
        try{
            enrollmentService.updateEnrollment(id, enrollmentUpdateDTO);
        } catch(Exception e) {
            return new ResponseEntity<>("Enrollment not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("Enrollment updated successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEnrollment(@Valid @PathVariable Long id) {
        try{
            enrollmentService.deleteEnrollment(id);
        } catch(Exception e) {
            return new ResponseEntity<>("Enrollment not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("Enrollment deleted successfully");
    }

    // =====> Additional endpoints <=====

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @GetMapping("/students/{course}")
    public ResponseEntity<List<EnrollmentDTO>> getStudentsByCourse(@Valid @PathVariable String course) {

        List<EnrollmentDTO> enrollments =  new ArrayList<EnrollmentDTO>();

        try{
            enrollments = enrollmentService.studentsFilteredByCourse(course);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(enrollments);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/students/failed")
    public ResponseEntity<List<EnrollmentDTO>> getStudentsWithFailingGrades() {

        List<EnrollmentDTO> enrollments = new ArrayList<EnrollmentDTO>();

        try {
            enrollments = enrollmentService.failingStudents();
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(enrollments);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/course/{course}/top5")
    public ResponseEntity<List<EnrollmentDTO>> getTop5StudentsInCourse(@Valid @PathVariable String course) {

        List<EnrollmentDTO> enrollments = new ArrayList<EnrollmentDTO>();

        try {
            enrollments = enrollmentService.getTop5InCourse(course);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(enrollments);
    }

    // =====> Endpoints using other services <=====

    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    @GetMapping("/student/enrollment/{enrollmentId}/student-details")
    public ResponseEntity<StudentDTO> getStudentDetailsFromEnrollment(@Valid @PathVariable Long enrollmentId) {
        try {
            StudentDTO studentDetails = enrollmentService.getStudentDetailsFromEnrollment(enrollmentId);
            return ResponseEntity.ok(studentDetails);
        } catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}