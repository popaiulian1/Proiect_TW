package org.upstarters.enrollment.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upstarters.enrollment.dto.CourseDTO;
import org.upstarters.enrollment.dto.FullCourseDTO;
import org.upstarters.enrollment.service.course.CourseAPIService;
import org.upstarters.enrollment.service.course.CourseFeignClient;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
class CourseAPIServiceTest {

    @Mock
    private CourseFeignClient courseFeignClient;

    @InjectMocks
    private CourseAPIService courseAPIService;

    private CourseDTO testCourseDTO;
    private FullCourseDTO testFullCourseDTO;

    @BeforeEach
    void setUp() {
        testCourseDTO = new CourseDTO("Math 101", "Mathematics", 100);
        testFullCourseDTO = new FullCourseDTO(1L, "Math 101", "Mathematics", 100);
    }

    @Test
    void testGetCourseIdByName_Success() {
        String courseName = "Math 101";

        when(courseFeignClient.getCourseByTitle(courseName)).thenReturn(testFullCourseDTO);

        Long result = courseAPIService.getCourseIdByName(courseName);

        assertNotNull(result);
        assertEquals(1L, result);
        verify(courseFeignClient, times(1)).getCourseByTitle(courseName);
    }

    @Test
    void testGetCourseIdByName_CourseNotFound() {
        String courseName = "NonExistent Course";

        when(courseFeignClient.getCourseByTitle(courseName))
                .thenThrow(FeignException.NotFound.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseAPIService.getCourseIdByName(courseName));

        assertTrue(exception.getMessage().contains("Failed to get course id"));
        verify(courseFeignClient, times(1)).getCourseByTitle(courseName);
    }

    @Test
    void testGetCourseIdByName_FeignException() {
        String courseName = "Math 101";

        when(courseFeignClient.getCourseByTitle(courseName))
                .thenThrow(new RuntimeException("Service unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseAPIService.getCourseIdByName(courseName));

        assertTrue(exception.getMessage().contains("Failed to get course id"));
        verify(courseFeignClient, times(1)).getCourseByTitle(courseName);
    }

    @Test
    void testGetCourseIdByName_NullResponse() {
        String courseName = "Math 101";

        when(courseFeignClient.getCourseByTitle(courseName)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseAPIService.getCourseIdByName(courseName));

        assertTrue(exception.getMessage().contains("Failed to get course id"));
    }

    @Test
    void testGetCourseIdByName_EmptyCourseName() {
        String courseName = "";

        when(courseFeignClient.getCourseByTitle(courseName))
                .thenThrow(new RuntimeException("Invalid course name"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseAPIService.getCourseIdByName(courseName));

        assertTrue(exception.getMessage().contains("Failed to get course id"));
    }

    @Test
    void testGetCourseIdByName_WithSpaces() {
        String courseName = "Introduction to Computer Science";
        FullCourseDTO course = new FullCourseDTO(5L, courseName, "Computer Science", 150);

        when(courseFeignClient.getCourseByTitle(courseName)).thenReturn(course);

        Long result = courseAPIService.getCourseIdByName(courseName);

        assertNotNull(result);
        assertEquals(5L, result);
        verify(courseFeignClient, times(1)).getCourseByTitle(courseName);
    }

    @Test
    void testGetCourseNameById_Success() {
        Long courseId = 1L;

        when(courseFeignClient.getCourseById(courseId)).thenReturn(testCourseDTO);

        String result = courseAPIService.getCourseNameById(courseId);

        assertNotNull(result);
        assertEquals("Math 101", result);
        verify(courseFeignClient, times(1)).getCourseById(courseId);
    }

    @Test
    void testGetCourseNameById_CourseNotFound() {
        Long courseId = 999L;

        when(courseFeignClient.getCourseById(courseId))
                .thenThrow(FeignException.NotFound.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseAPIService.getCourseNameById(courseId));

        assertTrue(exception.getMessage().contains("Failed to get course name"));
        verify(courseFeignClient, times(1)).getCourseById(courseId);
    }

    @Test
    void testGetCourseNameById_FeignException() {
        Long courseId = 1L;

        when(courseFeignClient.getCourseById(courseId))
                .thenThrow(new RuntimeException("Service unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseAPIService.getCourseNameById(courseId));

        assertTrue(exception.getMessage().contains("Failed to get course name"));
        verify(courseFeignClient, times(1)).getCourseById(courseId);
    }

    @Test
    void testGetCourseNameById_NullResponse() {
        Long courseId = 1L;

        when(courseFeignClient.getCourseById(courseId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseAPIService.getCourseNameById(courseId));

        assertTrue(exception.getMessage().contains("Failed to get course name"));
    }

    @Test
    void testGetCourseNameById_NullCourseId() {
        when(courseFeignClient.getCourseById(null))
                .thenThrow(new IllegalArgumentException("Course ID cannot be null"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseAPIService.getCourseNameById(null));

        assertTrue(exception.getMessage().contains("Failed to get course name"));
    }

    @Test
    void testGetCourseNameById_DifferentCourses() {
        Long courseId1 = 1L;
        Long courseId2 = 2L;

        CourseDTO course1 = new CourseDTO("Math 101", "Mathematics", 100);
        CourseDTO course2 = new CourseDTO("Physics 101", "Physics", 80);

        when(courseFeignClient.getCourseById(courseId1)).thenReturn(course1);
        when(courseFeignClient.getCourseById(courseId2)).thenReturn(course2);

        String result1 = courseAPIService.getCourseNameById(courseId1);
        String result2 = courseAPIService.getCourseNameById(courseId2);

        assertEquals("Math 101", result1);
        assertEquals("Physics 101", result2);
        verify(courseFeignClient, times(1)).getCourseById(courseId1);
        verify(courseFeignClient, times(1)).getCourseById(courseId2);
    }

    @Test
    void testGetCourseIdByName_MultipleCallsSameCourse() {
        String courseName = "Math 101";

        when(courseFeignClient.getCourseByTitle(courseName)).thenReturn(testFullCourseDTO);

        Long result1 = courseAPIService.getCourseIdByName(courseName);
        Long result2 = courseAPIService.getCourseIdByName(courseName);

        assertEquals(result1, result2);
        assertEquals(1L, result1);
        verify(courseFeignClient, times(2)).getCourseByTitle(courseName);
    }

    @Test
    void testGetCourseNameById_MultipleCallsSameId() {
        Long courseId = 1L;

        when(courseFeignClient.getCourseById(courseId)).thenReturn(testCourseDTO);

        String result1 = courseAPIService.getCourseNameById(courseId);
        String result2 = courseAPIService.getCourseNameById(courseId);

        assertEquals(result1, result2);
        assertEquals("Math 101", result1);
        verify(courseFeignClient, times(2)).getCourseById(courseId);
    }

    @Test
    void testGetCourseIdByName_WithSpecialCharacters() {
        String courseName = "C++ Programming";
        FullCourseDTO course = new FullCourseDTO(10L, courseName, "Computer Science", 50);

        when(courseFeignClient.getCourseByTitle(courseName)).thenReturn(course);

        Long result = courseAPIService.getCourseIdByName(courseName);

        assertNotNull(result);
        assertEquals(10L, result);
        verify(courseFeignClient, times(1)).getCourseByTitle(courseName);
    }

    @Test
    void testGetCourseNameById_ZeroId() {
        Long courseId = 0L;

        when(courseFeignClient.getCourseById(courseId))
                .thenThrow(new RuntimeException("Invalid course ID"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> courseAPIService.getCourseNameById(courseId));

        assertTrue(exception.getMessage().contains("Failed to get course name"));
    }

    @Test
    void testGetCourseIdByName_DifferentCoursesSameDepartment() {
        String courseName1 = "Math 101";
        String courseName2 = "Math 201";

        FullCourseDTO course1 = new FullCourseDTO(1L, courseName1, "Mathematics", 100);
        FullCourseDTO course2 = new FullCourseDTO(2L, courseName2, "Mathematics", 80);

        when(courseFeignClient.getCourseByTitle(courseName1)).thenReturn(course1);
        when(courseFeignClient.getCourseByTitle(courseName2)).thenReturn(course2);

        Long result1 = courseAPIService.getCourseIdByName(courseName1);
        Long result2 = courseAPIService.getCourseIdByName(courseName2);

        assertEquals(1L, result1);
        assertEquals(2L, result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void testGetCourseNameById_LongCourseName() {
        Long courseId = 100L;
        String longCourseName = "Advanced Topics in Artificial Intelligence and Machine Learning";
        CourseDTO course = new CourseDTO(longCourseName, "Computer Science", 30);

        when(courseFeignClient.getCourseById(courseId)).thenReturn(course);

        String result = courseAPIService.getCourseNameById(courseId);

        assertEquals(longCourseName, result);
        verify(courseFeignClient, times(1)).getCourseById(courseId);
    }
}