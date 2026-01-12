package org.upstarters.enrollment.Mapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.entity.Enrollment;
import org.upstarters.enrollment.mapper.EnrollmentMapper;
import org.upstarters.enrollment.service.course.CourseAPIService;

@ExtendWith(MockitoExtension.class)
class EnrollmentMapperTest {

    @Mock
    private CourseAPIService courseAPIService;

    @InjectMocks
    private EnrollmentMapper enrollmentMapper;

    private Enrollment testEnrollment;

    @BeforeEach
    void setUp() {
        testEnrollment = new Enrollment();
        testEnrollment.setId(1L);
        testEnrollment.setStudentEmail("john.doe@example.com");
        testEnrollment.setCourseId(1L);
        testEnrollment.setEnrollmentDate(LocalDate.of(2026, 1, 15));
        testEnrollment.setGrade(8.5);
    }

    @Test
    void testToDto_Success() {
        String courseName = "Math 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getStudent());
        assertEquals(courseName, result.getCourse());
        assertEquals("2026-01-15", result.getEnrollmentDate());
        assertEquals(8.5, result.getGrade());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_NullEnrollment() {
        EnrollmentDTO result = enrollmentMapper.toDto(null);

        assertNull(result);
        verify(courseAPIService, never()).getCourseNameById(anyLong());
    }

    @Test
    void testToDto_NullEnrollmentDate() {
        testEnrollment.setEnrollmentDate(null);
        String courseName = "Math 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getStudent());
        assertEquals(courseName, result.getCourse());
        assertNull(result.getEnrollmentDate());
        assertEquals(8.5, result.getGrade());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_ZeroGrade() {
        testEnrollment.setGrade(0.0);
        String courseName = "Physics 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals(0.0, result.getGrade());
        assertEquals(courseName, result.getCourse());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_MaxGrade() {
        testEnrollment.setGrade(10.0);
        String courseName = "Chemistry 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals(10.0, result.getGrade());
        assertEquals(courseName, result.getCourse());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_DifferentCourseIds() {
        Enrollment enrollment1 = new Enrollment("student1@example.com", 1L, LocalDate.of(2026, 1, 10), 8.5);
        Enrollment enrollment2 = new Enrollment("student2@example.com", 2L, LocalDate.of(2026, 1, 11), 9.0);

        when(courseAPIService.getCourseNameById(1L)).thenReturn("Math 101");
        when(courseAPIService.getCourseNameById(2L)).thenReturn("Physics 101");

        EnrollmentDTO result1 = enrollmentMapper.toDto(enrollment1);
        EnrollmentDTO result2 = enrollmentMapper.toDto(enrollment2);

        assertEquals("Math 101", result1.getCourse());
        assertEquals("Physics 101", result2.getCourse());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
        verify(courseAPIService, times(1)).getCourseNameById(2L);
    }

    @Test
    void testToDto_DateFormatting() {
        testEnrollment.setEnrollmentDate(LocalDate.of(2026, 12, 31));
        String courseName = "Biology 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals("2026-12-31", result.getEnrollmentDate());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_DateFormattingWithSingleDigitMonth() {
        testEnrollment.setEnrollmentDate(LocalDate.of(2026, 3, 5));
        String courseName = "History 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals("2026-03-05", result.getEnrollmentDate());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_EmptyStudentEmail() {
        testEnrollment.setStudentEmail("");
        String courseName = "English 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals("", result.getStudent());
        assertEquals(courseName, result.getCourse());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_NullStudentEmail() {
        testEnrollment.setStudentEmail(null);
        String courseName = "Geography 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertNull(result.getStudent());
        assertEquals(courseName, result.getCourse());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_LongCourseName() {
        String longCourseName = "Advanced Topics in Artificial Intelligence and Machine Learning";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(longCourseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals(longCourseName, result.getCourse());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_CourseNameWithSpecialCharacters() {
        String courseName = "C++ Programming";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals(courseName, result.getCourse());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_EmailWithSpecialCharacters() {
        testEnrollment.setStudentEmail("john.doe+test@example.com");
        String courseName = "Philosophy 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals("john.doe+test@example.com", result.getStudent());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_DecimalGrade() {
        testEnrollment.setGrade(7.75);
        String courseName = "Economics 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals(7.75, result.getGrade());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_MultipleCallsSameEnrollment() {
        String courseName = "Statistics 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result1 = enrollmentMapper.toDto(testEnrollment);
        EnrollmentDTO result2 = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.getStudent(), result2.getStudent());
        assertEquals(result1.getCourse(), result2.getCourse());
        assertEquals(result1.getEnrollmentDate(), result2.getEnrollmentDate());
        assertEquals(result1.getGrade(), result2.getGrade());
        verify(courseAPIService, times(2)).getCourseNameById(1L);
    }

    @Test
    void testToDto_CourseAPIServiceThrowsException() {
        when(courseAPIService.getCourseNameById(1L))
                .thenThrow(new RuntimeException("Course service unavailable"));

        assertThrows(RuntimeException.class, () -> enrollmentMapper.toDto(testEnrollment));
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_CourseAPIServiceReturnsNull() {
        when(courseAPIService.getCourseNameById(1L)).thenReturn(null);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertNull(result.getCourse());
        assertEquals("john.doe@example.com", result.getStudent());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }

    @Test
    void testToDto_AllFieldsNull() {
        Enrollment enrollment = new Enrollment();
        enrollment.setCourseId(1L);
        enrollment.setGrade(0.0);

        when(courseAPIService.getCourseNameById(1L)).thenReturn("Test Course");

        EnrollmentDTO result = enrollmentMapper.toDto(enrollment);

        assertNotNull(result);
        assertNull(result.getStudent());
        assertNull(result.getEnrollmentDate());
        assertEquals(0.0, result.getGrade());
        assertEquals("Test Course", result.getCourse());
    }

    @Test
    void testToDto_LeapYearDate() {
        testEnrollment.setEnrollmentDate(LocalDate.of(2024, 2, 29));
        String courseName = "Astronomy 101";

        when(courseAPIService.getCourseNameById(1L)).thenReturn(courseName);

        EnrollmentDTO result = enrollmentMapper.toDto(testEnrollment);

        assertNotNull(result);
        assertEquals("2024-02-29", result.getEnrollmentDate());
        verify(courseAPIService, times(1)).getCourseNameById(1L);
    }
}