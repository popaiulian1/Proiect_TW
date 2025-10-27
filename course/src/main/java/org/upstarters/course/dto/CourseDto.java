package org.upstarters.course.dto;

public class CourseDto {

    //region Fields
    private String title;
    private String description;
    private Integer capacity;
    //endregion

    //region Constructors
    public CourseDto() {}

    public CourseDto(String title, String description, Integer capacity) {
        this.title = title;
        this.description = description;
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
