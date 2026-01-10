package org.upstarters.enrollment.service.student;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.upstarters.enrollment.dto.StudentDTO;

@FeignClient(name = "student-service", path = "/students")
public interface StudentFeignClient {
    @GetMapping("/getByEmail/{email}")
    StudentDTO getStudentByEmail(@PathVariable("email") String email);
}
