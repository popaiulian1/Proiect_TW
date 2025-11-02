package org.upstarters.student.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upstarters.student.dtos.StudentDTO;
import org.upstarters.student.services.IStudentService;

@RestController
@RequestMapping(path = "/student")
public class StudentController {

    @Autowired
    private IStudentService studentService;

    @PostMapping("/create")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO createdStudent = studentService.addStudent(studentDTO);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<StudentDTO> fetchStudent(@Valid @PathVariable String email) {
        StudentDTO fetchedStudent = studentService.fetchStudent(email);
        return new ResponseEntity<>(fetchedStudent, HttpStatus.FOUND);
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<StudentDTO> updateStudent(@Valid @PathVariable String email, @Valid @RequestBody StudentDTO studentDTO) {
        StudentDTO updatedStudent = studentService.updateStudent(email, studentDTO);
        return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{email}")
    public ResponseEntity<Boolean> deleteStudent(@Valid @PathVariable String email) {
        boolean deleted = studentService.deleteStudent(email);
        return ResponseEntity.ok(deleted);
    }
}
