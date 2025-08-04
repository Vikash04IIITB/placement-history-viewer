package com.vikash.esd_final_project.repo;

import com.vikash.esd_final_project.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepo extends JpaRepository<Student, Long> {

        // @Query(value = "select s.id,s.first_name,s.last_name,s.email,case when" +
        // " exists(select 1 from placement_student p where s.id = p.sid) then 'Placed'
        // else " +
        // "'Unplaced' end as placement_status" + " from students s" ,nativeQuery =
        // true)
        // public List<Object[]> showAllStudents();

        @Query("SELECT s.id, s.firstName, s.lastName, s.email, " +
                        "CASE WHEN EXISTS (SELECT p FROM PlacementStudent p WHERE p.student.id = s.id) " +
                        "THEN 'Placed' ELSE 'Unplaced' END AS placementStatus " +
                        "FROM Student s")
        public List<Object[]> showAllStudents();

        @Query("SELECT s FROM Student s")
        Page<Student> findAllStudents(Pageable pageable);

        @Query("SELECT s FROM Student s WHERE s.email = :email")
        Page<Student> findStudentsByEmail(@Param("email") String email, Pageable pageable);

        @Query(value = "select " +
                        "s.first_name ,s.last_name,d.program,p.org,ao.org,s.graduation_year, " +
                        "case " +
                        "when exists(select 1 from alumni a where s.id = a.sid) then 'Yes' " +
                        "else 'No' " +
                        "end as isAlumni, " +
                        "case " +
                        "when exists(select 1 from placement_student p where p.sid = s.id) then 'Placed' " +
                        "else 'Unplaced' " +
                        "end as is_placed " +
                        "from students s join domains d " +
                        "on s.domain = d.id " +
                        "left join placement_student ps " +
                        "on ps.sid = s.id " +
                        "left join placement p " +
                        "on p.id = ps.place_Id " +
                        "left join alumni a " +
                        "on a.id = s.id " +
                        "left join alumni_org ao " +
                        "on a.id = ao.alumni_id " +
                        "where " +
                        "(lower(p.org) like concat('%',lower(:keyword),'%')) or " +
                        "(lower(ao.org) like concat('%',lower(:keyword),'%')) or " +
                        "(s.graduation_year = :keyword) or " +
                        "(lower(d.program) like concat('%',lower(:keyword),'%')) " +
                        "order by s.first_name", nativeQuery = true)
        public List<Object[]> showStudentsByKeyword(@Param("keyword") String keyword);
}
