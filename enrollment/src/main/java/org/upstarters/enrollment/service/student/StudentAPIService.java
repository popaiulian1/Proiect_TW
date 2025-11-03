package org.upstarters.enrollment.service.student;

import org.springframework.stereotype.Service;

@Service
public class StudentAPIService implements IStudentAPIService {

    @Override
    public Long getStudentIdByName(String studentName) {
        return Long.valueOf("1");
    }

    @Override
    public String getStudentNameById(Long studentId) {
        return "temporary_student";
    }
}
