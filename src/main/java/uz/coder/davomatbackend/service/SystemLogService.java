package uz.coder.davomatbackend.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uz.coder.davomatbackend.db.SystemLogDatabase;
import uz.coder.davomatbackend.db.model.SystemLogDbModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemLogService {
    
    private final SystemLogDatabase systemLogDatabase;
    
    /**
     * Log an action asynchronously (non-blocking)
     */
    @Async
    public void logAction(String level, String username, String action, String message) {
        try {
            SystemLogDbModel logEntry = new SystemLogDbModel(
                LocalDateTime.now(),
                level,
                username,
                action,
                message
            );
            systemLogDatabase.save(logEntry);
        } catch (Exception e) {
            log.error("Failed to save system log: {}", e.getMessage());
        }
    }
    
    /**
     * Log with full details including HTTP request info
     */
    @Async
    public void logRequest(String level, String username, String action, String message,
                          HttpServletRequest request, Integer statusCode, Long duration) {
        try {
            SystemLogDbModel logEntry = new SystemLogDbModel();
            logEntry.setTimestamp(LocalDateTime.now());
            logEntry.setLevel(level);
            logEntry.setUsername(username);
            logEntry.setAction(action);
            logEntry.setMessage(message);
            logEntry.setIpAddress(getClientIp(request));
            logEntry.setUserAgent(request.getHeader("User-Agent"));
            logEntry.setEndpoint(request.getRequestURI());
            logEntry.setMethod(request.getMethod());
            logEntry.setStatusCode(statusCode);
            logEntry.setDuration(duration);
            
            systemLogDatabase.save(logEntry);
        } catch (Exception e) {
            log.error("Failed to save system log: {}", e.getMessage());
        }
    }
    
    /**
     * Log an error with stack trace
     */
    @Async
    public void logError(String username, String action, String message, Exception exception) {
        try {
            SystemLogDbModel logEntry = new SystemLogDbModel();
            logEntry.setTimestamp(LocalDateTime.now());
            logEntry.setLevel("ERROR");
            logEntry.setUsername(username);
            logEntry.setAction(action);
            logEntry.setMessage(message);
            logEntry.setStackTrace(getStackTrace(exception));
            
            systemLogDatabase.save(logEntry);
        } catch (Exception e) {
            log.error("Failed to save error log: {}", e.getMessage());
        }
    }
    
    /**
     * Get all logs with pagination
     */
    public Page<SystemLogDbModel> getAllLogs(Pageable pageable) {
        return systemLogDatabase.findAll(pageable);
    }
    
    /**
     * Get logs by level
     */
    public Page<SystemLogDbModel> getLogsByLevel(String level, Pageable pageable) {
        return systemLogDatabase.findByLevelOrderByTimestampDesc(level, pageable);
    }
    
    /**
     * Get logs by username
     */
    public Page<SystemLogDbModel> getLogsByUsername(String username, Pageable pageable) {
        return systemLogDatabase.findByUsernameOrderByTimestampDesc(username, pageable);
    }
    
    /**
     * Get logs by action
     */
    public Page<SystemLogDbModel> getLogsByAction(String action, Pageable pageable) {
        return systemLogDatabase.findByActionOrderByTimestampDesc(action, pageable);
    }
    
    /**
     * Search logs by keyword
     */
    public Page<SystemLogDbModel> searchLogs(String keyword, Pageable pageable) {
        return systemLogDatabase.findByMessageContainingIgnoreCaseOrderByTimestampDesc(keyword, pageable);
    }
    
    /**
     * Get logs by date range
     */
    public Page<SystemLogDbModel> getLogsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return systemLogDatabase.findByTimestampBetweenOrderByTimestampDesc(start, end, pageable);
    }
    
    /**
     * Get error logs
     */
    public Page<SystemLogDbModel> getErrorLogs(Pageable pageable) {
        return systemLogDatabase.findByLevelInOrderByTimestampDesc(
            java.util.List.of("ERROR", "FATAL"), pageable
        );
    }
    
    /**
     * Clean up old logs (older than specified days)
     */
    @Transactional
    public void cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        systemLogDatabase.deleteOldLogs(cutoffDate);
        log.info("Cleaned up logs older than {} days", daysToKeep);
    }
    
    /**
     * Delete a single log by ID
     */
    @Transactional
    public void deleteLog(Long id) {
        systemLogDatabase.deleteById(id);
        log.info("Deleted log with ID: {}", id);
    }
    
    /**
     * Delete multiple logs by IDs
     */
    @Transactional
    public void deleteLogs(java.util.List<Long> ids) {
        systemLogDatabase.deleteAllById(ids);
        log.info("Deleted {} logs", ids.size());
    }
    
    /**
     * Delete all logs
     */
    @Transactional
    public void deleteAllLogs() {
        long count = systemLogDatabase.count();
        systemLogDatabase.deleteAll();
        log.info("Deleted all {} logs", count);
    }
    
    /**
     * Get log statistics
     */
    public java.util.Map<String, Long> getLogStatistics() {
        return java.util.Map.of(
            "total", systemLogDatabase.count(),
            "info", systemLogDatabase.countByLevel("INFO"),
            "warn", systemLogDatabase.countByLevel("WARN"),
            "error", systemLogDatabase.countByLevel("ERROR"),
            "recentErrors", systemLogDatabase.countRecentErrors(LocalDateTime.now().minusHours(24))
        );
    }
    
    // Helper methods
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    private String getStackTrace(Exception exception) {
        if (exception == null) return null;
        
        StringBuilder sb = new StringBuilder();
        sb.append(exception.getClass().getName()).append(": ").append(exception.getMessage()).append("\n");
        
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
            if (sb.length() > 5000) break; // Limit stack trace size
        }
        
        return sb.toString();
    }
}
