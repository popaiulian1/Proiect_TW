package org.upstarters.enrollment.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.dto.EnrollmentUpdateDTO;
import org.upstarters.enrollment.dto.StudentDTO;
import org.upstarters.enrollment.entity.Enrollment;
import org.upstarters.enrollment.mapper.EnrollmentMapper;
import org.upstarters.enrollment.repository.EnrollmentRepository;
import org.upstarters.enrollment.service.course.CourseAPIService;
import org.upstarters.enrollment.service.enrollment.EnrollmentService;
import org.upstarters.enrollment.service.student.StudentAPIService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseAPIService courseAPIService;

    @Mock
    private StudentAPIService studentAPIService;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private StudentDTO testStudent;
    private Enrollment testEnrollment;
    private EnrollmentDTO testEnrollmentDTO;

    @BeforeEach
    void setUp() {
        testStudent = new StudentDTO("John", "Doe", "john.doe@example.com", "Computer Science");
        
        testEnrollment = new Enrollment();
        testEnrollment.setId(1L);
        testEnrollment.setStudentEmail("john.doe@example.com");
        testEnrollment.setCourseId(1L);
        testEnrollment.setEnrollmentDate(LocalDate.now());
        testEnrollment.setGrade(8.5);

        testEnrollmentDTO = new EnrollmentDTO("john.doe@example.com", "Math 101", "2026-01-12", 8.5);
    }

    @Test
    void testEnrollStudentInCourse_Success() {
        String studentEmail = "john.doe@example.com";
        String courseName = "Math 101";
        Long courseId = 1L;

        when(studentAPIService.getStudentByEmail(studentEmail)).thenReturn(testStudent);
        when(courseAPIService.getCourseIdByName(courseName)).thenReturn(courseId);
        when(enrollmentRepository.existsByStudentEmailAndCourseId(studentEmail, courseId)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);

        enrollmentService.enrollStudentInCourse(studentEmail, courseName);

        verify(studentAPIService, times(1)).getStudentByEmail(studentEmail);
        verify(courseAPIService, times(1)).getCourseIdByName(courseName);
        verify(enrollmentRepository, times(1)).existsByStudentEmailAndCourseId(studentEmail, courseId);
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void testEnrollStudentInCourse_StudentNotFound() {
        String studentEmail = "nonexistent@example.com";
        String courseName = "Math 101";

        when(studentAPIService.getStudentByEmail(studentEmail)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> enrollmentService.enrollStudentInCourse(studentEmail, courseName));

        assertEquals("Student not found with email: " + studentEmail, exception.getMessage());
        verify(courseAPIService, never()).getCourseIdByName(anyString());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testEnrollStudentInCourse_CourseNotFound() {
        String studentEmail = "john.doe@example.com";
        String courseName = "NonExistent Course";

        when(studentAPIService.getStudentByEmail(studentEmail)).thenReturn(testStudent);
        when(courseAPIService.getCourseIdByName(courseName)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> enrollmentService.enrollStudentInCourse(studentEmail, courseName));

        assertEquals("Course not found with name: " + courseName, exception.getMessage());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testEnrollStudentInCourse_AlreadyEnrolled() {
        String studentEmail = "john.doe@example.com";
        String courseName = "Math 101";
        Long courseId = 1L;

        when(studentAPIService.getStudentByEmail(studentEmail)).thenReturn(testStudent);
        when(courseAPIService.getCourseIdByName(courseName)).thenReturn(courseId);
        when(enrollmentRepository.existsByStudentEmailAndCourseId(studentEmail, courseId)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> enrollmentService.enrollStudentInCourse(studentEmail, courseName));

        assertEquals("Student is already enrolled in the course", exception.getMessage());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testGetAllEnrollments_Success() {
        Enrollment enrollment1 = new Enrollment("student1@example.com", 1L, LocalDate.now(), 8.5);
        Enrollment enrollment2 = new Enrollment("student2@example.com", 2L, LocalDate.now(), 9.0);
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        when(enrollmentRepository.findAll()).thenReturn(enrollments);

        List<Enrollment> result = enrollmentService.getAllEnrollments();

        assertEquals(2, result.size());
        assertEquals(enrollments, result);
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void testGetAllEnrollments_EmptyList() {
        when(enrollmentRepository.findAll()).thenReturn(Arrays.asList());

        List<Enrollment> result = enrollmentService.getAllEnrollments();

        assertTrue(result.isEmpty());
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void testGetEnrollment_Success() {
        Long enrollmentId = 1L;

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(testEnrollment));
        when(enrollmentMapper.toDto(testEnrollment)).thenReturn(testEnrollmentDTO);

        EnrollmentDTO result = enrollmentService.getEnrollment(enrollmentId);

        assertNotNull(result);
        assertEquals(testEnrollmentDTO, result);
        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(enrollmentMapper, times(1)).toDto(testEnrollment);
    }

    @Test
    void testGetEnrollment_NotFound() {
        Long enrollmentId = 999L;

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.getEnrollment(enrollmentId));

        assertEquals("Enrollment not found with id: " + enrollmentId, exception.getMessage());
        verify(enrollmentMapper, never()).toDto(any(Enrollment.class));
    }

    @Test
    void testUpdateEnrollment_Success_BothFields() {
        Long enrollmentId = 1L;
        EnrollmentUpdateDTO updateDTO = new EnrollmentUpdateDTO();
        updateDTO.setCourseName("Physics 101");
        updateDTO.setGrade(9.5);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(testEnrollment));
        when(courseAPIService.getCourseIdByName("Physics 101")).thenReturn(2L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);

        enrollmentService.updateEnrollment(enrollmentId, updateDTO);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(courseAPIService, times(1)).getCourseIdByName("Physics 101");
        verify(enrollmentRepository, times(1)).save(testEnrollment);
        assertEquals(2L, testEnrollment.getCourseId());
        assertEquals(9.5, testEnrollment.getGrade());
    }

    @Test
    void testUpdateEnrollment_OnlyGrade() {
        Long enrollmentId = 1L;
        EnrollmentUpdateDTO updateDTO = new EnrollmentUpdateDTO();
        updateDTO.setGrade(9.5);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(testEnrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);

        enrollmentService.updateEnrollment(enrollmentId, updateDTO);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(courseAPIService, never()).getCourseIdByName(anyString());
        verify(enrollmentRepository, times(1)).save(testEnrollment);
        assertEquals(9.5, testEnrollment.getGrade());
    }

    @Test
    void testUpdateEnrollment_OnlyCourse() {
        Long enrollmentId = 1L;
        EnrollmentUpdateDTO updateDTO = new EnrollmentUpdateDTO();
        updateDTO.setCourseName("Physics 101");

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(testEnrollment));
        when(courseAPIService.getCourseIdByName("Physics 101")).thenReturn(2L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(testEnrollment);

        enrollmentService.updateEnrollment(enrollmentId, updateDTO);

        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(courseAPIService, times(1)).getCourseIdByName("Physics 101");
        verify(enrollmentRepository, times(1)).save(testEnrollment);
        assertEquals(2L, testEnrollment.getCourseId());
    }

    @Test
    void testUpdateEnrollment_EnrollmentNotFound() {
        Long enrollmentId = 999L;
        EnrollmentUpdateDTO updateDTO = new EnrollmentUpdateDTO();
        updateDTO.setGrade(9.5);

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.updateEnrollment(enrollmentId, updateDTO));

        assertEquals("Enrollment not found with id: " + enrollmentId, exception.getMessage());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testUpdateEnrollment_CourseNotFound() {
        Long enrollmentId = 1L;
        EnrollmentUpdateDTO updateDTO = new EnrollmentUpdateDTO();
        updateDTO.setCourseName("NonExistent Course");

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(testEnrollment));
        when(courseAPIService.getCourseIdByName("NonExistent Course")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> enrollmentService.updateEnrollment(enrollmentId, updateDTO));

        assertEquals("Course not found with name: NonExistent Course", exception.getMessage());
        verify(enrollmentRepository, never()).save(any(Enrollment.class));
    }

    @Test
    void testDeleteEnrollment_Success() {
        Long enrollmentId = 1L;

        when(enrollmentRepository.existsById(enrollmentId)).thenReturn(true);
        doNothing().when(enrollmentRepository).deleteById(enrollmentId);

        enrollmentService.deleteEnrollment(enrollmentId);

        verify(enrollmentRepository, times(1)).existsById(enrollmentId);
        verify(enrollmentRepository, times(1)).deleteById(enrollmentId);
    }

    @Test
    void testDeleteEnrollment_NotFound() {
        Long enrollmentId = 999L;

        when(enrollmentRepository.existsById(enrollmentId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.deleteEnrollment(enrollmentId));

        assertEquals("Enrollment not found with id: " + enrollmentId, exception.getMessage());
        verify(enrollmentRepository, never()).deleteById(anyLong());
    }

    @Test
    void testStudentsFilteredByCourse_Success() {
        String courseName = "Math 101";
        Long courseId = 1L;
        Enrollment enrollment1 = new Enrollment("student1@example.com", courseId, LocalDate.now(), 8.5);
        Enrollment enrollment2 = new Enrollment("student2@example.com", courseId, LocalDate.now(), 9.0);
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        EnrollmentDTO dto1 = new EnrollmentDTO("student1@example.com", courseName, "2026-01-12", 8.5);
        EnrollmentDTO dto2 = new EnrollmentDTO("student2@example.com", courseName, "2026-01-12", 9.0);

        when(courseAPIService.getCourseIdByName(courseName)).thenReturn(courseId);
        when(enrollmentRepository.findAllByCourseId(courseId)).thenReturn(Optional.of(enrollments));
        when(enrollmentMapper.toDto(enrollment1)).thenReturn(dto1);
        when(enrollmentMapper.toDto(enrollment2)).thenReturn(dto2);

        List<EnrollmentDTO> result = enrollmentService.studentsFilteredByCourse(courseName);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(courseAPIService, times(1)).getCourseIdByName(courseName);
        verify(enrollmentRepository, times(1)).findAllByCourseId(courseId);
    }

    @Test
    void testStudentsFilteredByCourse_CourseNotFound() {
        String courseName = "NonExistent Course";

        when(courseAPIService.getCourseIdByName(courseName)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.studentsFilteredByCourse(courseName));

        assertEquals("Course not found with name: " + courseName, exception.getMessage());
        verify(enrollmentRepository, never()).findAllByCourseId(anyLong());
    }

    @Test
    void testStudentsFilteredByCourse_EmptyList() {
        String courseName = "Math 101";
        Long courseId = 1L;

        when(courseAPIService.getCourseIdByName(courseName)).thenReturn(courseId);
        when(enrollmentRepository.findAllByCourseId(courseId)).thenReturn(Optional.empty());

        List<EnrollmentDTO> result = enrollmentService.studentsFilteredByCourse(courseName);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFailingStudents_Success() {
        Enrollment enrollment1 = new Enrollment("student1@example.com", 1L, LocalDate.now(), 4.0);
        Enrollment enrollment2 = new Enrollment("student2@example.com", 2L, LocalDate.now(), 3.5);
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        EnrollmentDTO dto1 = new EnrollmentDTO("student1@example.com", "Math 101", "2026-01-12", 4.0);
        EnrollmentDTO dto2 = new EnrollmentDTO("student2@example.com", "Physics 101", "2026-01-12", 3.5);

        when(enrollmentRepository.findAllByGradeLessThanEqual(5.0)).thenReturn(Optional.of(enrollments));
        when(enrollmentMapper.toDto(enrollment1)).thenReturn(dto1);
        when(enrollmentMapper.toDto(enrollment2)).thenReturn(dto2);

        List<EnrollmentDTO> result = enrollmentService.failingStudents();

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(enrollmentRepository, times(1)).findAllByGradeLessThanEqual(5.0);
    }

    @Test
    void testFailingStudents_EmptyList() {
        when(enrollmentRepository.findAllByGradeLessThanEqual(5.0)).thenReturn(Optional.empty());

        List<EnrollmentDTO> result = enrollmentService.failingStudents();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTop5InCourse_Success() {
        String courseName = "Math 101";
        Long courseId = 1L;

        Enrollment e1 = new Enrollment("student1@example.com", courseId, LocalDate.now(), 10.0);
        Enrollment e2 = new Enrollment("student2@example.com", courseId, LocalDate.now(), 9.5);
        Enrollment e3 = new Enrollment("student3@example.com", courseId, LocalDate.now(), 9.0);
        Enrollment e4 = new Enrollment("student4@example.com", courseId, LocalDate.now(), 8.5);
        Enrollment e5 = new Enrollment("student5@example.com", courseId, LocalDate.now(), 8.0);
        Enrollment e6 = new Enrollment("student6@example.com", courseId, LocalDate.now(), 7.5);

        List<Enrollment> enrollments = Arrays.asList(e1, e2, e3, e4, e5, e6);

        EnrollmentDTO dto1 = new EnrollmentDTO("student1@example.com", courseName, "2026-01-12", 10.0);
        EnrollmentDTO dto2 = new EnrollmentDTO("student2@example.com", courseName, "2026-01-12", 9.5);
        EnrollmentDTO dto3 = new EnrollmentDTO("student3@example.com", courseName, "2026-01-12", 9.0);
        EnrollmentDTO dto4 = new EnrollmentDTO("student4@example.com", courseName, "2026-01-12", 8.5);
        EnrollmentDTO dto5 = new EnrollmentDTO("student5@example.com", courseName, "2026-01-12", 8.0);
        EnrollmentDTO dto6 = new EnrollmentDTO("student6@example.com", courseName, "2026-01-12", 7.5);

        when(courseAPIService.getCourseIdByName(courseName)).thenReturn(courseId);
        when(enrollmentRepository.findAllByCourseId(courseId)).thenReturn(Optional.of(enrollments));
        when(enrollmentMapper.toDto(e1)).thenReturn(dto1);
        when(enrollmentMapper.toDto(e2)).thenReturn(dto2);
        when(enrollmentMapper.toDto(e3)).thenReturn(dto3);
        when(enrollmentMapper.toDto(e4)).thenReturn(dto4);
        when(enrollmentMapper.toDto(e5)).thenReturn(dto5);
        when(enrollmentMapper.toDto(e6)).thenReturn(dto6);

        List<EnrollmentDTO> result = enrollmentService.getTop5InCourse(courseName);

        assertEquals(5, result.size());
        assertEquals(10.0, result.get(0).getGrade());
        assertEquals(8.0, result.get(4).getGrade());
        verify(courseAPIService, times(1)).getCourseIdByName(courseName);
        verify(enrollmentRepository, times(1)).findAllByCourseId(courseId);
    }

    @Test
    void testGetTop5InCourse_CourseNotFound() {
        String courseName = "NonExistent Course";

        when(courseAPIService.getCourseIdByName(courseName)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.getTop5InCourse(courseName));

        assertEquals("Course not found with name: " + courseName, exception.getMessage());
    }

    @Test
    void testGetTop5InCourse_LessThan5Students() {
        String courseName = "Math 101";
        Long courseId = 1L;

        Enrollment e1 = new Enrollment("student1@example.com", courseId, LocalDate.now(), 10.0);
        Enrollment e2 = new Enrollment("student2@example.com", courseId, LocalDate.now(), 9.5);
        List<Enrollment> enrollments = Arrays.asList(e1, e2);

        EnrollmentDTO dto1 = new EnrollmentDTO("student1@example.com", courseName, "2026-01-12", 10.0);
        EnrollmentDTO dto2 = new EnrollmentDTO("student2@example.com", courseName, "2026-01-12", 9.5);

        when(courseAPIService.getCourseIdByName(courseName)).thenReturn(courseId);
        when(enrollmentRepository.findAllByCourseId(courseId)).thenReturn(Optional.of(enrollments));
        when(enrollmentMapper.toDto(e1)).thenReturn(dto1);
        when(enrollmentMapper.toDto(e2)).thenReturn(dto2);

        List<EnrollmentDTO> result = enrollmentService.getTop5InCourse(courseName);

        assertEquals(2, result.size());
    }

    @Test
    void testGetEnrollmentsByStudent_Success() {
        String studentEmail = "john.doe@example.com";

        Enrollment enrollment1 = new Enrollment(studentEmail, 1L, LocalDate.now(), 8.5);
        Enrollment enrollment2 = new Enrollment(studentEmail, 2L, LocalDate.now(), 9.0);
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        EnrollmentDTO dto1 = new EnrollmentDTO(studentEmail, "Math 101", "2026-01-12", 8.5);
        EnrollmentDTO dto2 = new EnrollmentDTO(studentEmail, "Physics 101", "2026-01-12", 9.0);

        when(studentAPIService.getStudentByEmail(studentEmail)).thenReturn(testStudent);
        when(enrollmentRepository.findAllByStudentEmail(studentEmail)).thenReturn(Optional.of(enrollments));
        when(enrollmentMapper.toDto(enrollment1)).thenReturn(dto1);
        when(enrollmentMapper.toDto(enrollment2)).thenReturn(dto2);

        List<EnrollmentDTO> result = enrollmentService.getEnrollmentsByStudent(studentEmail);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(studentAPIService, times(1)).getStudentByEmail(studentEmail);
        verify(enrollmentRepository, times(1)).findAllByStudentEmail(studentEmail);
    }

    @Test
    void testGetEnrollmentsByStudent_StudentNotFound() {
        String studentEmail = "nonexistent@example.com";

        when(studentAPIService.getStudentByEmail(studentEmail)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.getEnrollmentsByStudent(studentEmail));

        assertEquals("Student not found with email: " + studentEmail, exception.getMessage());
        verify(enrollmentRepository, never()).findAllByStudentEmail(anyString());
    }

    @Test
    void testGetEnrollmentsByStudent_EmptyList() {
        String studentEmail = "john.doe@example.com";

        when(studentAPIService.getStudentByEmail(studentEmail)).thenReturn(testStudent);
        when(enrollmentRepository.findAllByStudentEmail(studentEmail)).thenReturn(Optional.empty());

        List<EnrollmentDTO> result = enrollmentService.getEnrollmentsByStudent(studentEmail);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetStudentDetailsFromEnrollment_Success() {
        Long enrollmentId = 1L;

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(testEnrollment));
        when(studentAPIService.getStudentByEmail(testEnrollment.getStudentEmail())).thenReturn(testStudent);

        StudentDTO result = enrollmentService.getStudentDetailsFromEnrollment(enrollmentId);

        assertNotNull(result);
        assertEquals(testStudent, result);
        verify(enrollmentRepository, times(1)).findById(enrollmentId);
        verify(studentAPIService, times(1)).getStudentByEmail(testEnrollment.getStudentEmail());
    }

    @Test
    void testGetStudentDetailsFromEnrollment_EnrollmentNotFound() {
        Long enrollmentId = 999L;

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.getStudentDetailsFromEnrollment(enrollmentId));

        assertEquals("Enrollment not found with id: " + enrollmentId, exception.getMessage());
        verify(studentAPIService, never()).getStudentByEmail(anyString());
    }

    @Test
    void testGetStudentDetailsFromEnrollment_StudentNotFound() {
        Long enrollmentId = 1L;

        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(testEnrollment));
        when(studentAPIService.getStudentByEmail(testEnrollment.getStudentEmail())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.getStudentDetailsFromEnrollment(enrollmentId));

        assertEquals("Student not found with email: " + testEnrollment.getStudentEmail(), exception.getMessage());
    }

    @Test
    void testUpdateStudentEmailInEnrollments_Success() {
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";

        StudentDTO oldStudent = new StudentDTO("John", "Doe", oldEmail, "Computer Science");
        StudentDTO updatedStudent = new StudentDTO("John", "Doe", newEmail, "Computer Science");

        Enrollment enrollment1 = new Enrollment(oldEmail, 1L, LocalDate.now(), 8.5);
        Enrollment enrollment2 = new Enrollment(oldEmail, 2L, LocalDate.now(), 9.0);
        List<Enrollment> enrollments = Arrays.asList(enrollment1, enrollment2);

        EnrollmentDTO resultDTO = new EnrollmentDTO(newEmail, "Math 101", "2026-01-12", 8.5);

        when(studentAPIService.getStudentByEmail(oldEmail)).thenReturn(oldStudent);
        when(studentAPIService.updateStudentInfo(eq(oldEmail), any(StudentDTO.class))).thenReturn(updatedStudent);
        when(enrollmentRepository.findAllByStudentEmail(oldEmail)).thenReturn(Optional.of(enrollments));
        when(enrollmentRepository.saveAll(anyList())).thenReturn(enrollments);
        when(enrollmentMapper.toDto(any(Enrollment.class))).thenReturn(resultDTO);

        EnrollmentDTO result = enrollmentService.updateStudentEmailInEnrollments(oldEmail, newEmail);

        assertNotNull(result);
        assertEquals(newEmail, result.getStudent());
        verify(studentAPIService, times(1)).getStudentByEmail(oldEmail);
        verify(studentAPIService, times(1)).updateStudentInfo(eq(oldEmail), any(StudentDTO.class));
        verify(enrollmentRepository, times(1)).findAllByStudentEmail(oldEmail);
        verify(enrollmentRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUpdateStudentEmailInEnrollments_StudentNotFound() {
        String oldEmail = "nonexistent@example.com";
        String newEmail = "new@example.com";

        when(studentAPIService.getStudentByEmail(oldEmail)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.updateStudentEmailInEnrollments(oldEmail, newEmail));

        assertEquals("Student not found with email: " + oldEmail, exception.getMessage());
        verify(studentAPIService, never()).updateStudentInfo(anyString(), any(StudentDTO.class));
        verify(enrollmentRepository, never()).saveAll(anyList());
    }

    @Test
    void testUpdateStudentEmailInEnrollments_StudentServiceUpdateFails() {
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";

        StudentDTO oldStudent = new StudentDTO("John", "Doe", oldEmail, "Computer Science");

        when(studentAPIService.getStudentByEmail(oldEmail)).thenReturn(oldStudent);
        when(studentAPIService.updateStudentInfo(eq(oldEmail), any(StudentDTO.class))).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> enrollmentService.updateStudentEmailInEnrollments(oldEmail, newEmail));

        assertTrue(exception.getMessage().contains("Failed to update student email in student service"));
        verify(enrollmentRepository, never()).saveAll(anyList());
    }

    @Test
    void testUpdateStudentEmailInEnrollments_NoEnrollmentsFound() {
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";

        StudentDTO oldStudent = new StudentDTO("John", "Doe", oldEmail, "Computer Science");
        StudentDTO updatedStudent = new StudentDTO("John", "Doe", newEmail, "Computer Science");

        when(studentAPIService.getStudentByEmail(oldEmail)).thenReturn(oldStudent);
        when(studentAPIService.updateStudentInfo(eq(oldEmail), any(StudentDTO.class))).thenReturn(updatedStudent);
        when(enrollmentRepository.findAllByStudentEmail(oldEmail)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> enrollmentService.updateStudentEmailInEnrollments(oldEmail, newEmail));

        assertEquals("No enrollments found for student with email: " + oldEmail, exception.getMessage());
        verify(enrollmentRepository, never()).saveAll(anyList());
    }
}