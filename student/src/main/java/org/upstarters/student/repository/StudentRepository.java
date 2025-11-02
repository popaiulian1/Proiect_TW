package org.upstarters.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.upstarters.student.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
