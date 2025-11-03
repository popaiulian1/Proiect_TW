package org.upstarters.enrollment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upstarters.enrollment.entity.Enrollment;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<List<Enrollment>> findEnrollmentsByCourseId(Long courseId);
    Optional<List<Enrollment>> findEnrollmentsByGradeLessThanEqual(double grade);
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}
