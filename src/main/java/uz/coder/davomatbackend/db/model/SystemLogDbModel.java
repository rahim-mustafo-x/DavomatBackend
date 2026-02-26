package uz.coder.davomatbackend.db.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "system_logs", indexes = {
    @Index(name = "idx_log_level", columnList = "level"),
    @Index(name = "idx_log_timestamp", columnList = "timestamp"),
    @Index(name = "idx_log_user", columnList = "username")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemLogDbModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false, length = 20)
    private String level; // INFO, WARN, ERROR, DEBUG
    
    @Column(length = 100)
    private String username; // User who triggered the action
    
    @Column(nullable = false, length = 100)
    private String action; // LOGIN, LOGOUT, CREATE_COURSE, etc.
    
    @Column(length = 500)
    private String message;
    
    @Column(length = 100)
    private String ipAddress;
    
    @Column(length = 200)
    private String userAgent;
    
    @Column(length = 100)
    private String endpoint; // API endpoint called
    
    @Column(length = 10)
    private String method; // GET, POST, PUT, DELETE
    
    @Column
    private Integer statusCode; // HTTP status code
    
    @Column
    private Long duration; // Request duration in ms
    
    @Column(columnDefinition = "TEXT")
    private String stackTrace; // For errors
    
    public SystemLogDbModel(LocalDateTime timestamp, String level, String username, 
                           String action, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.username = username;
        this.action = action;
        this.message = message;
    }
}
