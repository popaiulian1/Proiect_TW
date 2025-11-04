package org.upstarters.course.dto;

public class CourseDto {

    //region Fields
    private String title;
    private String department;
    private Integer capacity;
    //endregion

    //region Constructors
    public CourseDto() {}

    public CourseDto(String title, String department, Integer capacity) {
        this.title = title;
        this.department = department;
        this.capacity = capacity;
    }
    //endregion

    //region Getters and Setters

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

    //endregion
}
