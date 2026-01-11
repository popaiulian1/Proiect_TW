package org.upstarters.student.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.upstarters.student.dtos.ExternalCourseDTO;

import java.util.List;

@FeignClient(name="courses", path="/courses")
public interface CoursesFeignClient {

    @GetMapping("/getCoursesByDepartment/{department}")
    List<ExternalCourseDTO> fetchCoursesByDepartment(@PathVariable String department);
}
