package org.upstarters.student.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upstarters.student.dtos.ExternalCourseDTO;
import org.upstarters.student.dtos.StudentDTO;
import org.upstarters.student.entity.Student;
import org.upstarters.student.mapper.StudentMapper;
import org.upstarters.student.repository.StudentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImplementations implements IStudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CoursesFeignClient coursesFeignClient;

    @Override
    public StudentDTO addStudent(StudentDTO studentDTO) {
        if(studentRepository.findByEmail(studentDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Student with this email already exists!");
        }

        Student student = StudentMapper.toEntity(studentDTO);
        studentRepository.save(student);
        return StudentMapper.toDTO(student);
    }

    @Override
    public StudentDTO fetchStudent(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));
        return StudentMapper.toDTO(student);
    }

    @Override
    public Long countStudents() {
        return studentRepository.count();
    }

    @Override
    public List<StudentDTO> fetchStudentsByMajor(String major) {
        List<Student> student = studentRepository.findAllByMajor(major);

        return student
                .stream()
                .map(StudentMapper::toDTO)
                .toList();
    }

    @Override
    public List<StudentDTO> fetchStudents() {
        List<Student> students = studentRepository.findAll();

        return students
                .stream()
                .map(StudentMapper::toDTO)
                .toList();
    }

    @Override
    public StudentDTO updateStudent(String email, StudentDTO studentDTO) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));

        student.setFirstname(studentDTO.getFirstName());
        student.setLastname(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        student.setMajor(studentDTO.getMajor());

        studentRepository.save(student);
        return StudentMapper.toDTO(student);
    }

    @Override
    public boolean deleteStudent(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));

        studentRepository.delete(student);
        return true;
    }

    @Override
    public long fetchStudentIdFromEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));

        return student.getId();
    }

    @Override
    public List<ExternalCourseDTO> getRecommendedCourses(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));

        String major = student.getMajor();

        List<ExternalCourseDTO> recommendedCourses = coursesFeignClient.fetchCoursesByDepartment(major);

        return recommendedCourses;
    }

    @Override
    public StudentDTO updateMajorFromCourse(String email, String courseTitle) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));

        ExternalCourseDTO course = coursesFeignClient.fetchCourseByTitle(courseTitle);

        if (course == null) {
            throw new RuntimeException("Course with title " + courseTitle + " not found in Course Service!");
        }

        student.setMajor(course.getDepartment());
        studentRepository.save(student);

        return StudentMapper.toDTO(student);
    }
}
