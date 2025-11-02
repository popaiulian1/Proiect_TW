package org.upstarters.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upstarters.course.dto.CourseDto;
import org.upstarters.course.entity.Course;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findByTitle(String title);
    List<Course> findCourseByDepartment(String department);


}
