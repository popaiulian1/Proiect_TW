package org.upstarters.enrollment.service.student;

import org.springframework.stereotype.Service;
import org.upstarters.enrollment.dto.StudentDTO;

import feign.FeignException;

/**
 * Service class for interacting with the Student microservice via Feign client.
 * Provides methods to retrieve and update student information including student lookups
 * by ID or email, and student profile updates. This service acts as a wrapper around
 * the StudentFeignClient, handling exceptions and providing a clean API for student-related operations.
 *
 * @author Popa Marian-Iulian
 * @version 1.0
 * @since 2026-01-12
 */
@Service
public class StudentAPIService implements IStudentAPIService {

    private final StudentFeignClient studentFeignClient;

    /**
     * Constructs a new StudentAPIService with the specified Feign client.
     *
     * @author Popa Marian-Iulian
     * @param studentFeignClientInstance the Feign client instance for student service communication
     * @since 1.0
     */
    StudentAPIService(StudentFeignClient studentFeignClientInstance) {
        this.studentFeignClient = studentFeignClientInstance;
    }

    /**
     * Retrieves a student's email address by their unique identifier.
     * Makes a remote call to the student microservice to fetch student details
     * and extracts the email address from the response.
     * Returns null if the student is not found.
     *
     * @author Popa Marian-Iulian
     * @param studentId the unique identifier of the student
     * @return the email address of the student, or null if the student is not found
     * @throws RuntimeException if the remote service call fails or an error occurs during processing
     * @see StudentFeignClient#getStudentById(Long)
     * @see StudentDTO
     * @since 1.0
     */
    @Override
    public String getStudentEmailById(Long studentId) {
        try {
            StudentDTO student = studentFeignClient.getStudentById(studentId);
            return student.getEmail();
        } catch (FeignException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get student email by ID: " + studentId, e);
        }
    }

    /**
     * Retrieves complete student information by email address.
     * Makes a remote call to the student microservice to fetch the full student profile.
     * Returns null if no student is found with the specified email address.
     *
     * @author Popa Marian-Iulian
     * @param email the email address of the student to look up
     * @return the complete student data transfer object, or null if the student is not found
     * @throws RuntimeException if the remote service call fails or an error occurs during processing
     * @see StudentFeignClient#getStudentByEmail(String)
     * @see StudentDTO
     * @since 1.0
     */
    @Override
    public StudentDTO getStudentByEmail(String email) {
        try {
            StudentDTO response = studentFeignClient.getStudentByEmail(email);
            return response;
        } catch (FeignException.NotFound e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get student by email: " + email, e);
        }
    }

    /**
     * Updates student information in the student microservice.
     * Makes a remote call to update the student's profile with the provided information.
     * The old email is used to identify the student record to update, while the studentDTO
     * contains the new information including potentially a new email address.
     *
     * @author Popa Marian-Iulian
     * @param oldEmail the current email address of the student to identify the record
     * @param studentDTO the data transfer object containing the updated student information
     * @return the updated student data transfer object from the service, or null if update fails
     * @throws RuntimeException if the remote service call fails or an error occurs during processing
     * @see StudentFeignClient#updateStudent(String, StudentDTO)
     * @see StudentDTO
     * @since 1.0
     */
    @Override
    public StudentDTO updateStudentInfo(String oldEmail, StudentDTO studentDTO) {
        try {
            StudentDTO response = studentFeignClient.updateStudent(oldEmail, studentDTO);
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update student with email: " + studentDTO.getEmail(), e);
        }
    }
}
