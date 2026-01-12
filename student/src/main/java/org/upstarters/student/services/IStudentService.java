package org.upstarters.student.services;

import org.upstarters.student.dtos.ExternalCourseDTO;
import org.upstarters.student.dtos.StudentDTO;

import java.util.List;

public interface IStudentService {
    StudentDTO addStudent(StudentDTO studentDTO);
    StudentDTO fetchStudent(String email);
    Long countStudents();
    List<StudentDTO> fetchStudentsByMajor(String major);
    List<StudentDTO> fetchStudents();
    StudentDTO updateStudent(String email, StudentDTO studentDTO);
    boolean deleteStudent(String email);
    Long fetchStudentIdFromEmail(String email);
    String fetchStudentEmailFromId(Long id);

    List<ExternalCourseDTO> getRecommendedCourses(String email);
    StudentDTO updateMajorFromCourse(String email, String courseTitle);
}
