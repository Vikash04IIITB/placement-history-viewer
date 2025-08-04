package com.vikash.esd_final_project.mapper;

import com.vikash.esd_final_project.dto.EmployeeRequest;
import com.vikash.esd_final_project.dto.EmployeeResponse;
import com.vikash.esd_final_project.entity.Employee;
import org.springframework.stereotype.Service;

@Service
public class EmployeeMapper {
    public Employee toEmployee(EmployeeRequest request) {
        return Employee.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .title(request.title())
                .photographPath(request.photographPath())
                .email(request.email())
                .department(request.departmentName())
                .password(request.password())
                .build();
    }

    public EmployeeResponse toEmployeeResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getTitle(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getPhotographPath());
    }

    public void updateEmployeeFromRequest(EmployeeRequest request, Employee employee) {
        if (request.firstName() != null) {
            employee.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            employee.setLastName(request.lastName());
        }
        if (request.title() != null) {
            employee.setTitle(request.title());
        }
        if (request.photographPath() != null) {
            employee.setPhotographPath(request.photographPath());
        }
        if (request.email() != null) {
            employee.setEmail(request.email());
        }
        if (request.departmentName() != null) {
            employee.setDepartment(request.departmentName());
        }
    }
}
