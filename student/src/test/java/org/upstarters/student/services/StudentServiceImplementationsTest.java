package org.upstarters.student.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upstarters.student.dtos.ExternalCourseDTO;
import org.upstarters.student.dtos.StudentDTO;
import org.upstarters.student.entity.Student;
import org.upstarters.student.repository.StudentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplementationsTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CoursesFeignClient coursesFeignClient;

    @InjectMocks
    private StudentServiceImplementations studentService;

    @Test
    void addStudent_ShouldSaveAndReturnStudent_WhenEmailDoesNotExist() {
        StudentDTO inputDto = new StudentDTO("John", "Doe", "john@test.com", "CS");
        Student savedEntity = new Student("John", "Doe", "john@test.com", "CS");

        when(studentRepository.findByEmail(inputDto.getEmail())).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(savedEntity);

        StudentDTO result = studentService.addStudent(inputDto);

        assertNotNull(result);
        assertEquals(inputDto.getEmail(), result.getEmail());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void addStudent_ShouldThrowException_WhenEmailAlreadyExists() {
        StudentDTO inputDto = new StudentDTO("Jane", "Doe", "jane@test.com", "Math");
        when(studentRepository.findByEmail(inputDto.getEmail())).thenReturn(Optional.of(new Student()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> studentService.addStudent(inputDto));
        assertEquals("Student with this email already exists!", exception.getMessage());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void fetchStudent_ShouldReturnStudent_WhenExists() {
        String email = "test@test.com";
        Student student = new Student("Test", "User", email, "Physics");
        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(student));

        StudentDTO result = studentService.fetchStudent(email);

        assertEquals(email, result.getEmail());
        assertEquals("Physics", result.getMajor());
    }

    @Test
    void fetchStudent_ShouldThrowException_WhenNotFound() {
        String email = "unknown@test.com";
        when(studentRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> studentService.fetchStudent(email));
    }

    @Test
    void countStudents_ShouldReturnCount() {
        when(studentRepository.count()).thenReturn(5L);

        Long count = studentService.countStudents();

        assertEquals(5L, count);
    }

    @Test
    void fetchStudentsByMajor_ShouldReturnList() {
        String major = "CS";
        List<Student> students = Arrays.asList(
                new Student("A", "B", "a@b.com", major),
                new Student("C", "D", "c@d.com", major)
        );
        when(studentRepository.findAllByMajor(major)).thenReturn(students);

        List<StudentDTO> results = studentService.fetchStudentsByMajor(major);

        assertEquals(2, results.size());
        assertEquals(major, results.get(0).getMajor());
    }

    @Test
    void fetchStudents_ShouldReturnAllStudents() {
        List<Student> students = Arrays.asList(new Student(), new Student());
        when(studentRepository.findAll()).thenReturn(students);

        List<StudentDTO> results = studentService.fetchStudents();

        assertEquals(2, results.size());
    }

    @Test
    void updateStudent_ShouldUpdateFields_WhenExists() {
        String email = "old@test.com";
        StudentDTO updateDto = new StudentDTO("NewName", "NewLast", email, "NewMajor");
        Student existingStudent = new Student("OldName", "OldLast", email, "OldMajor");

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(existingStudent));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentDTO result = studentService.updateStudent(email, updateDto);

        assertEquals("NewName", result.getFirstName());
        assertEquals("NewMajor", result.getMajor());
        verify(studentRepository).save(existingStudent);
    }

    @Test
    void deleteStudent_ShouldReturnTrue_WhenExists() {
        String email = "delete@test.com";
        Student student = new Student();
        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(student));

        boolean result = studentService.deleteStudent(email);

        assertTrue(result);
        verify(studentRepository).delete(student);
    }

    @Test
    void fetchStudentIdFromEmail_ShouldReturnId() {
        String email = "id@test.com";
        Student student = new Student();
        student.setId(123L);
        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(student));

        Long id = studentService.fetchStudentIdFromEmail(email);

        assertEquals(123L, id);
    }

    @Test
    void fetchStudentEmailFromId_ShouldReturnEmail() {
        Long id = 100L;
        Student student = new Student("A", "B", "found@test.com", "M");
        when(studentRepository.findById(id)).thenReturn(Optional.of(student));

        String email = studentService.fetchStudentEmailFromId(id);

        assertEquals("found@test.com", email);
    }

    @Test
    void getRecommendedCourses_ShouldReturnCoursesBasedOnMajor() {
        String email = "rec@test.com";
        String major = "Biology";
        Student student = new Student("A", "B", email, major);
        ExternalCourseDTO course = new ExternalCourseDTO("Bio 101", "Biology", 30);

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(student));
        when(coursesFeignClient.fetchCoursesByDepartment(major)).thenReturn(Arrays.asList(course));

        List<ExternalCourseDTO> result = studentService.getRecommendedCourses(email);

        assertEquals(1, result.size());
        assertEquals("Bio 101", result.get(0).getTitle());
    }

    @Test
    void updateMajorFromCourse_ShouldUpdateMajor_WhenCourseExists() {
        String email = "major@test.com";
        String courseTitle = "Intro to Chem";
        Student student = new Student("A", "B", email, "Undecided");
        ExternalCourseDTO course = new ExternalCourseDTO(courseTitle, "Chemistry", 50);

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(student));
        when(coursesFeignClient.fetchCourseByTitle(courseTitle)).thenReturn(course);
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        StudentDTO result = studentService.updateMajorFromCourse(email, courseTitle);

        assertEquals("Chemistry", result.getMajor());
        verify(studentRepository).save(student);
    }

    @Test
    void updateMajorFromCourse_ShouldThrowException_WhenCourseNotFound() {
        String email = "major@test.com";
        String courseTitle = "NonExistent";
        Student student = new Student("A", "B", email, "Undecided");

        when(studentRepository.findByEmail(email)).thenReturn(Optional.of(student));
        when(coursesFeignClient.fetchCourseByTitle(courseTitle)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> studentService.updateMajorFromCourse(email, courseTitle));
        assertTrue(ex.getMessage().contains("Course with title " + courseTitle + " not found"));
    }
}