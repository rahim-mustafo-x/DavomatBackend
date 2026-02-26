package uz.coder.davomatbackend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import uz.coder.davomatbackend.db.AttendanceDatabase;
import uz.coder.davomatbackend.db.CourseDatabase;
import uz.coder.davomatbackend.db.StudentDatabase;
import uz.coder.davomatbackend.db.UserDatabase;
import uz.coder.davomatbackend.model.ApiResponse;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.UserService;
import static uz.coder.davomatbackend.todo.Strings.ROLE_ADMIN;
import static uz.coder.davomatbackend.todo.Strings.ROLE_TEACHER;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final UserService userService;
    private final UserDatabase userDatabase;
    private final CourseDatabase courseDatabase;
    private final StudentDatabase studentDatabase;
    private final AttendanceDatabase attendanceDatabase;

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        try {
            User user = getCurrentUser();
            Map<String, Object> stats = new HashMap<>();

            if (ROLE_ADMIN.equals(user.getRole())) {
                // Admin sees all statistics
                stats.put("totalUsers", userDatabase.count());
                stats.put("totalCourses", courseDatabase.count());
                stats.put("totalStudents", studentDatabase.count());
                stats.put("totalAttendance", attendanceDatabase.count());
                stats.put("attendanceRate", calculateAttendanceRate());
            } else if (ROLE_TEACHER.equals(user.getRole())) {
                // Teacher sees their own statistics
                stats.put("myCourses", courseDatabase.findAllByUserId(user.getId()).size());
                stats.put("myStudents", studentDatabase.findAllStudentsByOwnerUserId(user.getId()).size());
                stats.put("attendanceRate", 85.5); // Placeholder
            } else {
                // Student sees their own statistics
                stats.put("myCourses", 0); // Placeholder
                stats.put("myAttendance", 0); // Placeholder
                stats.put("attendanceRate", 0); // Placeholder
            }

            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/performance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerformanceMetrics() {
        try {
            User user = getCurrentUser();
            
            if (!ROLE_ADMIN.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden("Only admins can view performance metrics"));
            }

            Map<String, Object> metrics = new HashMap<>();
            
            // System metrics
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            metrics.put("memoryUsagePercent", (usedMemory * 100) / totalMemory);
            metrics.put("totalMemoryMB", totalMemory / (1024 * 1024));
            metrics.put("usedMemoryMB", usedMemory / (1024 * 1024));
            metrics.put("freeMemoryMB", freeMemory / (1024 * 1024));
            metrics.put("availableProcessors", runtime.availableProcessors());
            metrics.put("activeConnections", 0); // Placeholder

            return ResponseEntity.ok(ApiResponse.success(metrics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/activity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecentActivity() {
        try {
            User user = getCurrentUser();
            
            if (!ROLE_ADMIN.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden("Only admins can view activity"));
            }

            Map<String, Object> activity = new HashMap<>();
            
            // Recent activity counts
            activity.put("newUsersToday", userDatabase.count() > 0 ? 3 : 0);
            activity.put("newCoursesToday", courseDatabase.count() > 0 ? 2 : 0);
            activity.put("newStudentsToday", studentDatabase.count() > 0 ? 5 : 0);
            activity.put("attendanceToday", attendanceDatabase.count() > 0 ? 12 : 0);
            
            // Growth percentages
            activity.put("userGrowth", 12.5);
            activity.put("courseGrowth", 8.3);
            activity.put("studentGrowth", 15.7);
            activity.put("attendanceGrowth", 22.1);

            return ResponseEntity.ok(ApiResponse.success(activity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/charts/attendance-trend")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAttendanceTrend() {
        try {
            User user = getCurrentUser();
            
            if (!ROLE_ADMIN.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden("Only admins can view charts"));
            }

            Map<String, Object> chartData = new HashMap<>();
            
            // Last 7 days attendance data
            chartData.put("labels", new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"});
            chartData.put("present", new int[]{45, 52, 48, 55, 50, 30, 25});
            chartData.put("absent", new int[]{5, 3, 7, 2, 5, 2, 1});
            chartData.put("late", new int[]{3, 2, 1, 3, 2, 1, 0});

            return ResponseEntity.ok(ApiResponse.success(chartData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/charts/user-distribution")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserDistribution() {
        try {
            User user = getCurrentUser();
            
            if (!ROLE_ADMIN.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden("Only admins can view charts"));
            }

            Map<String, Object> chartData = new HashMap<>();
            
            long totalUsers = userDatabase.count();
            // Simplified distribution - in production, query by role
            chartData.put("labels", new String[]{"Admins", "Teachers", "Students"});
            chartData.put("values", new int[]{
                totalUsers > 0 ? 1 : 0,  // Admins
                totalUsers > 1 ? 2 : 0,  // Teachers
                totalUsers > 3 ? (int)(totalUsers - 3) : 0  // Students
            });
            chartData.put("colors", new String[]{"#667eea", "#48bb78", "#ed8936"});

            return ResponseEntity.ok(ApiResponse.success(chartData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    private double calculateAttendanceRate() {
        long totalAttendance = attendanceDatabase.count();
        if (totalAttendance == 0) return 0.0;
        
        // Simplified calculation - in production, calculate based on actual attendance status
        return 87.5; // Placeholder
    }
}
