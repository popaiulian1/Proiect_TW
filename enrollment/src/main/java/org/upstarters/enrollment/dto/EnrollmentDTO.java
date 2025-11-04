package org.upstarters.enrollment.dto;

public class EnrollmentDTO {

    private String student;
    private String course;
    private String enrollmentDate;
    private double grade;

    public EnrollmentDTO(String student, String course, String enrollmentDate, double grade) {
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.grade = grade;
    }

    public EnrollmentDTO() {
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    @Override
    public String toString(){
        return this.student + " " + this.course + " " + this.enrollmentDate + " " + this.grade;
    }
}
