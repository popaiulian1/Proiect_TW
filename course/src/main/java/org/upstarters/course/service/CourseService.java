package org.upstarters.course.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upstarters.course.dto.CourseDto;
import org.upstarters.course.dto.ExternalStudentDTO;
import org.upstarters.course.dto.FullCourseDto;
import org.upstarters.course.entity.Course;
import org.upstarters.course.mapper.CourseMapper;
import org.upstarters.course.repository.CourseRepository;
import org.upstarters.course.service.interfaces.ICourseService;
import org.upstarters.course.service.interfaces.StudentsFeignClient;

import java.util.List;
import java.util.Optional;

/**
 * Service class that handles all business logic related to Course management.
 * <p>
 * This service provides CRUD operations for courses and integrates with external
 * student services via Feign client for cross-service communication.
 *
 * @author Petre Flaviu-Mihai
 * @version 1.0
 * @see ICourseService
 * @see CourseRepository
 * @see StudentsFeignClient
 */
@Service
public class CourseService implements ICourseService {

    private final CourseRepository courseRepository;
    private final StudentsFeignClient studentsFeignClient;

    /**
     * Constructs a new CourseService with the required dependencies.
     *
     * @param studentsFeignClient the Feign client for communicating with the Students microservice
     * @param courseRepository    the repository for Course entity persistence operations
     * @author Petre Flaviu-Mihai
     */
    @Autowired
    public CourseService(StudentsFeignClient studentsFeignClient, CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
        this.studentsFeignClient = studentsFeignClient;
    }

    /**
     * Creates a new course in the system.
     * <p>
     * This method converts the provided DTO to an entity, persists it to the database,
     * and returns the saved course as a DTO.
     *
     * @param courseDto the data transfer object containing course information to be created
     * @return the created course as a {@link CourseDto} with persisted data
     * @author Petre Flaviu-Mihai
     * @see CourseMapper#toEntity(CourseDto)
     * @see CourseMapper#toDto(Course)
     */
    @Override
    public CourseDto addCourse(CourseDto courseDto) {
        Course course = CourseMapper.toEntity(courseDto);
        courseRepository.save(course);
        return CourseMapper.toDto(course);
    }

    /**
     * Retrieves a course by its unique identifier.
     * <p>
     * This method queries the database for a course with the specified ID
     * and returns it wrapped in an Optional to handle the case where the course
     * may not exist.
     *
     * @param id the unique identifier of the course to retrieve
     * @return an {@link Optional} containing the {@link CourseDto} if found,
     *         or an empty Optional if no course exists with the given ID
     * @author Petre Flaviu-Mihai
     */
    @Override
    public Optional<CourseDto> getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(CourseMapper::toDto);
    }

    /**
     * Retrieves all courses from the database.
     * <p>
     * This method fetches every course entity from the repository and converts
     * them to DTOs for presentation layer consumption.
     *
     * @return a {@link List} of {@link CourseDto} representing all courses in the system;
     *         returns an empty list if no courses exist
     * @author Petre Flaviu-Mihai
     */
    @Override
    public List<CourseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a course with full details by its title.
     * <p>
     * Unlike {@link #getCourseById(Long)}, this method returns a {@link FullCourseDto}
     * which includes the course ID along with other details.
     *
     * @param title the exact title of the course to search for
     * @return an {@link Optional} containing the {@link FullCourseDto} if found,
     *         or an empty Optional if no course exists with the given title
     * @author Petre Flaviu-Mihai
     * @see FullCourseDto
     */
    @Override
    public Optional<FullCourseDto> getFullCourseByTitle(String title) {
        return Optional.ofNullable(courseRepository.findByTitle(title))
                .map(CourseMapper::toFullDto);
    }

    /**
     * Updates an existing course's department and capacity.
     * <p>
     * This method finds a course by its title and updates its department and capacity
     * fields with the values provided in the DTO. The title field is used as the
     * identifier and cannot be updated through this method.
     * <p>
     * This operation is transactional to ensure data consistency.
     *
     * @param courseDto the DTO containing the title (used for lookup) and new values
     *                  for department and capacity
     * @return {@code true} if the course was found and successfully updated;
     *         {@code false} if no course with the specified title exists
     * @author Petre Flaviu-Mihai
     */
    @Override
    @Transactional
    public Boolean updateCourse(CourseDto courseDto) {
        Course existingCourse = courseRepository.findByTitle(courseDto.getTitle());

        if (existingCourse == null) {
            System.out.println("Course with title " + courseDto.getTitle() + " does not exist.");
            return false;
        }

        existingCourse.setDepartment(courseDto.getDepartment());
        existingCourse.setCapacity(courseDto.getCapacity());

        courseRepository.save(existingCourse);
        return true;
    }

    /**
     * Deletes a course from the system by its title.
     * <p>
     * This method searches for a course with the specified title and removes it
     * from the database if found. This operation is transactional.
     *
     * @param title the exact title of the course to delete
     * @return {@code true} if the course was found and successfully deleted;
     *         {@code false} if no course with the specified title exists
     * @author Petre Flaviu-Mihai
     */
    @Override
    @Transactional
    public Boolean deleteCourse(String title) {
        Course existingCourse = courseRepository.findByTitle(title);
        if (existingCourse == null) {
            System.out.println("Course with title " + title + " does not exist.");
            return false;
        }

        courseRepository.delete(existingCourse);
        return true;
    }

    /**
     * Retrieves all courses belonging to a specific department.
     * <p>
     * This method queries the repository for all courses that match the given
     * department name and returns them as a list of DTOs.
     *
     * @param department the name of the department to filter courses by
     * @return a {@link List} of {@link CourseDto} belonging to the specified department
     * @throws RuntimeException if no courses are found for the specified department
     * @author Petre Flaviu-Mihai
     */
    @Override
    @Transactional
    public List<CourseDto> getCoursesByDepartment(String department) {
        List<Course> departmentCourses = courseRepository.findCourseByDepartment(department);

        if (departmentCourses.isEmpty()) {
            throw new RuntimeException("Course with department " + department + " does not exist.");
        }

        return departmentCourses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }

    /**
     * Updates the capacity of a specific course.
     * <p>
     * This method allows partial updates to a course by modifying only its capacity.
     * The capacity must be a non-negative integer.
     *
     * @param capacity the new capacity value to set (must be >= 0)
     * @param title    the exact title of the course to update
     * @return {@code true} if the course was found and capacity was successfully updated;
     *         {@code false} if the course was not found or the capacity is negative
     * @author Petre Flaviu-Mihai
     */
    @Override
    @Transactional
    public Boolean updateCapacityOfCourse(Integer capacity, String title) {
        Course existingCourse = courseRepository.findByTitle(title);

        if (existingCourse == null) {
            System.out.println("Course with title " + title + " does not exist.");
            return false;
        }

        if (capacity < 0) {
            System.out.println("Capacity of course with title " + title + " must be positive.");
            return false;
        }

        existingCourse.setCapacity(capacity);

        courseRepository.save(existingCourse);
        return true;
    }

    /**
     * Retrieves all courses that have available capacity.
     * <p>
     * A course is considered available if its capacity is greater than zero.
     * This method uses a custom repository query to filter courses.
     *
     * @return a {@link List} of {@link CourseDto} representing all courses with capacity > 0
     * @throws RuntimeException if no available courses are found in the system
     * @author Petre Flaviu-Mihai
     * @see CourseRepository#findCourseAvailable()
     */
    @Override
    public List<CourseDto> getCoursesAvailable() {
        List<Course> availableCourses = courseRepository.findCourseAvailable();

        if (availableCourses.isEmpty()) {
            throw new RuntimeException("There are no available courses.");
        }

        return availableCourses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all courses sorted by capacity in ascending order.
     * <p>
     * This method returns courses ordered from lowest to highest capacity,
     * which can be useful for displaying courses by availability.
     *
     * @return a {@link List} of {@link CourseDto} sorted by capacity in ascending order;
     *         returns an empty list if no courses exist
     * @author Petre Flaviu-Mihai
     * @see CourseRepository#findAllByOrderByCapacityAsc()
     */
    @Override
    public List<CourseDto> getCoursesSortedByCapacity() {
        List<Course> courses = courseRepository.findAllByOrderByCapacityAsc();

        return courses.stream()
                .map(CourseMapper::toDto)
                .toList();
    }

    /**
     * Retrieves all students from the external Students microservice.
     * <p>
     * This method uses the Feign client to make a synchronous HTTP call to the
     * Students service to fetch all registered students.
     *
     * @return a {@link List} of {@link ExternalStudentDTO} representing all students
     *         from the external service
     * @author Petre Flaviu-Mihai
     * @see StudentsFeignClient#getStudents()
     */
    @Override
    public List<ExternalStudentDTO> getStudents() {
        return studentsFeignClient.getStudents();
    }

    /**
     * Retrieves students filtered by their major/department from the external Students microservice.
     * <p>
     * This method uses the Feign client to fetch students whose major matches
     * the specified department.
     *
     * @param department the major/department to filter students by
     * @return a {@link List} of {@link ExternalStudentDTO} representing students
     *         in the specified department
     * @author Petre Flaviu-Mihai
     * @see StudentsFeignClient#getStudentsByMajor(String)
     */
    @Override
    public List<ExternalStudentDTO> getStudentsByDepartment(String department) {
        return studentsFeignClient.getStudentsByMajor(department);
    }

    /**
     * Synchronizes a course's capacity with the number of students in a department.
     * <p>
     * This method fetches the count of students from the specified department via
     * the Students microservice and updates the course's capacity to match that count.
     * This is useful for automatically adjusting course capacity based on enrollment.
     * <p>
     * The operation will fail if either the students cannot be retrieved from the
     * external service or if the specified course does not exist.
     *
     * @param courseTitle the exact title of the course whose capacity should be updated
     * @param department  the department to count students from
     * @return {@code true} if the course capacity was successfully synchronized;
     *         {@code false} if the students could not be retrieved or the course was not found
     * @author Petre Flaviu-Mihai
     * @see StudentsFeignClient#getStudentsByMajor(String)
     */
    @Override
    public Boolean updateCourseCapacityBasedOnStudentCount(String courseTitle, String department) {
        List<ExternalStudentDTO> students = studentsFeignClient.getStudentsByMajor(department);

        if (students == null) {
            return false;
        }

        Course existingCourse = courseRepository.findByTitle(courseTitle);
        if (existingCourse == null) {
            return false;
        }

        existingCourse.setCapacity(students.size());
        courseRepository.save(existingCourse);
        return true;
    }
}