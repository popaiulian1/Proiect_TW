package org.upstarters.enrollment.service.student;

import org.upstarters.enrollment.dto.StudentDTO;
public interface IStudentAPIService {
    String getStudentEmailById(Long studentId);

    StudentDTO getStudentByEmail(String email);

    StudentDTO updateStudentInfo(String oldEmail, StudentDTO studentDTO);
}
