package com.vikash.esd_final_project.controller;

import com.vikash.esd_final_project.dto.EmployeeRequest;
import com.vikash.esd_final_project.dto.EmployeeResponse;
import com.vikash.esd_final_project.dto.PagedResponse;
import com.vikash.esd_final_project.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @GetMapping("/")
    public ResponseEntity<PagedResponse<EmployeeResponse>> getAllEmployees(
            @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        PagedResponse<EmployeeResponse> employees = employeeService.getAllEmployeesPaginated(pageable);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<EmployeeResponse>> searchEmployeesByName(
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        PagedResponse<EmployeeResponse> employees = employeeService.searchEmployeesByName(name, pageable);
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse employee = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(employee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
