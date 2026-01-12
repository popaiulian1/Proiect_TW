package org.upstarters.enrollment.service.course;

import org.springframework.stereotype.Service;
import org.upstarters.enrollment.dto.CourseDTO;
import org.upstarters.enrollment.dto.FullCourseDTO;

/**
 * Service class for interacting with the Course microservice via Feign client.
 * Provides methods to retrieve course information including course IDs and names.
 * This service acts as a wrapper around the CourseFeignClient, handling exceptions
 * and providing a clean API for course-related operations.
 *
 * @author Popa Marian-Iulian
 * @version 1.0
 * @since 2026-01-12
 */
@Service
public class CourseAPIService implements ICourseAPIService{

    private final CourseFeignClient courseFeignClient;

    /**
     * Constructs a new CourseAPIService with the specified Feign client.
     *
     * @author Popa Marian-Iulian
     * @param courseFeignClientInstance the Feign client instance for course service communication
     * @since 1.0
     */
    CourseAPIService(CourseFeignClient courseFeignClientInstance) {
        this.courseFeignClient = courseFeignClientInstance;
    }

    /**
     * Retrieves the unique identifier of a course by its name.
     * Makes a remote call to the course microservice to fetch course details
     * and extracts the course ID from the response.
     *
     * @author Popa Marian-Iulian
     * @param courseName the name of the course to look up
     * @return the unique identifier of the course, or null if the course is not found
     * @throws RuntimeException if the remote service call fails or an error occurs during processing
     * @see CourseFeignClient#getCourseByTitle(String)
     * @see FullCourseDTO
     * @since 1.0
     */
    @Override
    public Long getCourseIdByName(String courseName) {
        try {
            FullCourseDTO response = courseFeignClient.getCourseByTitle(courseName);
            return response.getId();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get course id", e);
        }
    }

    /**
     * Retrieves the name of a course by its unique identifier.
     * Makes a remote call to the course microservice to fetch course details
     * and extracts the course title from the response.
     *
     * @author Popa Marian-Iulian
     * @param courseId the unique identifier of the course
     * @return the name (title) of the course, or null if the course is not found
     * @throws RuntimeException if the remote service call fails or an error occurs during processing
     * @see CourseFeignClient#getCourseById(Long)
     * @see CourseDTO
     * @since 1.0
     */
    @Override
    public String getCourseNameById(Long courseId) {
        try {
            CourseDTO response = courseFeignClient.getCourseById(courseId);
            return response.getTitle();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get course name", e);
        }
    }
}
