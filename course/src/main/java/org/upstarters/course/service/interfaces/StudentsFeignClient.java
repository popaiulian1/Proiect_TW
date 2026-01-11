package org.upstarters.course.service.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.upstarters.course.dto.ExternalStudentDTO;

import java.util.List;

@FeignClient(name = "students", path = "/students")
public interface StudentsFeignClient {

    @GetMapping("/getStudents")
    List<ExternalStudentDTO> getStudents();

    @GetMapping("/getStudentsByMajor/{major}")
    List<ExternalStudentDTO> getStudentsByMajor(@PathVariable("major") String major);
}
