package uz.coder.davomatbackend.db;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uz.coder.davomatbackend.db.model.SystemLogDbModel;

@Repository
public interface SystemLogDatabase extends JpaRepository<SystemLogDbModel, Long> {
    
    // Find by level
    Page<SystemLogDbModel> findByLevelOrderByTimestampDesc(String level, Pageable pageable);
    
    // Find by username
    Page<SystemLogDbModel> findByUsernameOrderByTimestampDesc(String username, Pageable pageable);
    
    // Find by action
    Page<SystemLogDbModel> findByActionOrderByTimestampDesc(String action, Pageable pageable);
    
    // Find by date range
    Page<SystemLogDbModel> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime start, LocalDateTime end, Pageable pageable
    );
    
    // Find errors
    Page<SystemLogDbModel> findByLevelInOrderByTimestampDesc(
        java.util.List<String> levels, Pageable pageable
    );
    
    // Search in message
    Page<SystemLogDbModel> findByMessageContainingIgnoreCaseOrderByTimestampDesc(
        String keyword, Pageable pageable
    );
    
    // Delete old logs (cleanup)
    @Modifying
    @Query("DELETE FROM SystemLogDbModel s WHERE s.timestamp < :cutoffDate")
    void deleteOldLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Count by level
    long countByLevel(String level);
    
    // Count errors in last 24 hours
    @Query("SELECT COUNT(s) FROM SystemLogDbModel s WHERE s.level = 'ERROR' AND s.timestamp > :since")
    long countRecentErrors(@Param("since") LocalDateTime since);
}
