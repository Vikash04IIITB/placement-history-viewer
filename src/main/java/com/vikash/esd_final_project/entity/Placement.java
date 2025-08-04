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
@Table(name = "placement")
public class Placement {
        @Id
//        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        //FK to organisation table,org column.
       @ManyToOne
       @JoinColumn(name = "org",referencedColumnName = "org")
       private Organisation org;

       @OneToMany(mappedBy = "placement")
       private List<PlacementStudent> placementStudents;

       @OneToMany(mappedBy = "placement")
       private List<Student> students;

       @OneToMany(mappedBy = "placement")
       private List<PlacementFilter> placementFilters;

       @Column(name = "profile")
        private String profile;

       @Column(name = "description")
       private String description;

       @Column(name = "intake")
       private int intake;

       @Column(name = "min_grade")
        private double minGrade;
}
