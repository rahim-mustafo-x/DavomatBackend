package uz.coder.davomatbackend.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import uz.coder.davomatbackend.model.ApiResponse;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.LogService;
import uz.coder.davomatbackend.service.UserService;

import java.util.List;
import java.util.Map;

import static uz.coder.davomatbackend.todo.Strings.ROLE_ADMIN;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;
    private final UserService userService;

    public LogController(LogService logService, UserService userService) {
        this.logService = logService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByEmail(username);
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getRecentLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "all") String level) {
        try {
            User user = getCurrentUser();
            if (!ROLE_ADMIN.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden("Admin access required"));
            }

            // Limit max size to prevent memory issues
            int safeSize = Math.min(size, 100);
            Pageable pageable = PageRequest.of(page, safeSize);
            
            List<Map<String, String>> logs = logService.getRecentLogs(pageable, level);
            return ResponseEntity.ok(ApiResponse.success(logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/security")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getSecurityLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            User user = getCurrentUser();
            if (!ROLE_ADMIN.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden("Admin access required"));
            }

            int safeSize = Math.min(size, 100);
            Pageable pageable = PageRequest.of(page, safeSize);
            
            List<Map<String, String>> logs = logService.getSecurityLogs(pageable);
            return ResponseEntity.ok(ApiResponse.success(logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/errors")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getErrorLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            User user = getCurrentUser();
            if (!ROLE_ADMIN.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden("Admin access required"));
            }

            int safeSize = Math.min(size, 100);
            Pageable pageable = PageRequest.of(page, safeSize);
            
            List<Map<String, String>> logs = logService.getErrorLogs(pageable);
            return ResponseEntity.ok(ApiResponse.success(logs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearLogs(@RequestParam String type) {
        try {
            User user = getCurrentUser();
            if (!ROLE_ADMIN.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden("Admin access required"));
            }

            boolean success = logService.clearLogs(type);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("Logs cleared successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.badRequest("Failed to clear logs"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
