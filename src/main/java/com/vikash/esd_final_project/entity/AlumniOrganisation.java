package com.vikash.esd_final_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alumni_org")
public class AlumniOrganisation {

    @Id
    // @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // FK to organistaion table's org column.
    @ManyToOne
    @JoinColumn(name = "org", referencedColumnName = "org")
    private Organisation organisation;

    // FK to alumni table's PK
    @ManyToOne
    @JoinColumn(name = "alumni_id")
    private Alumni alumni;

    @Column(name = "position")
    private String position;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "leave_date")
    private LocalDate leaveDate;

}
