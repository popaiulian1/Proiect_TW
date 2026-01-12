package org.upstarters.enrollment.dto;

public class EnrollmentRequestDTO {
    private String studentEmail;
    private String courseName;

    public EnrollmentRequestDTO() {
    }

    public EnrollmentRequestDTO(String studentEmail, String courseName) {
        this.studentEmail = studentEmail;
        this.courseName = courseName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}