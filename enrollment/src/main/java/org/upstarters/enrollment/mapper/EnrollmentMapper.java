package org.upstarters.enrollment.mapper;

import org.springframework.stereotype.Component;
import org.upstarters.enrollment.dto.EnrollmentDTO;
import org.upstarters.enrollment.entity.Enrollment;
import org.upstarters.enrollment.service.course.CourseAPIService;
import org.upstarters.enrollment.service.student.StudentAPIService;

import java.time.format.DateTimeFormatter;

@Component
public class EnrollmentMapper {

    private final StudentAPIService studentAPIService;
    private final CourseAPIService courseAPIService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public EnrollmentMapper(StudentAPIService studentAPIService, CourseAPIService courseAPIService) {
        this.studentAPIService = studentAPIService;
        this.courseAPIService = courseAPIService;
    }

    public EnrollmentDTO toDto(Enrollment enrollment) {
        if (enrollment == null) {
            return null;
        }

        String studentName = studentAPIService.getStudentNameById(enrollment.getStudentId());
        String courseName = courseAPIService.getCourseNameById(enrollment.getCourseId());
        String formattedDate = enrollment.getEnrollmentDate() != null ? enrollment.getEnrollmentDate().format(DATE_FORMATTER) : null;

        return new EnrollmentDTO(
                studentName,
                courseName,
                formattedDate,
                enrollment.getGrade()
        );
    }
}
