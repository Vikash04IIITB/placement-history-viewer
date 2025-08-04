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
@Table(name = "students")
public class Student {
    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "student")
    private List<Alumni> alumni;

    @OneToMany(mappedBy = "student")
    private List<PlacementStudent> placementStudent;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email" ,unique = true,nullable = false)
    private String email;

    @Column(name = "rollno",unique = true,nullable = false)
    private String rollno;

    @Column(name = "cgpa")
    private int cgpa;

    @Column(name = "graduation_year")
    private int gradYear;

    @Column(name = "total_credits")
    private double credits;

    @Column(name = "org")
    private String organization;

    @ManyToOne
    @JoinColumn(name = "domain")
    private Domains domain;

    @ManyToOne
    @JoinColumn(name = "specialisation")
    private Specialisation specialisation;

    //placement_id FK.
    @ManyToOne
    @JoinColumn(name = "place_id")
    private Placement placement;

    @Column(name = "photo_path")
    private String photoPath;
}
