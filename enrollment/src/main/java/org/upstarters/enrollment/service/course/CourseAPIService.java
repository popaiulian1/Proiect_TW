package org.upstarters.enrollment.service.course;

import org.springframework.stereotype.Service;
import org.upstarters.enrollment.dto.FullCourseDTO;
import org.upstarters.enrollment.dto.CourseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CourseAPIService implements ICourseAPIService{

    private final CourseFeignClient courseFeignClient;

    CourseAPIService(CourseFeignClient courseFeignClientInstance) {
        this.courseFeignClient = courseFeignClientInstance;
    }

    @Override
    public Long getCourseIdByName(String courseName) {
        try {
            FullCourseDTO response = courseFeignClient.getCourseByTitle(courseName);
            return response.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get course id", e);
        }
    }

    @Override
    public String getCourseNameById(Long courseId) {
        try {
            CourseDTO response = courseFeignClient.getCourseById(courseId);
            return response.getTitle();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get course name", e);
        }
    }
}
