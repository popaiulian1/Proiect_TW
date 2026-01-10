package org.upstarters.student.services;

import org.upstarters.student.dtos.StudentDTO;
import org.upstarters.student.enums.Major;

import java.util.List;

public interface IStudentService {
    StudentDTO addStudent(StudentDTO studentDTO);
    StudentDTO fetchStudent(String email);
    Long countStudents();
    List<StudentDTO> fetchStudentsByMajor(String major);
    List<StudentDTO> fetchStudents();
    StudentDTO updateStudent(String email, StudentDTO studentDTO);
    boolean deleteStudent(String email);
    long fetchStudentIdFromEmail(String email);
}
