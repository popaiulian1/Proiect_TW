package org.upstarters.enrollment.service.enrollment;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

/**
 * Service class for managing student enrollments in courses.
 * Provides business logic for enrollment operations including creation, retrieval,
 * updates, and deletion of enrollments.
 *
 * @author Popa Marian-Iulian
 * @version 1.0
 * @since 2026-01-12
 */
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

    /**
     * Enrolls a student in a specified course.
     * Creates a new enrollment record with the current date and initial grade of 0.0.
     * Validates that both the student and course exist before creating the enrollment.
     * 
     * @author Popa Marian-Iulian
     *
     * @param studentEmail the email address of the student to enroll
     * @param courseName the name of the course in which to enroll the student
     * @throws IllegalArgumentException if the student with the given email is not found
     * @throws IllegalArgumentException if the course with the given name is not found
     * @throws IllegalStateException if the student is already enrolled in the course
     * @see StudentAPIService#getStudentByEmail(String)
     * @see CourseAPIService#getCourseIdByName(String)
     * @since 1.0
     */
    @Override
    public void enrollStudentInCourse(String studentEmail, String courseName) {
        StudentDTO student = studentAPIService.getStudentByEmail(studentEmail);
        if (student == null) {
            throw new IllegalArgumentException("Student not found with email: " + studentEmail);
        }

        Long courseId = courseAPIService.getCourseIdByName(courseName);
        if (courseId == null) {
            throw new IllegalArgumentException("Course not found with name: " + courseName);
        }

        if (enrollmentRepository.existsByStudentEmailAndCourseId(studentEmail, courseId)) {
            throw new IllegalStateException("Student is already enrolled in the course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentEmail(studentEmail);
        enrollment.setCourseId(courseId);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setGrade(0.0);

        enrollmentRepository.save(enrollment);
    }

    /**
     * Retrieves all enrollments from the database.
     * Returns all enrollment records without any filtering.
     * 
     * @author Popa Marian-Iulian
     *
     * @return a list of all enrollments; empty list if no enrollments exist
     * @see Enrollment
     * @since 1.0
     */
    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    /**
     * Retrieves a specific enrollment by its unique identifier.
     * Maps the enrollment entity to a DTO before returning.
     * 
     * @author Popa Marian-Iulian
     *
     * @param enrollmentId the unique identifier of the enrollment
     * @return the enrollment data transfer object
     * @throws EntityNotFoundException if no enrollment exists with the given ID
     * @see EnrollmentDTO
     * @see EnrollmentMapper#toDto(Enrollment)
     * @since 1.0
     */
    @Override
    public EnrollmentDTO getEnrollment(Long enrollmentId) {
        return enrollmentRepository.findById(enrollmentId).map(enrollmentMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + enrollmentId));
    }

    /**
     * Updates an existing enrollment with new course or grade information.
     * Only updates fields that are provided in the update DTO (non-null values).
     * If a course name is provided, validates that the course exists.
     * 
     * @author Popa Marian-Iulian
     *
     * @param enrollmentId the unique identifier of the enrollment to update
     * @param enrollmentUpdateDTO the data transfer object containing update information
     * @throws EntityNotFoundException if no enrollment exists with the given ID
     * @throws IllegalArgumentException if a course name is provided but the course is not found
     * @see EnrollmentUpdateDTO
     * @see CourseAPIService#getCourseIdByName(String)
     * @since 1.0
     */
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

    /**
     * Deletes an enrollment from the database.
     * Permanently removes the enrollment record.
     * 
     * @author Popa Marian-Iulian
     *
     * @param enrollmentId the unique identifier of the enrollment to delete
     * @throws EntityNotFoundException if no enrollment exists with the given ID
     * @since 1.0
     */
    @Override
    public void deleteEnrollment(Long enrollmentId) {
        if (enrollmentRepository.existsById(enrollmentId)) {
            enrollmentRepository.deleteById(enrollmentId);
        } else {
            throw new EntityNotFoundException("Enrollment not found with id: " + enrollmentId);
        }
    }

    /**
     * Retrieves all students enrolled in a specific course.
     * Returns enrollment information for all students in the specified course.
     *
     * @author Popa Marian-Iulian
     * 
     * @param course the name of the course
     * @return a list of enrollment DTOs for students in the course; empty list if none found
     * @throws EntityNotFoundException if the course with the given name is not found
     * @see EnrollmentDTO
     * @see CourseAPIService#getCourseIdByName(String)
     * @since 1.0
     */
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

    /**
     * Retrieves all enrollments with failing grades.
     * A failing grade is defined as any grade less than or equal to 5.0.
     *
     * @author Popa Marian-Iulian
     * 
     * @return a list of enrollment DTOs for students with failing grades; empty list if none found
     * @see EnrollmentDTO
     * @since 1.0
     */
    @Override
    public List<EnrollmentDTO> failingStudents() {
        List<Enrollment> enrollments = enrollmentRepository.findAllByGradeLessThanEqual(5.0)
                .orElse(List.of());

        return enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the top 5 students in a specific course based on grades.
     * Students are sorted in descending order by grade, with the highest grades first.
     * If the course has fewer than 5 students, all enrolled students are returned.
     *
     * @author Popa Marian-Iulian
     * 
     * @param course the name of the course
     * @return a list of up to 5 enrollment DTOs with the highest grades; empty list if none found
     * @throws EntityNotFoundException if the course with the given name is not found
     * @see EnrollmentDTO
     * @see CourseAPIService#getCourseIdByName(String)
     * @since 1.0
     */
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

    /**
     * Retrieves all enrollments for a specific student.
     * Returns all courses in which the student is enrolled.
     *
     * @author Popa Marian-Iulian
     * 
     * @param studentEmail the email address of the student
     * @return a list of enrollment DTOs for the student; empty list if none found
     * @throws EntityNotFoundException if the student with the given email is not found
     * @see EnrollmentDTO
     * @see StudentAPIService#getStudentByEmail(String)
     * @since 1.0
     */
    @Override
    public List<EnrollmentDTO> getEnrollmentsByStudent(String studentEmail) {
        StudentDTO student = studentAPIService.getStudentByEmail(studentEmail);
        if (student == null) {
            throw new EntityNotFoundException("Student not found with email: " + studentEmail);
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentEmail(studentEmail)
                .orElse(List.of());

        return enrollments.stream()
                .map(enrollmentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves detailed student information from an enrollment record.
     * Fetches the complete student profile associated with the given enrollment.
     *
     * @author Popa Marian-Iulian
     * 
     * @param enrollmentId the unique identifier of the enrollment
     * @return the student data transfer object with complete profile information
     * @throws EntityNotFoundException if no enrollment exists with the given ID
     * @throws EntityNotFoundException if the student associated with the enrollment is not found
     * @see StudentDTO
     * @see StudentAPIService#getStudentByEmail(String)
     * @since 1.0
     */
    @Override
    public StudentDTO getStudentDetailsFromEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + enrollmentId));

        String studentEmail = enrollment.getStudentEmail();
        StudentDTO studentDetails = studentAPIService.getStudentByEmail(studentEmail);
        
        if (studentDetails == null) {
            throw new EntityNotFoundException("Student not found with email: " + studentEmail);
        }

        return studentDetails;
    }

    /**
     * Updates a student's email address across all enrollment records.
     * This method performs a cascading update, first updating the student's email
     * in the student service, then updating all associated enrollment records.
     * Returns the enrollment DTO of the first updated enrollment.
     *
     * @author Popa Marian-Iulian
     * 
     * @param oldEmail the current email address of the student
     * @param newEmail the new email address to update to
     * @return an enrollment DTO representing one of the updated enrollments, or null if none found
     * @throws EntityNotFoundException if the student with the old email is not found
     * @throws EntityNotFoundException if no enrollments exist for the student
     * @throws RuntimeException if the student email update in the student service fails
     * @see StudentDTO
     * @see EnrollmentDTO
     * @see StudentAPIService#getStudentByEmail(String)
     * @see StudentAPIService#updateStudentInfo(String, StudentDTO)
     * @since 1.0
     */
    @Override
    public EnrollmentDTO updateStudentEmailInEnrollments(String oldEmail, String newEmail) {

        StudentDTO student = studentAPIService.getStudentByEmail(oldEmail);

        if (student == null) {
            throw new EntityNotFoundException("Student not found with email: " + oldEmail);
        }
        
        student.setEmail(newEmail);

        StudentDTO response = studentAPIService.updateStudentInfo(oldEmail, student);

        if (response == null) {
            throw new RuntimeException("Failed to update student email in student service from " + oldEmail + " to " + newEmail);
        } else {
            List<Enrollment> enrollments = enrollmentRepository.findAllByStudentEmail(oldEmail)
                .orElse(List.of());

            if (enrollments.isEmpty()) {
                throw new EntityNotFoundException("No enrollments found for student with email: " + oldEmail);
            }

            for (Enrollment enrollment : enrollments) {
                enrollment.setStudentEmail(newEmail);
            }

            enrollmentRepository.saveAll(enrollments);

            return enrollments.stream()
                .findFirst()
                .map(enrollmentMapper::toDto)
                .orElse(null);
        }
    }
}