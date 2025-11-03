package org.upstarters.enrollment.entity;

import jakarta.persistence.*;

@Entity(name="courses")
public class Course {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id", unique = true)
    private Long id;

    @Column(name="title")
    private String title;

    @Column(name="capacity")
    private int capacity;

    @Column(name="department")
    private String department;

    public Course(Long id, String title, int capacity, String department) {
        this.id = id;
        this.title = title;
        this.capacity = capacity;
        this.department = department;
    }

    public Course() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
