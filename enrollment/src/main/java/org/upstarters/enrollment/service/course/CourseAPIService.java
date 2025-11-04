package org.upstarters.enrollment.service.course;

import org.springframework.stereotype.Service;

@Service
public class CourseAPIService implements ICourseAPIService{

    @Override
    public Long getCourseIdByName(String courseName) {
        return switch (courseName) {
            case "Mathematics" -> 1L;
            case "Biology" -> 3L;
            case "History" -> 2L;
            case "Literature" -> 4L;
            default -> null;
        };
    }

    @Override
    public String getCourseNameById(Long courseId) {
        return switch (courseId.intValue()) {
            case 1 -> "Mathematics";
            case 2 -> "History";
            case 3 -> "Biology";
            case 4 -> "Literature";
            default -> "Unknown";
        };
    }
}
