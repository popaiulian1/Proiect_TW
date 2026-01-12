package org.upstarters.enrollment.service.student;

import org.upstarters.enrollment.dto.StudentDTO;
public interface IStudentAPIService {
    Long getStudentIdByName(String studentName);

    String getStudentNameById(Long studentId);

    StudentDTO getStudentByEmail(String email);
}
