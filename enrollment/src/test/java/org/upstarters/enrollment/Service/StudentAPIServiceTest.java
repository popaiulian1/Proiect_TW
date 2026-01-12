package org.upstarters.enrollment.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upstarters.enrollment.dto.StudentDTO;
import org.upstarters.enrollment.service.student.StudentAPIService;
import org.upstarters.enrollment.service.student.StudentFeignClient;

import feign.FeignException;

@ExtendWith(MockitoExtension.class)
class StudentAPIServiceTest {

    @Mock
    private StudentFeignClient studentFeignClient;

    @InjectMocks
    private StudentAPIService studentAPIService;

    private StudentDTO testStudent;

    @BeforeEach
    void setUp() {
        testStudent = new StudentDTO("John", "Doe", "john.doe@example.com", "Computer Science");
    }

    @Test
    void testGetStudentEmailById_Success() {
        Long studentId = 1L;
        
        when(studentFeignClient.getStudentById(studentId)).thenReturn(testStudent);

        String result = studentAPIService.getStudentEmailById(studentId);

        assertNotNull(result);
        assertEquals("john.doe@example.com", result);
        verify(studentFeignClient, times(1)).getStudentById(studentId);
    }

    @Test
    void testGetStudentEmailById_StudentNotFound() {
        Long studentId = 999L;
        
        when(studentFeignClient.getStudentById(studentId))
                .thenThrow(FeignException.NotFound.class);

        String result = studentAPIService.getStudentEmailById(studentId);

        assertNull(result);
        verify(studentFeignClient, times(1)).getStudentById(studentId);
    }

    @Test
    void testGetStudentEmailById_FeignException() {
        Long studentId = 1L;
        
        when(studentFeignClient.getStudentById(studentId))
                .thenThrow(new RuntimeException("Service unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentAPIService.getStudentEmailById(studentId));

        assertTrue(exception.getMessage().contains("Failed to get student email by ID: " + studentId));
        verify(studentFeignClient, times(1)).getStudentById(studentId);
    }

    @Test
    void testGetStudentByEmail_Success() {
        String email = "john.doe@example.com";
        
        when(studentFeignClient.getStudentByEmail(email)).thenReturn(testStudent);

        StudentDTO result = studentAPIService.getStudentByEmail(email);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(email, result.getEmail());
        assertEquals("Computer Science", result.getMajor());
        verify(studentFeignClient, times(1)).getStudentByEmail(email);
    }

    @Test
    void testGetStudentByEmail_StudentNotFound() {
        String email = "nonexistent@example.com";
        
        when(studentFeignClient.getStudentByEmail(email))
                .thenThrow(FeignException.NotFound.class);

        StudentDTO result = studentAPIService.getStudentByEmail(email);

        assertNull(result);
        verify(studentFeignClient, times(1)).getStudentByEmail(email);
    }

    @Test
    void testGetStudentByEmail_FeignException() {
        String email = "john.doe@example.com";
        
        when(studentFeignClient.getStudentByEmail(email))
                .thenThrow(new RuntimeException("Service unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentAPIService.getStudentByEmail(email));

        assertTrue(exception.getMessage().contains("Failed to get student by email: " + email));
        verify(studentFeignClient, times(1)).getStudentByEmail(email);
    }

    @Test
    void testUpdateStudentInfo_Success() {
        String oldEmail = "old@example.com";
        StudentDTO updatedStudent = new StudentDTO("John", "Doe", "new@example.com", "Computer Science");
        
        when(studentFeignClient.updateStudent(eq(oldEmail), any(StudentDTO.class)))
                .thenReturn(updatedStudent);

        StudentDTO result = studentAPIService.updateStudentInfo(oldEmail, updatedStudent);

        assertNotNull(result);
        assertEquals("new@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("Computer Science", result.getMajor());
        verify(studentFeignClient, times(1)).updateStudent(eq(oldEmail), any(StudentDTO.class));
    }

    @Test
    void testUpdateStudentInfo_FeignException() {
        String oldEmail = "old@example.com";
        StudentDTO updatedStudent = new StudentDTO("John", "Doe", "new@example.com", "Computer Science");
        
        when(studentFeignClient.updateStudent(eq(oldEmail), any(StudentDTO.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentAPIService.updateStudentInfo(oldEmail, updatedStudent));

        assertTrue(exception.getMessage().contains("Failed to update student with email: " + updatedStudent.getEmail()));
        verify(studentFeignClient, times(1)).updateStudent(eq(oldEmail), any(StudentDTO.class));
    }

    @Test
    void testUpdateStudentInfo_UpdateMultipleFields() {
        String oldEmail = "old@example.com";
        StudentDTO updatedStudent = new StudentDTO("Jane", "Smith", "jane.smith@example.com", "Mathematics");
        
        when(studentFeignClient.updateStudent(eq(oldEmail), any(StudentDTO.class)))
                .thenReturn(updatedStudent);

        StudentDTO result = studentAPIService.updateStudentInfo(oldEmail, updatedStudent);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals("Mathematics", result.getMajor());
        verify(studentFeignClient, times(1)).updateStudent(eq(oldEmail), any(StudentDTO.class));
    }

    @Test
    void testGetStudentByEmail_EmptyEmail() {
        String email = "";
        
        when(studentFeignClient.getStudentByEmail(email))
                .thenThrow(new RuntimeException("Invalid email"));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentAPIService.getStudentByEmail(email));

        assertTrue(exception.getMessage().contains("Failed to get student by email: " + email));
    }

    @Test
    void testGetStudentEmailById_NullResponse() {
        Long studentId = 1L;
        
        when(studentFeignClient.getStudentById(studentId)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentAPIService.getStudentEmailById(studentId));

        assertTrue(exception.getMessage().contains("Failed to get student email by ID: " + studentId));
    }

    @Test
    void testUpdateStudentInfo_NullStudentDTO() {
        String oldEmail = "old@example.com";

        when(studentFeignClient.updateStudent(eq(oldEmail), any()))
                .thenThrow(new NullPointerException("Cannot invoke getEmail on null"));

        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> studentAPIService.updateStudentInfo(oldEmail, null));

        assertNotNull(exception);
    }

    @Test
    void testGetStudentById_MultipleCallsSameId() {
        Long studentId = 1L;
        
        when(studentFeignClient.getStudentById(studentId)).thenReturn(testStudent);

        String result1 = studentAPIService.getStudentEmailById(studentId);
        String result2 = studentAPIService.getStudentEmailById(studentId);

        assertEquals(result1, result2);
        verify(studentFeignClient, times(2)).getStudentById(studentId);
    }

    @Test
    void testGetStudentByEmail_SpecialCharactersInEmail() {
        String email = "test+filter@example.com";
        StudentDTO student = new StudentDTO("Test", "User", email, "Engineering");
        
        when(studentFeignClient.getStudentByEmail(email)).thenReturn(student);

        StudentDTO result = studentAPIService.getStudentByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(studentFeignClient, times(1)).getStudentByEmail(email);
    }
}