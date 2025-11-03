package org.upstarters.enrollment.service.student;

public interface IStudentAPIService {
    Long getStudentIdByName(String studentName);

    String getStudentNameById(Long studentId);
}
