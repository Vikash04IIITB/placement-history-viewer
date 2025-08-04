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
@Table(name = "organisation")
public class Organisation {
    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "org", unique = true)
    private String org;

    @OneToMany(mappedBy = "org")
    private List<Placement> placements;

    @OneToMany(mappedBy = "organisation")
    private List<AlumniOrganisation> alumniOrganisations;

    @Column(name = "address")
    private String address;
}
