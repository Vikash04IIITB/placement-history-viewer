package com.vikash.esd_final_project.controller;

import com.vikash.esd_final_project.dto.PagedResponse;
import com.vikash.esd_final_project.dto.StudentListResponse;
import com.vikash.esd_final_project.dto.StudentRequest;
import com.vikash.esd_final_project.dto.StudentResponse;
import com.vikash.esd_final_project.dto.StudentSearchResponse;
import com.vikash.esd_final_project.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/")
    public ResponseEntity<List<StudentListResponse>> getAllStudents() {
        List<StudentListResponse> students = studentService.showAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/paginated")
    public ResponseEntity<PagedResponse<StudentResponse>> getAllStudentsPaginated(
            @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        PagedResponse<StudentResponse> students = studentService.getAllStudentsPaginated(pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<StudentSearchResponse>> showStudentsByKeyword(@PathVariable String keyword) {
        List<StudentSearchResponse> students = studentService.showStudentsByKeyword(keyword);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        StudentResponse student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse student = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {
        StudentResponse student = studentService.updateStudent(id, request);
        return ResponseEntity.ok(student);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
