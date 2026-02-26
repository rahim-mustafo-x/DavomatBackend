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
import uz.coder.davomatbackend.db.model.GroupDbModel;

@Repository
public interface GroupDatabase extends JpaRepository<GroupDbModel, Long> {
    @Modifying
    @Transactional
    @Query("update GroupDbModel g set g.title=:title, g.courseId=:courseId where g.id=:id")
    void update(@Param("id") long id, @Param("title") String title, @Param("courseId") long courseId);

    @RestResource(path = "by-course", rel = "by-course")
    @Query("select g from GroupDbModel g where g.courseId=:courseId")
    List<GroupDbModel> findAllByCourseId(@Param("courseId") long courseId);

    @RestResource(path = "by-course-paged", rel = "by-course-paged")
    @Query("select g from GroupDbModel g where g.courseId=:courseId")
    Page<GroupDbModel> findAllByCourseId(@Param("courseId") long courseId, Pageable pageable);

    @Query("""
        select g from GroupDbModel g
        where g.courseId in :courseIds
    """)
    List<GroupDbModel> findGroupsByCourseIds(@Param("courseIds") List<Long> courseIds);
}
