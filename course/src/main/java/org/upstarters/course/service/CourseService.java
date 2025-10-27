package org.upstarters.course.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upstarters.course.dto.CourseDto;
import org.upstarters.course.entity.Course;
import org.upstarters.course.mapper.CourseMapper;
import org.upstarters.course.repository.CourseRepository;
import org.upstarters.course.service.interfaces.ICourseService;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService implements ICourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public CourseDto addCourse(CourseDto courseDto) {
        Course course = CourseMapper.toEntity(courseDto);
        courseRepository.save(course);
        return CourseMapper.toDto(course);
    }

    @Override
    public Optional<CourseDto> getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(CourseMapper::toDto);
    }

    @Override
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }

    @Override
    public Boolean updateCourse(CourseDto courseDto) {
        Course course = CourseMapper.toEntity(courseDto);

        if(course.getCourseId() == null || !courseRepository.existsById(course.getCourseId())) {
            System.out.println("Course with ID " + course.getCourseId() + " does not exist.");
            return false;
        }

        courseRepository.save(course);
        return true;
    }

    @Override
    public Boolean deleteCourse(Long courseId) {
        if (courseId == null || !courseRepository.existsById(courseId)) {
            System.out.println("Course with ID " + courseId + " does not exist.");
            return false;
        }
        courseRepository.deleteById(courseId);
        return true;
    }
}
