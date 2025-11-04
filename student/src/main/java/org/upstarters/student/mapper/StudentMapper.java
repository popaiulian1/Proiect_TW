package org.upstarters.student.mapper;

import org.upstarters.student.dtos.StudentDTO;
import org.upstarters.student.entity.Student;

public class StudentMapper {

    public static Student toEntity(StudentDTO studentDTO) {
        return new Student(
                studentDTO.getFirstName(),
                studentDTO.getLastName(),
                studentDTO.getEmail(),
                studentDTO.getMajor()
        );
    }

    public static StudentDTO toDTO(Student student) {
        return new StudentDTO(
                student.getFirstname(),
                student.getLastname(),
                student.getEmail(),
                student.getMajor()
        );
    }
}
