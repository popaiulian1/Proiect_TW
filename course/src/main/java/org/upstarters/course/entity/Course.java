package org.upstarters.course.entity;

import jakarta.persistence.*;

@Entity
@Table (name = "Course")
public class Course {

    //region Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "course_id")
    private Long course_id;

    @Column (name = "title")
    private String title;

    @Column (name = "description")
    private String description;

    @Column (name = "capacity")
    private Integer capacity;
    //endregion

    //region Constructors
    public Course() {}

    public Course(Integer capacity, String description, String title, Long course_id) {
        this.capacity = capacity;
        this.description = description;
        this.title = title;
        this.course_id = course_id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    //endregion
}
