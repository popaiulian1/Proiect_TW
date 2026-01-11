package org.upstarters.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.upstarters.course.dto.CourseDto;
import org.upstarters.course.dto.ExternalStudentDTO;
import org.upstarters.course.dto.FullCourseDto;
import org.upstarters.course.service.CourseService;

import java.util.List;

@RestController
@RequestMapping(path = "/courses")
public class CourseController {

    public final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    //region Post Endpoints
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addCourse")
    public ResponseEntity<CourseDto> addCourse(@RequestBody CourseDto courseDto) {
        CourseDto addCourseDto = courseService.addCourse(courseDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addCourseDto);
    }
    //endregion

    //region Get Endpoints
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllCourses")
    public ResponseEntity<Iterable<CourseDto>> getAllCourses() {
        Iterable<CourseDto> courseDtos = courseService.getAllCourses();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseDtos);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/getCourseById/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long courseId) {
        return courseService.getCourseById(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/getCoursesByDepartment/{department}")
    public ResponseEntity<Iterable<CourseDto>> getCoursesByDepartment(@PathVariable String department) {
        Iterable<CourseDto> courseDtos = courseService.getCoursesByDepartment(department);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseDtos);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("getAllAvailableCourses")
    public ResponseEntity<Iterable<CourseDto>> getAllAvailableCourses() {
        Iterable<CourseDto> availableCoursesDtos = courseService.getCoursesAvailable();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(availableCoursesDtos);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/getCoursesSortedByCapacity")
    public ResponseEntity<Iterable<CourseDto>> getCoursesSortedByCapacity() {
        Iterable<CourseDto> courseDtos = courseService.getCoursesSortedByCapacity();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseDtos);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/getByTitle/{title}")
    public ResponseEntity<FullCourseDto> getCourseById(@PathVariable String title) {
        return courseService.getFullCourseByTitle(title)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/getAllStudents")
    public ResponseEntity<List<ExternalStudentDTO>> getAllStudents() {
        List<ExternalStudentDTO> students = courseService.getStudents();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(students);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/getStudentsByDepartment/{department}")
    public ResponseEntity<List<ExternalStudentDTO>> getStudentsByDepartment(@PathVariable String department) {
        List<ExternalStudentDTO> students = courseService.getStudentsByDepartment(department);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(students);
    }

    //endregion

    //region Update Endpoints
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateCourse")
    public ResponseEntity<String> updateCourse(@RequestBody CourseDto courseDto) {
        Boolean isUpdated = courseService.updateCourse(courseDto);

        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Course updated successfully.");
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Course not found.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/updateCapacity/{title}")
    public ResponseEntity<String> updateCapacity(@PathVariable String title, @RequestParam Integer capacity) {
        Boolean isUpdated = courseService.updateCapacityOfCourse(capacity, title);

        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Course updated successfully.");
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Course not found.");
        }
    }

    //endregion

    //region Delete Endpoints
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteCourse/{title}")
    public ResponseEntity<String> deleteCourse(@PathVariable String title) {
        Boolean isDeleted = courseService.deleteCourse(title);

        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Course deleted successfully.");
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Course not found.");
        }
    }
    //endregion



}
