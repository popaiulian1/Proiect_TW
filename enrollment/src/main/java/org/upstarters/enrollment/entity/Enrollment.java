package org.upstarters.enrollment.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity()
@Table(name = "enrollments")
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name="enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name="grade")
    private Double grade;


    public Enrollment(Long studentId, Long courseId, LocalDate enrollmentDate, Double grade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.grade = grade;
    }

    public Enrollment() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }
}
