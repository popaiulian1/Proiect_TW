package org.upstarters.course.mapper;

import org.upstarters.course.dto.CourseDto;
import org.upstarters.course.entity.Course;

public class CourseMapper {

    public static CourseDto toDto(Course courseEntity) {
        return new CourseDto(
                courseEntity.getTitle(),
                courseEntity.getDepartment(),
                courseEntity.getCapacity()
        );
    }

    public static Course toEntity(CourseDto courseDto) {
        return new Course(
                courseDto.getTitle(),
                courseDto.getDepartment(),
                courseDto.getCapacity()
        );
    }
}
