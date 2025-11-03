package org.upstarters.enrollment.service.course;

public interface ICourseAPIService {
    Long getCourseIdByName(String courseName);

    String getCourseNameById(Long courseId);
}
