package org.upstarters.course.entity;

import jakarta.persistence.*;

@Entity
@Table (name = "courses")
public class Course {

    //region Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id")
    private Long course_id;

    @Column (name = "title")
    private String title;

    @Column(name = "department")
    private String department;

    @Column (name = "capacity")
    private Integer capacity;
    //endregion

    //region Constructors
    public Course() {}

    public Course(String title, String department, Integer capacity) {
        this.title = title;
        this.department = department;
        this.capacity = capacity;
    }

    public Course(Long course_id, String title, String department, Integer capacity) {
        this.course_id = course_id;
        this.title = title;
        this.department = department;
        this.capacity = capacity;
    }

    //endregion

    //region Getters and Setters

    public Long getCourseId() {
        return course_id;
    }

    public void setCourseId(Long course_id) {
        this.course_id = course_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() { return department; }

    public void setDepartment(String department) { this.department = department; }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    //endregion
}



