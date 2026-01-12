package org.upstarters.enrollment.service.student;

import org.springframework.stereotype.Service;
import org.upstarters.enrollment.dto.FullCourseDTO;
import org.upstarters.enrollment.dto.StudentDTO;

@Service
public class StudentAPIService implements IStudentAPIService {

    private final StudentFeignClient studentFeignClient;

    StudentAPIService(StudentFeignClient studentFeignClientInstance) {
        this.studentFeignClient = studentFeignClientInstance;
    }

    @Override
    public Long getStudentIdByName(String studentName) {
        // TODO: Implement this method to call the Student Service via Feign client
        return switch (studentName) {
            case "Jane Doe" -> Long.valueOf("2");
            case "John Doe" -> Long.valueOf("1");
            default -> Long.valueOf("1");
        };
    }

    @Override
    public String getStudentNameById(Long studentId) {
        // TODO: Implement this method to call the Student Service via Feign client
        return "placeholder_student";
    }

    @Override
    public StudentDTO getStudentByEmail(String email) {
        try {
            StudentDTO response = studentFeignClient.getStudentByEmail(email);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get student by email: " + email, e);
        }
    }
}
