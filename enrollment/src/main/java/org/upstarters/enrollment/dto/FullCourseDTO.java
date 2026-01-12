package org.upstarters.enrollment.dto;

public class FullCourseDTO {
    private Long id;
    private String title;
    private String department;
    private Integer capacity;
    //endregion

    //region Constructors
    public FullCourseDTO() {}

    public FullCourseDTO(Long id, String title, String department, Integer capacity) {
        this.id = id;
        this.title = title;
        this.department = department;
        this.capacity = capacity;
    }
    //endregion

    //region Getters and Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() { return department; }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
