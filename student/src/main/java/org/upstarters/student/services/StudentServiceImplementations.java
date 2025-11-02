package org.upstarters.student.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upstarters.student.dtos.StudentDTO;
import org.upstarters.student.entity.Student;
import org.upstarters.student.mapper.StudentMapper;
import org.upstarters.student.repository.StudentRepository;

@Service
public class StudentServiceImplementations implements IStudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentDTO addStudent(StudentDTO studentDTO) {
        if(studentRepository.findByEmail(studentDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Student with this email already exists!");
        }

        Student student = StudentMapper.toEntity(studentDTO);
        studentRepository.save(student);
        return StudentMapper.toDTO(student);
    }

}
