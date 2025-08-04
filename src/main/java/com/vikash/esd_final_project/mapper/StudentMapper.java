package com.vikash.esd_final_project.mapper;

import com.vikash.esd_final_project.dto.StudentRequest;
import com.vikash.esd_final_project.dto.StudentResponse;
import com.vikash.esd_final_project.entity.Student;
import org.springframework.stereotype.Service;

@Service
public class StudentMapper {
    public Student toStudent(StudentRequest request) {
        return Student.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .domain(request.domain())
                .credits(request.credits())
                .cgpa(request.cgpa())
                .gradYear(request.gradYear())
                .organization(request.organization())
                .specialisation(request.specialisation())
                .rollno(request.rollno())
                .placement(request.placement())
                .photoPath(request.photo_path())
                .build();
    }

    public StudentResponse toStudentResponse(Student student) {
        return new StudentResponse(
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getDomain(),
                student.getGradYear(),
                student.getOrganization(),
                student.getSpecialisation());
    }

    public void updateStudentFromRequest(StudentRequest request, Student student) {
        if (request.firstName() != null) {
            student.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            student.setLastName(request.lastName());
        }
        if (request.email() != null) {
            student.setEmail(request.email());
        }
        if (request.domain() != null) {
            student.setDomain(request.domain());
        }
        if (request.credits() > 0) {
            student.setCredits(request.credits());
        }
        if (request.cgpa() > 0) {
            student.setCgpa(request.cgpa());
        }
        if (request.gradYear() > 0) {
            student.setGradYear(request.gradYear());
        }
        if (request.organization() != null) {
            student.setOrganization(request.organization());
        }
        if (request.specialisation() != null) {
            student.setSpecialisation(request.specialisation());
        }
        if (request.rollno() != null) {
            student.setRollno(request.rollno());
        }
        if (request.placement() != null) {
            student.setPlacement(request.placement());
        }
        if (request.photo_path() != null) {
            student.setPhotoPath(request.photo_path());
        }
    }
}
