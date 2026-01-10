package org.upstarters.enrollment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upstarters.enrollment.entity.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<List<Enrollment>> findEnrollmentsByCourseId(Long courseId);
    Optional<List<Enrollment>> findEnrollmentsByGradeLessThanEqual(double grade);
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
    boolean existsByCourseId(Long courseId);
    boolean existsByStudentId(Long studentId);
    Optional<List<Enrollment>> findAllByCourseId(Long courseId);
    Optional<List<Enrollment>> findAllByGradeLessThanEqual(double grade);
    Optional<List<Enrollment>> findAllByStudentId(Long studentId);
}
