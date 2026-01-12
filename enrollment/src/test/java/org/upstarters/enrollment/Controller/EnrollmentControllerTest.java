package org.upstarters.enrollment.Controller;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.upstarters.enrollment.controller.EnrollmentController;
import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.dto.EnrollmentRequestDTO;
import org.upstarters.enrollment.dto.EnrollmentUpdateDTO;
import org.upstarters.enrollment.dto.StudentDTO;
import org.upstarters.enrollment.entity.Enrollment;
import org.upstarters.enrollment.mapper.EnrollmentMapper;
import org.upstarters.enrollment.service.enrollment.EnrollmentService;

import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class EnrollmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private EnrollmentMapper enrollmentMapper;

    @InjectMocks
    private EnrollmentController enrollmentController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(enrollmentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPing_AsAdmin_ShouldReturnPong() throws Exception {
        mockMvc.perform(get("/enrollments/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testPing_AsStudent_ShouldReturnPong() throws Exception {
        mockMvc.perform(get("/enrollments/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEnrollment_Success() throws Exception {
        EnrollmentRequestDTO requestDTO = new EnrollmentRequestDTO("student@example.com", "Math 101");

        doNothing().when(enrollmentService).enrollStudentInCourse(anyString(), anyString());

        mockMvc.perform(post("/enrollments/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Enrollment created successfully"));

        verify(enrollmentService, times(1)).enrollStudentInCourse("student@example.com", "Math 101");
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testCreateEnrollment_MissingStudentEmail_ShouldReturnBadRequest() throws Exception {
        EnrollmentRequestDTO requestDTO = new EnrollmentRequestDTO(null, "Math 101");

        mockMvc.perform(post("/enrollments/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Student and course must be provided"));

        verify(enrollmentService, never()).enrollStudentInCourse(anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testCreateEnrollment_MissingCourseName_ShouldReturnBadRequest() throws Exception {
        EnrollmentRequestDTO requestDTO = new EnrollmentRequestDTO("student@example.com", null);

        mockMvc.perform(post("/enrollments/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Student and course must be provided"));

        verify(enrollmentService, never()).enrollStudentInCourse(anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEnrollment_ServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        EnrollmentRequestDTO requestDTO = new EnrollmentRequestDTO("student@example.com", "Math 101");

        doThrow(new IllegalArgumentException("Student not found"))
                .when(enrollmentService).enrollStudentInCourse(anyString(), anyString());

        mockMvc.perform(post("/enrollments/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Student not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllEnrollments_Success() throws Exception {
        Enrollment enrollment1 = new Enrollment("student1@example.com", 1L, null, 8.5);
        Enrollment enrollment2 = new Enrollment("student2@example.com", 2L, null, 9.0);

        EnrollmentDTO dto1 = new EnrollmentDTO("student1@example.com", "Math 101", "2026-01-01", 8.5);
        EnrollmentDTO dto2 = new EnrollmentDTO("student2@example.com", "Physics 101", "2026-01-02", 9.0);

        when(enrollmentService.getAllEnrollments()).thenReturn(Arrays.asList(enrollment1, enrollment2));
        when(enrollmentMapper.toDto(enrollment1)).thenReturn(dto1);
        when(enrollmentMapper.toDto(enrollment2)).thenReturn(dto2);

        mockMvc.perform(get("/enrollments/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].student").value("student1@example.com"))
                .andExpect(jsonPath("$[0].course").value("Math 101"))
                .andExpect(jsonPath("$[1].student").value("student2@example.com"))
                .andExpect(jsonPath("$[1].course").value("Physics 101"));

        verify(enrollmentService, times(1)).getAllEnrollments();
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetAllEnrollments_EmptyList() throws Exception {
        when(enrollmentService.getAllEnrollments()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/enrollments/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEnrollmentById_Success() throws Exception {
        Long enrollmentId = 1L;
        EnrollmentDTO dto = new EnrollmentDTO("student@example.com", "Math 101", "2026-01-01", 8.5);

        when(enrollmentService.getEnrollment(enrollmentId)).thenReturn(dto);

        mockMvc.perform(get("/enrollments/enrollment/{id}", enrollmentId))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.student").value("student@example.com"))
                .andExpect(jsonPath("$.course").value("Math 101"))
                .andExpect(jsonPath("$.grade").value(8.5));

        verify(enrollmentService, times(1)).getEnrollment(enrollmentId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetEnrollmentById_NotFound() throws Exception {
        Long enrollmentId = 999L;

        when(enrollmentService.getEnrollment(enrollmentId))
                .thenThrow(new RuntimeException("Enrollment not found"));

        mockMvc.perform(get("/enrollments/enrollment/{id}", enrollmentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateEnrollment_Success() throws Exception {
        Long enrollmentId = 1L;
        EnrollmentUpdateDTO updateDTO = new EnrollmentUpdateDTO();
        updateDTO.setCourseName("Updated Course");
        updateDTO.setGrade(9.5);

        doNothing().when(enrollmentService).updateEnrollment(eq(enrollmentId), any(EnrollmentUpdateDTO.class));

        mockMvc.perform(put("/enrollments/update/{id}", enrollmentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Enrollment updated successfully"));

        verify(enrollmentService, times(1)).updateEnrollment(eq(enrollmentId), any(EnrollmentUpdateDTO.class));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testUpdateEnrollment_NotFound() throws Exception {
        Long enrollmentId = 999L;
        EnrollmentUpdateDTO updateDTO = new EnrollmentUpdateDTO();
        updateDTO.setCourseName("Updated Course");
        updateDTO.setGrade(9.5);

        doThrow(new RuntimeException("Enrollment not found"))
                .when(enrollmentService).updateEnrollment(eq(enrollmentId), any(EnrollmentUpdateDTO.class));

        mockMvc.perform(put("/enrollments/update/{id}", enrollmentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Enrollment not found with id: " + enrollmentId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteEnrollment_Success() throws Exception {
        Long enrollmentId = 1L;

        doNothing().when(enrollmentService).deleteEnrollment(enrollmentId);

        mockMvc.perform(delete("/enrollments/delete/{id}", enrollmentId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Enrollment deleted successfully"));

        verify(enrollmentService, times(1)).deleteEnrollment(enrollmentId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteEnrollment_NotFound() throws Exception {
        Long enrollmentId = 999L;

        doThrow(new RuntimeException("Enrollment not found"))
                .when(enrollmentService).deleteEnrollment(enrollmentId);

        mockMvc.perform(delete("/enrollments/delete/{id}", enrollmentId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Enrollment not found with id: " + enrollmentId));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentsByCourse_Success() throws Exception {
        String courseName = "Math 101";
        EnrollmentDTO dto1 = new EnrollmentDTO("student1@example.com", courseName, "2026-01-01", 8.5);
        EnrollmentDTO dto2 = new EnrollmentDTO("student2@example.com", courseName, "2026-01-02", 9.0);

        when(enrollmentService.studentsFilteredByCourse(courseName))
                .thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/enrollments/students/{course}", courseName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].course").value(courseName))
                .andExpect(jsonPath("$[1].course").value(courseName));

        verify(enrollmentService, times(1)).studentsFilteredByCourse(courseName);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetStudentsByCourse_NotFound() throws Exception {
        String courseName = "NonExistent Course";

        when(enrollmentService.studentsFilteredByCourse(courseName))
                .thenThrow(new RuntimeException("Course not found"));

        mockMvc.perform(get("/enrollments/students/{course}", courseName))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentsWithFailingGrades_Success() throws Exception {
        EnrollmentDTO dto1 = new EnrollmentDTO("student1@example.com", "Math 101", "2026-01-01", 4.0);
        EnrollmentDTO dto2 = new EnrollmentDTO("student2@example.com", "Physics 101", "2026-01-02", 3.5);

        when(enrollmentService.failingStudents()).thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/enrollments/students/failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].grade").value(4.0))
                .andExpect(jsonPath("$[1].grade").value(3.5));

        verify(enrollmentService, times(1)).failingStudents();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentsWithFailingGrades_EmptyList() throws Exception {
        when(enrollmentService.failingStudents()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/enrollments/students/failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTop5StudentsInCourse_Success() throws Exception {
        String courseName = "Math 101";
        List<EnrollmentDTO> topStudents = Arrays.asList(
                new EnrollmentDTO("student1@example.com", courseName, "2026-01-01", 10.0),
                new EnrollmentDTO("student2@example.com", courseName, "2026-01-02", 9.8),
                new EnrollmentDTO("student3@example.com", courseName, "2026-01-03", 9.5),
                new EnrollmentDTO("student4@example.com", courseName, "2026-01-04", 9.2),
                new EnrollmentDTO("student5@example.com", courseName, "2026-01-05", 9.0)
        );

        when(enrollmentService.getTop5InCourse(courseName)).thenReturn(topStudents);

        mockMvc.perform(get("/enrollments/course/{course}/top5", courseName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].grade").value(10.0))
                .andExpect(jsonPath("$[4].grade").value(9.0));

        verify(enrollmentService, times(1)).getTop5InCourse(courseName);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTop5StudentsInCourse_NotFound() throws Exception {
        String courseName = "NonExistent Course";

        when(enrollmentService.getTop5InCourse(courseName))
                .thenThrow(new RuntimeException("Course not found"));

        mockMvc.perform(get("/enrollments/course/{course}/top5", courseName))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetStudentDetailsFromEnrollment_Success() throws Exception {
        Long enrollmentId = 1L;
        StudentDTO studentDTO = new StudentDTO("John", "Doe", "john.doe@example.com", "Computer Science");

        when(enrollmentService.getStudentDetailsFromEnrollment(enrollmentId)).thenReturn(studentDTO);

        mockMvc.perform(get("/enrollments/student/enrollment/{enrollmentId}/student-details", enrollmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.major").value("Computer Science"));

        verify(enrollmentService, times(1)).getStudentDetailsFromEnrollment(enrollmentId);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testGetStudentDetailsFromEnrollment_NotFound() throws Exception {
        Long enrollmentId = 999L;

        when(enrollmentService.getStudentDetailsFromEnrollment(enrollmentId))
                .thenThrow(new RuntimeException("Enrollment not found"));

        mockMvc.perform(get("/enrollments/student/enrollment/{enrollmentId}/student-details", enrollmentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudentEmailInEnrollments_Success() throws Exception {
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";
        EnrollmentDTO updatedDTO = new EnrollmentDTO(newEmail, "Math 101", "2026-01-01", 8.5);

        when(enrollmentService.updateStudentEmailInEnrollments(oldEmail, newEmail)).thenReturn(updatedDTO);

        mockMvc.perform(put("/enrollments/student/update-email")
                        .with(csrf())
                        .param("oldEmail", oldEmail)
                        .param("newEmail", newEmail))
                .andExpect(status().isOk())
                .andExpect(content().string("Student email updated successfully in enrollments."));

        verify(enrollmentService, times(1)).updateStudentEmailInEnrollments(oldEmail, newEmail);
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testUpdateStudentEmailInEnrollments_Failure() throws Exception {
        String oldEmail = "old@example.com";
        String newEmail = "new@example.com";

        when(enrollmentService.updateStudentEmailInEnrollments(oldEmail, newEmail))
                .thenThrow(new RuntimeException("Student not found"));

        mockMvc.perform(put("/enrollments/student/update-email")
                        .with(csrf())
                        .param("oldEmail", oldEmail)
                        .param("newEmail", newEmail))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Student not found"));
    }
}