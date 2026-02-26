package uz.coder.davomatbackend.db;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import uz.coder.davomatbackend.db.model.AttendanceDbModel;

@Repository
public interface AttendanceDatabase extends JpaRepository<AttendanceDbModel, Long> {

    @Query("SELECT a FROM AttendanceDbModel a WHERE a.studentId = :studentId AND a.date = :date")
    Optional<AttendanceDbModel> findByStudentIdAndDate(@Param("studentId") Long studentId,
                                                       @Param("date") LocalDate date);

    @RestResource(path = "by-student", rel = "by-student")
    @Query("SELECT a FROM AttendanceDbModel a WHERE a.studentId = :studentId")
    List<AttendanceDbModel> findAllByStudentId(@Param("studentId") Long studentId);

    @RestResource(path = "by-student-paged", rel = "by-student-paged")
    @Query("SELECT a FROM AttendanceDbModel a WHERE a.studentId = :studentId")
    Page<AttendanceDbModel> findAllByStudentId(@Param("studentId") Long studentId, Pageable pageable);

    @Query("""
    select a from AttendanceDbModel a
    where a.studentId in (
        select s.id from StudentDbModel s
        where s.groupId in (
            select g.id from GroupDbModel g
            where (:groupId is null or g.id = :groupId)
            and g.courseId in (
                select c.id from CourseDbModel c
                where (:courseId is null or c.id = :courseId)
                and c.userId = :userId
                and exists (
                    select u.id from UserDbModel u
                    where u.id = :userId
                    and u.role = 'ROLE_TEACHER'
                )
            )
        )
    )
""")
    List<AttendanceDbModel> findAllByTeacherAndOptionalCourseAndGroup(
            @Param("userId") long userId,
            @Param("courseId") Long courseId,
            @Param("groupId") Long groupId
    );
}