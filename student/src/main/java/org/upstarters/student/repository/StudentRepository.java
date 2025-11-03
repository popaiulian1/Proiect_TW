package org.upstarters.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.upstarters.student.entity.Student;
import org.upstarters.student.enums.Major;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    //List<Student> findAllByMajor(String major);
}
