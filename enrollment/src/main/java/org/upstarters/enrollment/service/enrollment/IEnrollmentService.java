package org.upstarters.enrollment.service.enrollment;

import java.util.List;

import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.dto.EnrollmentUpdateDTO;
import org.upstarters.enrollment.dto.StudentDTO;
import org.upstarters.enrollment.entity.Enrollment;

public interface IEnrollmentService {
    void enrollStudentInCourse(String studentName, String courseName);
    List<Enrollment> getAllEnrollments();
    EnrollmentDTO getEnrollment(Long enrollmentId);
    void updateEnrollment(Long enrollmentId, EnrollmentUpdateDTO enrollmentUpdateDTO);
    void deleteEnrollment(Long enrollmentId);
    List<EnrollmentDTO> studentsFilteredByCourse(String course);
    List<EnrollmentDTO> failingStudents();
    List<EnrollmentDTO> getTop5InCourse(String course);
    List<EnrollmentDTO> getEnrollmentsByStudent(String student);
    StudentDTO getStudentDetailsFromEnrollment(Long enrollmentId);
    EnrollmentDTO updateStudentEmailInEnrollments(String oldEmail, String newEmail);
}
