package uz.coder.davomatbackend.db;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import uz.coder.davomatbackend.db.model.StudentDbModel;

@Repository
public interface StudentDatabase extends JpaRepository<StudentDbModel, Long> {
    @Modifying
    @Transactional
    @Query("update StudentDbModel s set s.phoneNumber=:phoneNumber, s.groupId=:groupId, s.userId=:userId where s.id=:id")
    void update(@Param("id") long id, @Param("phoneNumber") String phoneNumber, @Param("userId") long userId, @Param("groupId") long groupId);

    @RestResource(path = "by-group", rel = "by-group")
    @Query("select s from StudentDbModel s where s.groupId=:groupId")
    List<StudentDbModel> findAllByGroupId(@Param("groupId") long groupId);

    @RestResource(path = "by-group-paged", rel = "by-group-paged")
    @Query("select s from StudentDbModel s where s.groupId=:groupId")
    Page<StudentDbModel> findAllByGroupId(@Param("groupId") long groupId, Pageable pageable);

    @Query("""
    select s from StudentDbModel s
    where s.groupId in (
        select g.id from GroupDbModel g
        where g.courseId in (
            select c.id from CourseDbModel c where c.userId = :userId
        )
    )
""")
    List<StudentDbModel> findAllStudentsByOwnerUserId(@Param("userId") long userId);

    @Query(value = "select s from StudentDbModel s where s.userId=:userId and s.groupId=:groupId")
    StudentDbModel findStudentsByUserIdAndGroupId(@Param("userId") Long userId,
                                                        @Param("groupId") Long groupId);
}
