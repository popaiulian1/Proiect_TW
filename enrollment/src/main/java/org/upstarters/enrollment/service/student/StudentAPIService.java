package org.upstarters.enrollment.service.student;

import org.springframework.stereotype.Service;
import org.upstarters.enrollment.dto.StudentDTO;

import feign.FeignException;

@Service
public class StudentAPIService implements IStudentAPIService {

    private final StudentFeignClient studentFeignClient;

    StudentAPIService(StudentFeignClient studentFeignClientInstance) {
        this.studentFeignClient = studentFeignClientInstance;
    }

    @Override
    public String getStudentEmailById(Long studentId) {
        try {
            StudentDTO student = studentFeignClient.getStudentById(studentId);
            return student.getEmail();
        } catch (FeignException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get student email by ID: " + studentId, e);
        }
    }

    @Override
    public StudentDTO getStudentByEmail(String email) {
        try {
            StudentDTO response = studentFeignClient.getStudentByEmail(email);
            return response;
        } catch (FeignException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get student by email: " + email, e);
        }
    }

    @Override
    public StudentDTO updateStudentInfo(String oldEmail, StudentDTO studentDTO) {
        try {
            StudentDTO response = studentFeignClient.updateStudent(oldEmail, studentDTO);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update student with email: " + studentDTO.getEmail(), e);
        }
    }
}
