package org.upstarters.enrollment.dto;

public class EnrollmentRequestDTO {
    private String studentName;
    private String courseName;

    public EnrollmentRequestDTO() {
    }

    public EnrollmentRequestDTO(String studentName, String courseName) {
        this.studentName = studentName;
        this.courseName = courseName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
