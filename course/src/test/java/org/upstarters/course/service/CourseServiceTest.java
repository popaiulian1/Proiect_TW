package org.upstarters.course.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upstarters.course.dto.CourseDto;
import org.upstarters.course.dto.ExternalStudentDTO;
import org.upstarters.course.dto.FullCourseDto;
import org.upstarters.course.entity.Course;
import org.upstarters.course.repository.CourseRepository;
import org.upstarters.course.service.interfaces.StudentsFeignClient;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentsFeignClient studentsFeignClient;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseDto courseDto;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setTitle("Java Programming");
        course.setDepartment("IT");
        course.setCapacity(30);

        courseDto = new CourseDto();
        courseDto.setTitle("Java Programming");
        courseDto.setDepartment("IT");
        courseDto.setCapacity(30);
    }

    @Test
    void addCourse_Success() {
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        CourseDto saved = courseService.addCourse(courseDto);
        assertNotNull(saved);
        assertEquals("Java Programming", saved.getTitle());
    }

    @Test
    void getCourseById_Found() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        Optional<CourseDto> result = courseService.getCourseById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void getAllCourses_ReturnsList() {
        when(courseRepository.findAll()).thenReturn(List.of(course));
        List<CourseDto> result = courseService.getAllCourses();
        assertEquals(1, result.size());
    }

    @Test
    void getFullCourseByTitle_Found() {
        when(courseRepository.findByTitle("Java Programming")).thenReturn(course);
        Optional<FullCourseDto> result = courseService.getFullCourseByTitle("Java Programming");
        assertTrue(result.isPresent());
    }

    @Test
    void updateCourse_Success() {
        when(courseRepository.findByTitle("Java Programming")).thenReturn(course);
        assertTrue(courseService.updateCourse(courseDto));
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void updateCourse_NotFound() {
        when(courseRepository.findByTitle("Java Programming")).thenReturn(null);
        assertFalse(courseService.updateCourse(courseDto));
    }

    @Test
    void deleteCourse_Success() {
        when(courseRepository.findByTitle("Java Programming")).thenReturn(course);
        assertTrue(courseService.deleteCourse("Java Programming"));
        verify(courseRepository).delete(course);
    }

    @Test
    void getCoursesByDepartment_Success() {
        when(courseRepository.findCourseByDepartment("IT")).thenReturn(List.of(course));
        assertFalse(courseService.getCoursesByDepartment("IT").isEmpty());
    }

    @Test
    void getCoursesByDepartment_ThrowsException_WhenEmpty() {
        when(courseRepository.findCourseByDepartment("None")).thenReturn(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> courseService.getCoursesByDepartment("None"));
    }

    @Test
    void updateCapacityOfCourse_Success() {
        when(courseRepository.findByTitle("Java Programming")).thenReturn(course);
        assertTrue(courseService.updateCapacityOfCourse(50, "Java Programming"));
        assertEquals(50, course.getCapacity());
    }

    @Test
    void updateCapacityOfCourse_NegativeCapacity_ReturnsFalse() {
        assertFalse(courseService.updateCapacityOfCourse(-10, "Java Programming"));
    }

    @Test
    void getCoursesAvailable_ThrowsException_WhenNone() {
        when(courseRepository.findCourseAvailable()).thenReturn(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> courseService.getCoursesAvailable());
    }

    @Test
    void getStudents_CallsFeign() {
        when(studentsFeignClient.getStudents()).thenReturn(List.of(new ExternalStudentDTO()));
        assertNotNull(courseService.getStudents());
    }

    @Test
    void updateCourseCapacityBasedOnStudentCount_Success() {
        when(studentsFeignClient.getStudentsByMajor("IT")).thenReturn(List.of(new ExternalStudentDTO(), new ExternalStudentDTO()));
        when(courseRepository.findByTitle("Java Programming")).thenReturn(course);
        assertTrue(courseService.updateCourseCapacityBasedOnStudentCount("Java Programming", "IT"));
        assertEquals(2, course.getCapacity());
    }
}