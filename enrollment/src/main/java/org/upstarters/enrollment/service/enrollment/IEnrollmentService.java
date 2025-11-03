package org.upstarters.enrollment.service.enrollment;

import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.dto.EnrollmentUpdateDTO;
import org.upstarters.enrollment.entity.Enrollment;

import java.util.List;

public interface IEnrollmentService {
    void enrollStudentInCourse(String studentName, String courseName);
    List<Enrollment> getAllEnrollments();
    EnrollmentDTO getEnrollment(Long enrollmentId);
    void updateEnrollment(Long enrollmentId, EnrollmentUpdateDTO enrollmentUpdateDTO);
    void deleteEnrollment(Long enrollmentId);
}
