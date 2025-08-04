package com.vikash.esd_final_project.service;

import com.vikash.esd_final_project.dto.PagedResponse;
import com.vikash.esd_final_project.dto.StudentListResponse;
import com.vikash.esd_final_project.dto.StudentRequest;
import com.vikash.esd_final_project.dto.StudentResponse;
import com.vikash.esd_final_project.dto.StudentSearchResponse;
import com.vikash.esd_final_project.entity.Student;
import com.vikash.esd_final_project.exception.StudentNotFoundException;
import com.vikash.esd_final_project.mapper.StudentMapper;
import com.vikash.esd_final_project.repo.StudentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepo studentRepo;
    private final StudentMapper studentMapper;

    @Cacheable(value = "students", key = "'all-students'")
    public List<StudentListResponse> showAllStudents() {
        List<Object[]> results = studentRepo.showAllStudents();
        return results.stream()
                .map(row -> new StudentListResponse(
                        (Long) row[0], // id
                        (String) row[1], // firstName
                        (String) row[2], // lastName
                        (String) row[3], // email
                        (String) row[4] // placementStatus
                ))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "students", key = "'paginated-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public PagedResponse<StudentResponse> getAllStudentsPaginated(Pageable pageable) {
        Page<Student> studentPage = studentRepo.findAllStudents(pageable);
        Page<StudentResponse> responsePage = studentPage.map(studentMapper::toStudentResponse);
        return PagedResponse.of(responsePage);
    }

    @Cacheable(value = "student-search", key = "#keyword")
    public List<StudentSearchResponse> showStudentsByKeyword(String keyword) {
        List<Object[]> results = studentRepo.showStudentsByKeyword(keyword);
        if (results.isEmpty()) {
            throw new StudentNotFoundException(String.format("Student with keyword %s not found", keyword));
        }
        return results.stream()
                .map(row -> new StudentSearchResponse(
                        (String) row[0], // firstName
                        (String) row[1], // lastName
                        (String) row[2], // program
                        (String) row[3], // placementOrg
                        (String) row[4], // alumniOrg
                        row[5] != null ? (Integer) row[5] : null, // graduationYear
                        (String) row[6], // isAlumni
                        (String) row[7] // placementStatus
                ))
                .collect(Collectors.toList());
    }

    @CacheEvict(value = { "students", "student-search" }, allEntries = true)
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        Student student = studentMapper.toStudent(request);
        Student savedStudent = studentRepo.save(student);
        return studentMapper.toStudentResponse(savedStudent);
    }

    @CacheEvict(value = { "students", "student-search" }, allEntries = true)
    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student existingStudent = studentRepo.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));

        studentMapper.updateStudentFromRequest(request, existingStudent);
        Student savedStudent = studentRepo.save(existingStudent);
        return studentMapper.toStudentResponse(savedStudent);
    }

    @Cacheable(value = "students", key = "#id")
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));
        return studentMapper.toStudentResponse(student);
    }

    @CacheEvict(value = { "students", "student-search" }, allEntries = true)
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepo.existsById(id)) {
            throw new StudentNotFoundException("Student with id " + id + " not found");
        }
        studentRepo.deleteById(id);
    }
}
