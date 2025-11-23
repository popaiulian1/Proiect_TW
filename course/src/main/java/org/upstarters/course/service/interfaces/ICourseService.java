package org.upstarters.course.service.interfaces;

import org.upstarters.course.dto.CourseDto;

import java.util.List;
import java.util.Optional;

public interface ICourseService {
    CourseDto addCourse(CourseDto courseDto);
    List<CourseDto> getAllCourses();
    Optional<CourseDto> getCourseById(Long id);
    Boolean updateCourse(CourseDto courseDto);
    Boolean deleteCourse(String Title);

    List<CourseDto> getCoursesByDepartment(String department);
    Boolean updateCapacityOfCourse(Integer capacity, String title);
    List<CourseDto> getCoursesAvailable();
    List<CourseDto> getCoursesSortedByCapacity();
}
