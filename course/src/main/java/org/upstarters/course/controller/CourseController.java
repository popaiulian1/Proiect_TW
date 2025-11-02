package org.upstarters.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upstarters.course.dto.CourseDto;
import org.upstarters.course.service.CourseService;

@RestController
@RequestMapping(path = "/courses")
public class CourseController {

    public final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    //region Post Endpoints
    @PostMapping("/addCourse")
    public ResponseEntity<CourseDto> addCourse(@RequestBody CourseDto courseDto) {
        CourseDto addCourseDto = courseService.addCourse(courseDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addCourseDto);
    }
    //endregion

    //region Get Endpoints
    @GetMapping("/getAllCourses")
    public ResponseEntity<Iterable<CourseDto>> getAllCourses() {
        Iterable<CourseDto> courseDtos = courseService.getAllCourses();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseDtos);
    }

    @GetMapping("/getCourseById/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long courseId) {
        return courseService.getCourseById(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getCoursesByDepartment/{department}")
    public ResponseEntity<Iterable<CourseDto>> getCoursesByDepartment(@PathVariable String department) {
        Iterable<CourseDto> courseDtos = courseService.getCoursesByDepartment(department);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(courseDtos);
    }

    //endregion

    //region Update Endpoints
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

    @PatchMapping("/updateCapacity/{tile}")
    public ResponseEntity<String> updateCapacity(@PathVariable String tile, @RequestParam Integer capacity) {
        Boolean isUpdated = courseService.updateCapacityOfCourse(capacity, tile);

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
