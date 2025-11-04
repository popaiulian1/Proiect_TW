package org.upstarters.enrollment.service.student;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class StudentAPIService implements IStudentAPIService {

    @Override
    public Long getStudentIdByName(String studentName) {
        return switch (studentName) {
            case "Jane Doe" -> Long.valueOf("2");
            case "John Doe" -> Long.valueOf("1");
            default -> Long.valueOf("1");
        };
    }

    @Override
    public String getStudentNameById(Long studentId) {
        return "placeholder_student";
    }
}
