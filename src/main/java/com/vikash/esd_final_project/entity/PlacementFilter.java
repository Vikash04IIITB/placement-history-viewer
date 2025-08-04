package com.vikash.esd_final_project.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "placememt_filter")
public class PlacementFilter {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Placement placement;

    @ManyToOne
    @JoinColumn(name = "specialisation")
    private Specialisation specialisation;

    @ManyToOne
    @JoinColumn(name = "domain")
    private Domains domain;
}
