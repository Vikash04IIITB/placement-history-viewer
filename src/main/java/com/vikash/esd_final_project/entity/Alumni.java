package com.vikash.esd_final_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alumni")
public class Alumni {
    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // sid here is fk referencing sid of students table.
    @ManyToOne
    @JoinColumn(name = "sid")
    private Student student;

    @OneToMany(mappedBy = "alumni")
    private List<AlumniOrganisation> alumniOrganisations;

    @Column(name = "email")
    private String email;

    @Column(name = "contact")
    private long contact;

}
