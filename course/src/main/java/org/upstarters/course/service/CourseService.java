package org.upstarters.course.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upstarters.course.dto.CourseDto;
import org.upstarters.course.dto.ExternalStudentDTO;
import org.upstarters.course.dto.FullCourseDto;
import org.upstarters.course.entity.Course;
import org.upstarters.course.mapper.CourseMapper;
import org.upstarters.course.repository.CourseRepository;
import org.upstarters.course.service.interfaces.ICourseService;
import org.upstarters.course.service.interfaces.StudentsFeignClient;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService implements ICourseService {

    private final CourseRepository courseRepository;
    private final StudentsFeignClient studentsFeignClient;

    @Autowired
    public CourseService(StudentsFeignClient studentsFeignClient, CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
        this.studentsFeignClient = studentsFeignClient;
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
    public Optional<FullCourseDto> getFullCourseByTitle(String title) {
        return Optional.ofNullable(courseRepository.findByTitle(title))
                .map(CourseMapper::toFullDto);
    }



    @Override
    @Transactional
    public Boolean updateCourse(CourseDto courseDto) {
        Course existingCourse = courseRepository.findByTitle(courseDto.getTitle());

        if (existingCourse == null) {
            System.out.println("Course with title " + courseDto.getTitle() + " does not exist.");
            return false;
        }

        existingCourse.setDepartment(courseDto.getDepartment());
        existingCourse.setCapacity(courseDto.getCapacity());

        courseRepository.save(existingCourse);
        return true;
    }

    @Override
    @Transactional
    public Boolean deleteCourse(String title) {
        Course existingCourse = courseRepository.findByTitle(title);
        if (existingCourse == null) {
            System.out.println("Course with title " + title + " does not exist.");
            return false;
        }

        courseRepository.delete(existingCourse);
        return true;
    }

    @Override
    @Transactional
    public List<CourseDto> getCoursesByDepartment(String department) {
        List<Course> departmentCourses = courseRepository.findCourseByDepartment(department);

        if(departmentCourses.isEmpty()) {
            throw new RuntimeException("Course with department " + department + " does not exist.");
        }

        return departmentCourses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public Boolean updateCapacityOfCourse(Integer capacity, String title) {
        Course existingCourse = courseRepository.findByTitle(title);

        if (existingCourse == null) {
            System.out.println("Course with title " + title + " does not exist.");
            return false;
        }

        if(capacity < 0){
            System.out.println("Capacity of course with title " + title + " must be positive.");
            return false;
        }

        existingCourse.setCapacity(capacity);

        courseRepository.save(existingCourse);
        return true;
    }

    @Override
    public List<CourseDto> getCoursesAvailable() {
        List<Course> availableCourses = courseRepository.findCourseAvailable();

        if(availableCourses.isEmpty()) {
            throw new RuntimeException("There are now available courses.");
        }

        return availableCourses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }

    @Override
    public List<CourseDto> getCoursesSortedByCapacity() {
        List<Course> courses = courseRepository.findAllByOrderByCapacityAsc();

        return courses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }

    @Override
    public List<ExternalStudentDTO> getStudents() {
        return studentsFeignClient.getStudents();
    }

    @Override
    public List<ExternalStudentDTO> getStudentsByDepartment(String department) {
        return studentsFeignClient.getStudentsByMajor(department);
    }

    @Override
    public Boolean updateCourseCapacityBasedOnStudentCount(String courseTitle, String department) {
        List<ExternalStudentDTO> students = studentsFeignClient.getStudentsByMajor(department);

        if (students == null) {
            return false;
        }

        Course existingCourse = courseRepository.findByTitle(courseTitle);
        if (existingCourse == null) {
            return false;
        }

        existingCourse.setCapacity(students.size());
        courseRepository.save(existingCourse);
        return true;
    }
}
