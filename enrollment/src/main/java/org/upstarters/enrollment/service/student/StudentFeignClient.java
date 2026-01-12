package org.upstarters.enrollment.service.student;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.upstarters.enrollment.dto.StudentDTO;

@FeignClient(name = "students", path = "/students")
public interface StudentFeignClient {
    @GetMapping("/getByEmail/{email}")
    StudentDTO getStudentByEmail(@PathVariable("email") String email);

    @GetMapping("/getStudentById/{studentId}")
    StudentDTO getStudentById(@PathVariable("studentId") Long studentId);

    @PutMapping("/update/{email}")
    StudentDTO updateStudent(@PathVariable("email") String email, @RequestBody StudentDTO studentDTO);
}
