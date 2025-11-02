package org.upstarters.student.services;

import org.upstarters.student.dtos.StudentDTO;

public interface IStudentService {
    StudentDTO addStudent(StudentDTO studentDTO);
    StudentDTO fetchStudent(String email);
    StudentDTO updateStudent(String email, StudentDTO studentDTO);
    boolean deleteStudent(String email);
}
