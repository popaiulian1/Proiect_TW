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

    /**
     * Adds a new student to the system.
     * <p>
     * This method first checks if a student with the provided email already exists
     * in the repository. If the email is unique, the student data is mapped to an
     * entity and persisted to the database.
     *
     * @author Opris Liviu Vlad
     * @param  studentDTO the data transfer object containing the new student's information
     * @return            the persisted student information converted back to a DTO
     * @throws RuntimeException if a student with the specified email already exists
     * @see               StudentDTO
     */
    @Override
    public StudentDTO addStudent(StudentDTO studentDTO) {
        if(studentRepository.findByEmail(studentDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Student with this email already exists!");
        }

        Student student = StudentMapper.toEntity(studentDTO);
        studentRepository.save(student);
        return StudentMapper.toDTO(student);
    }

    /**
     * Retrieves a specific student based on their email address.
     * <p>
     * Searches the repository for a student entry matching the given email.
     * If found, the entity is converted to a DTO and returned.
     *
     * @author Opris Liviu Vlad
     * @param  email the email address used to identify the student
     * @return       the student details associated with the given email
     * @throws RuntimeException if no student is found with the provided email
     */
    @Override
    public StudentDTO fetchStudent(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));
        return StudentMapper.toDTO(student);
    }

    /**
     * Returns the total number of students currently registered in the system.
     *
     * @author Opris Liviu Vlad
     * @return the count of student entities in the database
     */
    @Override
    public Long countStudents() {
        return studentRepository.count();
    }

    /**
     * Retrieves a list of students associated with a specific major.
     * <p>
     * This method queries the repository for all students who have declared
     * the specified major and maps the resulting entities to a list of DTOs.
     *
     * @author Opris Liviu Vlad
     * @param  major the major field to filter students by
     * @return       a list of students belonging to the specified major
     */
    @Override
    public List<StudentDTO> fetchStudentsByMajor(String major) {
        List<Student> student = studentRepository.findAllByMajor(major);

        return student
                .stream()
                .map(StudentMapper::toDTO)
                .toList();
    }

    /**
     * Retrieves all students registered in the system.
     * <p>
     * Fetches all student entities from the database and converts them
     * into a list of Data Transfer Objects.
     *
     * @author Opris Liviu Vlad
     * @return a list of all students
     */
    @Override
    public List<StudentDTO> fetchStudents() {
        List<Student> students = studentRepository.findAll();

        return students
                .stream()
                .map(StudentMapper::toDTO)
                .toList();
    }

    /**
     * Updates the information of an existing student identified by email.
     * <p>
     * This method locates the student by email, updates their first name, last name,
     * email, and major based on the provided DTO, and saves the changes.
     *
     * @author Opris Liviu Vlad
     * @param  email      the email address of the student to update
     * @param  studentDTO the object containing the updated student information
     * @return            the updated student details
     * @throws RuntimeException if the student with the specified email does not exist
     */
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

    /**
     * Deletes a student from the system identified by their email address.
     * <p>
     * Attempts to find the student entity by email and removes it from the repository.
     *
     * @author Opris Liviu Vlad
     * @param  email the email address of the student to delete
     * @return       true if the deletion was successful
     * @throws RuntimeException if the student with the specified email does not exist
     */
    @Override
    public boolean deleteStudent(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));

        studentRepository.delete(student);
        return true;
    }

    /**
     * Retrieves the internal database ID for a student based on their email.
     *
     * @author Opris Liviu Vlad
     * @param  email the email address of the student
     * @return       the unique identifier (ID) of the student
     * @throws RuntimeException if the student with the specified email does not exist
     */
    @Override
    public Long fetchStudentIdFromEmail(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));

        return student.getId();
    }

    /**
     * Retrieves the email address associated with a specific student ID.
     *
     * @author Opris Liviu Vlad
     * @param  id the unique identifier of the student
     * @return    the email address of the student
     * @throws RuntimeException if the student with the specified ID does not exist
     */
    @Override
    public String fetchStudentEmailFromId(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student with this id does not exist!"));

        return student.getEmail();
    }

    /**
     * Fetches a list of recommended external courses for a student.
     * <p>
     * This method retrieves the student's major using their email and then
     * calls an external service (via Feign client) to find courses offered
     * by that department.
     *
     * @author Opris Liviu Vlad
     * @param  email the email address of the student
     * @return       a list of external courses recommended for the student's major
     * @throws RuntimeException if the student with the specified email does not exist
     * @see          CoursesFeignClient
     */
    @Override
    public List<ExternalCourseDTO> getRecommendedCourses(String email) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student with this email does not exist!"));

        String major = student.getMajor();

        List<ExternalCourseDTO> recommendedCourses = coursesFeignClient.fetchCoursesByDepartment(major);

        return recommendedCourses;
    }

    /**
     * Updates a student's major based on the department of a specified course.
     * <p>
     * This method looks up a course by its title using an external service. If the
     * course exists, the student's major is updated to match the course's department.
     *
     * @author Opris Liviu Vlad
     * @param  email       the email address of the student to update
     * @param  courseTitle the title of the course used to determine the new major
     * @return             the updated student details
     * @throws RuntimeException if the student does not exist or the course is not found
     * @see                CoursesFeignClient
     */
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
