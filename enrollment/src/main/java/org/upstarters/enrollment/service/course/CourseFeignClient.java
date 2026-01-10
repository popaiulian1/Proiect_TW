package org.upstarters.enrollment.service.course;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.upstarters.enrollment.dto.CourseDTO;
import org.upstarters.enrollment.dto.FullCourseDTO;

@FeignClient(name = "course-service", path = "/courses")
public interface CourseFeignClient {
    @GetMapping("/getCourseById/{courseId}")
    CourseDTO getCourseById(@PathVariable("courseId") Long courseId);
    
    @GetMapping("/getCourseByTitle/{title}")
    FullCourseDTO getCourseByTitle(@PathVariable("title") String title);
}
