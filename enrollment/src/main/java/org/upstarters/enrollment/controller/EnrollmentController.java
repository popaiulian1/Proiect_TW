package org.upstarters.enrollment.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.dto.EnrollmentRequestDTO;
import org.upstarters.enrollment.dto.EnrollmentUpdateDTO;
import org.upstarters.enrollment.entity.Enrollment;
import org.upstarters.enrollment.mapper.EnrollmentMapper;
import org.upstarters.enrollment.service.enrollment.EnrollmentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/private")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;

    EnrollmentController( EnrollmentService enrollmentServiceInstance,
                          EnrollmentMapper enrollmentMapperInstance) {
        enrollmentService = enrollmentServiceInstance;
        enrollmentMapper = enrollmentMapperInstance;

    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

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

    @GetMapping("/all")
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();

        List<EnrollmentDTO> enrollmentDTOs = enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(enrollmentDTOs, HttpStatus.OK);
    }

    @GetMapping("/enrollment/{id}")
    public ResponseEntity<EnrollmentDTO> getEnrollmentById(@Valid @PathVariable Long id) {

        try{
            EnrollmentDTO enrollment = enrollmentService.getEnrollment(id);
            return new ResponseEntity<>(enrollment,HttpStatus.FOUND);
        } catch(Exception e) {
            return new ResponseEntity<>( null , HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateEnrollment(@Valid @PathVariable Long id, @Valid @RequestBody EnrollmentUpdateDTO enrollmentUpdateDTO) {
        try{
            enrollmentService.updateEnrollment(id, enrollmentUpdateDTO);
        } catch(Exception e) {
            return new ResponseEntity<>("Enrollment not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("Enrollment updated successfully");
    }

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

    @GetMapping("/students/{course}/")
    public ResponseEntity<String> getStudentsByCourse(@Valid @PathVariable String course) {
        // Placeholder for fetching students by course logic
        return ResponseEntity.ok("List of students enrolled in the specified course");
    }

    @GetMapping("/students/failed/")
    public ResponseEntity<String> getStudentsWithFailingGrades() {
        // Placeholder for fetching students with failing grades logic
        return ResponseEntity.ok("List of students with failing grades");
    }

    @GetMapping("/course/{course}/top5")
    public ResponseEntity<String> getTop5StudentsInCourse(@Valid @PathVariable String course) {
        // Placeholder for fetching top 5 students in a course logic
        return ResponseEntity.ok("Top 5 students in the specified course");
    }

}
