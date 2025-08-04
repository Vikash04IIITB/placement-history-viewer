package com.vikash.esd_final_project.service;

import com.vikash.esd_final_project.dto.EmployeeRequest;
import com.vikash.esd_final_project.dto.EmployeeResponse;
import com.vikash.esd_final_project.dto.LoginRequest;
import com.vikash.esd_final_project.dto.PagedResponse;
import com.vikash.esd_final_project.entity.Employee;
import com.vikash.esd_final_project.exception.EmployeeNotFoundException;
import com.vikash.esd_final_project.helper.Encryption;
import com.vikash.esd_final_project.helper.JWTHelperSecure;
import com.vikash.esd_final_project.mapper.EmployeeMapper;
import com.vikash.esd_final_project.repo.EmployeeRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class EmployeeService {
    private final EmployeeMapper employeeMapper;
    private final EmployeeRepo employeeRepo;
    private final Encryption encryption;
    private final JWTHelperSecure jWTHelper;

    @CacheEvict(value = "employees", allEntries = true)
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = employeeMapper.toEmployee(request);
        employee.setPassword(encryption.encode(employee.getPassword()));
        Employee savedEmployee = employeeRepo.save(employee);
        return employeeMapper.toEmployeeResponse(savedEmployee);
    }

    @Cacheable(value = "employees", key = "#email")
    public Employee getEmployeeByEmail(String email) {
        return employeeRepo.findByEmail(email)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with email " + email + " not found"));
    }

    @Cacheable(value = "employees", key = "#id")
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));
        return employeeMapper.toEmployeeResponse(employee);
    }

    @Cacheable(value = "employees", key = "'all-employees-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public PagedResponse<EmployeeResponse> getAllEmployeesPaginated(Pageable pageable) {
        Page<Employee> employeePage = employeeRepo.findAllEmployees(pageable);
        Page<EmployeeResponse> responsePage = employeePage.map(employeeMapper::toEmployeeResponse);
        return PagedResponse.of(responsePage);
    }

    @Cacheable(value = "employees", key = "'search-' + #name + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public PagedResponse<EmployeeResponse> searchEmployeesByName(String name, Pageable pageable) {
        Page<Employee> employeePage = employeeRepo.findEmployeesByName(name, pageable);
        Page<EmployeeResponse> responsePage = employeePage.map(employeeMapper::toEmployeeResponse);
        return PagedResponse.of(responsePage);
    }

    @CacheEvict(value = "employees", allEntries = true)
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee existingEmployee = employeeRepo.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee with id " + id + " not found"));

        employeeMapper.updateEmployeeFromRequest(request, existingEmployee);
        if (request.password() != null && !request.password().isEmpty()) {
            existingEmployee.setPassword(encryption.encode(request.password()));
        }
        Employee savedEmployee = employeeRepo.save(existingEmployee);
        return employeeMapper.toEmployeeResponse(savedEmployee);
    }

    @CacheEvict(value = "employees", allEntries = true)
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepo.existsById(id)) {
            throw new EmployeeNotFoundException("Employee with id " + id + " not found");
        }
        employeeRepo.deleteById(id);
    }

    public String login(@Valid LoginRequest request) {
        Employee employee = getEmployeeByEmail(request.email());
        if (employee == null) {
            throw new EmployeeNotFoundException("Employee with email " + request.email() + " not found");
        }
        if (!encryption.validates(request.password(), employee.getPassword())) {
            throw new EmployeeNotFoundException("Invalid password");
        }
        return jWTHelper.generateToken(request.email());
    }
}
