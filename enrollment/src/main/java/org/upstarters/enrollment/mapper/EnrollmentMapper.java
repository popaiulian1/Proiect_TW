package org.upstarters.enrollment.mapper;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;
import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.entity.Enrollment;
import org.upstarters.enrollment.service.course.CourseAPIService;

@Component
public class EnrollmentMapper {

    private final CourseAPIService courseAPIService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public EnrollmentMapper(CourseAPIService courseAPIService) {
        this.courseAPIService = courseAPIService;
    }

    public EnrollmentDTO toDto(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        String courseName = courseAPIService.getCourseNameById(enrollment.getCourseId());
        String formattedDate = enrollment.getEnrollmentDate() != null ? enrollment.getEnrollmentDate().format(DATE_FORMATTER) : null;

        return new EnrollmentDTO(
                enrollment.getStudentEmail(),
                courseName,
                formattedDate,
                enrollment.getGrade()
        );
    }
}