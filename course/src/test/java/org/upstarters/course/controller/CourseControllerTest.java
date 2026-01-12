package org.upstarters.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.upstarters.course.config.SecurityConfig;
import org.upstarters.course.dto.CourseDto;
import org.upstarters.course.service.CourseService;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@Import(SecurityConfig.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private JwtDecoder jwtDecoder; // Mock obligatoriu pentru contextul de securitate

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addCourse_AdminAccess_ReturnsCreated() throws Exception {
        CourseDto dto = new CourseDto();
        dto.setTitle("Java");
        when(courseService.addCourse(any())).thenReturn(dto);

        mockMvc.perform(post("/courses/addCourse")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void addCourse_StudentAccess_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/courses/addCourse")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getCourseById_Success() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(Optional.of(new CourseDto()));
        mockMvc.perform(get("/courses/getCourseById/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCourses_AdminAccess_ReturnsOk() throws Exception {
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/courses/getAllCourses"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCourse_Success() throws Exception {
        when(courseService.updateCourse(any())).thenReturn(true);
        mockMvc.perform(put("/courses/updateCourse")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CourseDto())))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateCapacity_Success() throws Exception {
        when(courseService.updateCapacityOfCourse(anyInt(), anyString())).thenReturn(true);
        mockMvc.perform(patch("/courses/updateCapacity/Java")
                        .param("capacity", "50")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void syncCapacity_Success() throws Exception {
        when(courseService.updateCourseCapacityBasedOnStudentCount(anyString(), anyString())).thenReturn(true);
        mockMvc.perform(put("/courses/syncCapacityWithStudents/Java")
                        .param("department", "IT")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCourse_Success() throws Exception {
        when(courseService.deleteCourse("Java")).thenReturn(true);
        mockMvc.perform(delete("/courses/deleteCourse/Java")
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}