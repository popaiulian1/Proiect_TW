package org.upstarters.enrollment.service.enrollment;

import org.springframework.stereotype.Service;
import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.dto.EnrollmentUpdateDTO;
import org.upstarters.enrollment.dto.StudentDTO;
import org.upstarters.enrollment.entity.Enrollment;
import org.upstarters.enrollment.mapper.EnrollmentMapper;
import org.upstarters.enrollment.repository.EnrollmentRepository;
import org.upstarters.enrollment.service.course.CourseAPIService;
import org.upstarters.enrollment.service.student.StudentAPIService;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.upstarters.enrollment.entity.Student;

@Service
public class EnrollmentService implements IEnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseAPIService courseAPIService;
    private final StudentAPIService studentAPIService;
    private final EnrollmentMapper enrollmentMapper;

    EnrollmentService(EnrollmentRepository enrollmentRepository,
                      CourseAPIService courseAPIService,
                      StudentAPIService studentAPIService,
                      EnrollmentMapper enrollmentMapper) {
        this.enrollmentMapper = enrollmentMapper;
        this.enrollmentRepository = enrollmentRepository;
        this.courseAPIService =  courseAPIService;
        this.studentAPIService = studentAPIService;
    }

    @Override
    public void enrollStudentInCourse(String studentName, String courseName) {
        Long studentId = studentAPIService.getStudentIdByName(studentName);
        Long courseId = courseAPIService.getCourseIdByName(courseName);

        if (studentId == null) {
            throw new IllegalArgumentException("Student not found with name: " + studentName);
        }
        if (courseId == null) {
            throw new IllegalArgumentException("Course not found with name: " + courseName);
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Student is already enrolled in the course");
        }

        Enrollment enrollment = new Enrollment();

        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setGrade(0.0);

        enrollmentRepository.save(enrollment);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public EnrollmentDTO getEnrollment(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId).map(enrollmentMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + enrollmentId));
    }

    @Override
    public void updateEnrollment(Long enrollmentId, EnrollmentUpdateDTO enrollmentUpdateDTO) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + enrollmentId));

        if (enrollmentUpdateDTO.getCourseName() != null && !enrollmentUpdateDTO.getCourseName().isEmpty()) {
            Long courseId = courseAPIService.getCourseIdByName(enrollmentUpdateDTO.getCourseName());
            if (courseId == null) {
                throw new IllegalArgumentException("Course not found with name: " + enrollmentUpdateDTO.getCourseName());
            }
            enrollment.setCourseId(courseId);
        }

        if (enrollmentUpdateDTO.getGrade() != null) {
            enrollment.setGrade(enrollmentUpdateDTO.getGrade());
        }

        enrollmentRepository.save(enrollment);
    }

    @Override
    public void deleteEnrollment(Long enrollmentId) {
        if (enrollmentRepository.existsById(enrollmentId)) {
            enrollmentRepository.deleteById(enrollmentId);
        } else {
            throw new EntityNotFoundException("Enrollment not found with id: " + enrollmentId);
        }
    }

    @Override
    public List<EnrollmentDTO> studentsFilteredByCourse(String course) {
        Long courseId = courseAPIService.getCourseIdByName(course);

        if (courseId == null){
            throw new EntityNotFoundException("Course not found with name: " + course);
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId)
                .orElse(List.of());

        return enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentDTO> failingStudents() {
        List<Enrollment> enrollments = enrollmentRepository.findAllByGradeLessThanEqual(5.0)
                .orElse(List.of());

        return enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentDTO> getTop5InCourse(String course) {

        Long courseId = courseAPIService.getCourseIdByName(course);

        if (courseId == null) {
            throw new EntityNotFoundException("Course not found with name: " + course);
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId).orElse(List.of());

        return enrollments.stream().map(enrollmentMapper::toDto)
                .sorted(Comparator.comparing(EnrollmentDTO::getGrade).reversed())
                .limit(5)
                .collect(Collectors.toList());

    }

    @Override
    public List<EnrollmentDTO> getEnrollmentsByStudent(String student) {
        Long studentId = studentAPIService.getStudentIdByName(student);

        if (studentId == null) {
            throw new EntityNotFoundException("Student not found with name: " + student);
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(studentId)
                .orElse(List.of());

        return enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDTO getStudentDetailsFromEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + enrollmentId));

        Long studentId = enrollment.getStudentId();
        String studentName = studentAPIService.getStudentNameById(studentId);

        if (studentName == null) {
            throw new EntityNotFoundException("Student not found with id: " + studentId);
        }

        StudentDTO studentDetails = studentAPIService.getStudentByEmail(studentName);
        return studentDetails;
    }
}
