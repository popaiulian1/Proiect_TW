package org.upstarters.enrollment.service.course;

import org.springframework.stereotype.Service;

@Service
public class CourseAPIService implements ICourseAPIService{

    @Override
    public Long getCourseIdByName(String courseName) {
        return switch (courseName) {
            case "Mathematics" -> Long.valueOf("1");
            case "Biology" -> Long.valueOf("3");
            case "History" -> Long.valueOf("2");
            case "Literature" -> Long.valueOf("4");
            default -> Long.valueOf("1");
        };
    }

    @Override
    public String getCourseNameById(Long courseId) {
        return "temporary_course";
    }
}
