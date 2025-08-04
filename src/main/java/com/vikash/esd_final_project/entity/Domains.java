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
@Table(name = "domains")
public class Domains {
    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "domain")
    private List<PlacementFilter> placementFilters;

    @OneToMany(mappedBy = "domain")
    private List<Student> students;

    @Column(name = "program")
    private String program;

    @Column(name = "batch")
    private String batch;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "qualification")
    private String qualification;
}
