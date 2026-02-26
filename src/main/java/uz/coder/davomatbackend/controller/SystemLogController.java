package uz.coder.davomatbackend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import uz.coder.davomatbackend.db.model.SystemLogDbModel;
import uz.coder.davomatbackend.model.PageResponse;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.service.SystemLogService;
import static uz.coder.davomatbackend.todo.Strings.ROLE_ADMIN;

@RestController
@RequestMapping("/api/system-logs")
@RequiredArgsConstructor
@Tag(name = "System Logs", description = "System logging and monitoring")
@PreAuthorize("hasAuthority('" + ROLE_ADMIN + "')")
public class SystemLogController {
    
    private final SystemLogService systemLogService;
    
    @GetMapping
    @Operation(summary = "Get all system logs (Admin only)")
    public ResponseEntity<PageResponse<SystemLogDbModel>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), 
            Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<SystemLogDbModel> logs = systemLogService.getAllLogs(pageable);
        
        return ResponseEntity.ok(PageResponse.of(logs));
    }
    
    @GetMapping("/level/{level}")
    @Operation(summary = "Get logs by level (INFO, WARN, ERROR)")
    public ResponseEntity<PageResponse<SystemLogDbModel>> getLogsByLevel(
            @PathVariable String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<SystemLogDbModel> logs = systemLogService.getLogsByLevel(level.toUpperCase(), pageable);
        
        return ResponseEntity.ok(PageResponse.of(logs));
    }
    
    @GetMapping("/user/{username}")
    @Operation(summary = "Get logs by username")
    public ResponseEntity<PageResponse<SystemLogDbModel>> getLogsByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<SystemLogDbModel> logs = systemLogService.getLogsByUsername(username, pageable);
        
        return ResponseEntity.ok(PageResponse.of(logs));
    }
    
    @GetMapping("/action/{action}")
    @Operation(summary = "Get logs by action")
    public ResponseEntity<PageResponse<SystemLogDbModel>> getLogsByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<SystemLogDbModel> logs = systemLogService.getLogsByAction(action, pageable);
        
        return ResponseEntity.ok(PageResponse.of(logs));
    }
    
    @GetMapping("/errors")
    @Operation(summary = "Get error logs only")
    public ResponseEntity<PageResponse<SystemLogDbModel>> getErrorLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<SystemLogDbModel> logs = systemLogService.getErrorLogs(pageable);
        
        return ResponseEntity.ok(PageResponse.of(logs));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search logs by keyword")
    public ResponseEntity<PageResponse<SystemLogDbModel>> searchLogs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
        Page<SystemLogDbModel> logs = systemLogService.searchLogs(keyword, pageable);
        
        return ResponseEntity.ok(PageResponse.of(logs));
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Get log statistics")
    public ResponseEntity<Response<java.util.Map<String, Long>>> getStatistics() {
        java.util.Map<String, Long> stats = systemLogService.getLogStatistics();
        return ResponseEntity.ok(new Response<>(200, stats, "Statistics retrieved successfully"));
    }
    
    @DeleteMapping("/cleanup")
    @Operation(summary = "Clean up old logs")
    public ResponseEntity<Response<Void>> cleanupOldLogs(
            @RequestParam(defaultValue = "90") int daysToKeep
    ) {
        systemLogService.cleanupOldLogs(daysToKeep);
        return ResponseEntity.ok(new Response<>(200, 
            "Logs older than " + daysToKeep + " days have been deleted"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a single log by ID")
    public ResponseEntity<Response<Void>> deleteLog(@PathVariable Long id) {
        systemLogService.deleteLog(id);
        return ResponseEntity.ok(new Response<>(200, "Log deleted successfully"));
    }
    
    @DeleteMapping("/bulk")
    @Operation(summary = "Delete multiple logs by IDs")
    public ResponseEntity<Response<Void>> deleteLogs(@RequestBody java.util.List<Long> ids) {
        systemLogService.deleteLogs(ids);
        return ResponseEntity.ok(new Response<>(200, 
            ids.size() + " log(s) deleted successfully"));
    }
    
    @DeleteMapping("/all")
    @Operation(summary = "Delete all logs")
    public ResponseEntity<Response<Void>> deleteAllLogs() {
        systemLogService.deleteAllLogs();
        return ResponseEntity.ok(new Response<>(200, "All logs deleted successfully"));
    }
}
