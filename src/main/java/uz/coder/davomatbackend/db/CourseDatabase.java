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
import uz.coder.davomatbackend.db.model.CourseDbModel;

@Repository
public interface CourseDatabase extends JpaRepository<CourseDbModel, Long> {
    @Transactional
    @Modifying
    @Query("update CourseDbModel c set c.title=:title, c.description=:description, c.userId=:userId where c.id=:id")
    void update(@Param("id") long id, @Param("title") String title, @Param("description") String description, @Param("userId") long userId);

    @RestResource(path = "by-user", rel = "by-user")
    @Query("select c from CourseDbModel c where c.userId=:userId")
    List<CourseDbModel> findAllByUserId(@Param("userId") long userId);

    @RestResource(path = "by-user-paged", rel = "by-user-paged")
    @Query("select c from CourseDbModel c where c.userId=:userId")
    Page<CourseDbModel> findAllByUserId(@Param("userId") long userId, Pageable pageable);

    @Query("""
    select c
    from StudentDbModel s
    join GroupDbModel g on s.groupId = g.id
    join CourseDbModel c on g.courseId = c.id
    where s.userId = :userId
""")
    List<CourseDbModel> findAllByStudentId(@Param("userId") long userId);
}
