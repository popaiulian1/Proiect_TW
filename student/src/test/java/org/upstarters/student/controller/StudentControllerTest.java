package org.upstarters.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.upstarters.student.controller.StudentController;
import org.upstarters.student.dtos.ExternalCourseDTO;
import org.upstarters.student.dtos.StudentDTO;
import org.upstarters.student.services.IStudentService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IStudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentDTO studentDTO;
    private ExternalCourseDTO externalCourseDTO;

    @BeforeEach
    void setUp() {
        studentDTO = new StudentDTO("John", "Doe", "john.doe@example.com", "Computer Science");
        externalCourseDTO = new ExternalCourseDTO("Intro to Java", "CS", 100);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStudent_ShouldReturnCreated() throws Exception {
        when(studentService.addStudent(any(StudentDTO.class))).thenReturn(studentDTO);

        mockMvc.perform(post("/students/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void fetchStudent_ShouldReturnStudent() throws Exception {
        when(studentService.fetchStudent("john.doe@example.com")).thenReturn(studentDTO);

        mockMvc.perform(get("/students/getByEmail/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void fetchStudents_ShouldReturnList() throws Exception {
        when(studentService.fetchStudents()).thenReturn(Collections.singletonList(studentDTO));

        mockMvc.perform(get("/students/getStudents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void countStudents_ShouldReturnCount() throws Exception {
        when(studentService.countStudents()).thenReturn(10L);

        mockMvc.perform(get("/students/countStudents"))
                .andExpect(status().isFound())
                .andExpect(content().string("10"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void fetchStudentByMajor_ShouldReturnList() throws Exception {
        when(studentService.fetchStudentsByMajor("Computer Science")).thenReturn(Arrays.asList(studentDTO));

        mockMvc.perform(get("/students/getStudentsByMajor/{major}", "Computer Science"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].major").value("Computer Science"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStudent_ShouldReturnUpdatedStudent() throws Exception {
        StudentDTO updatedDto = new StudentDTO("John", "Smith", "john.doe@example.com", "Mathematics");
        when(studentService.updateStudent(eq("john.doe@example.com"), any(StudentDTO.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/students/update/{email}", "john.doe@example.com")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.major").value("Mathematics"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStudent_ShouldReturnTrue() throws Exception {
        when(studentService.deleteStudent("john.doe@example.com")).thenReturn(true);

        mockMvc.perform(delete("/students/delete/{email}", "john.doe@example.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void fetchStudentIdFromEmail_ShouldReturnId() throws Exception {
        when(studentService.fetchStudentIdFromEmail("john.doe@example.com")).thenReturn(123L);

        mockMvc.perform(get("/students/getId/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void fetchStudentEmailFromId_ShouldReturnEmail() throws Exception {
        when(studentService.fetchStudentEmailFromId(123L)).thenReturn("john.doe@example.com");

        mockMvc.perform(get("/students/getEmail/{id}", 123L))
                .andExpect(status().isOk())
                .andExpect(content().string("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getRecommendedCourses_ShouldReturnCourses() throws Exception {
        List<ExternalCourseDTO> courses = Arrays.asList(externalCourseDTO);
        when(studentService.getRecommendedCourses("john.doe@example.com")).thenReturn(courses);

        mockMvc.perform(get("/students/recommendations/{email}", "john.doe@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Intro to Java"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMajorFromCourse_ShouldReturnUpdatedStudent() throws Exception {
        StudentDTO updatedStudent = new StudentDTO("John", "Doe", "john.doe@example.com", "Physics");
        when(studentService.updateMajorFromCourse("john.doe@example.com", "Physics 101")).thenReturn(updatedStudent);

        mockMvc.perform(put("/students/update-major-from-course/{email}/{courseTitle}",
                        "john.doe@example.com", "Physics 101")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.major").value("Physics"));
    }
}