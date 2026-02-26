package uz.coder.davomatbackend.controller;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import uz.coder.davomatbackend.model.AddAttendance;
import uz.coder.davomatbackend.model.ApiResponse;
import uz.coder.davomatbackend.model.Attendance;
import uz.coder.davomatbackend.model.PageResponse;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.AttendanceService;
import uz.coder.davomatbackend.service.UserService;
import uz.coder.davomatbackend.service.WebSocketNotificationService;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserService userService;
    private final WebSocketNotificationService notificationService;

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Attendance>> save(@RequestBody AddAttendance addAttendance) {
        try {
            Attendance attendance = new Attendance();
            attendance.setDate(addAttendance.getDate());
            attendance.setStudentId(addAttendance.getStudentId());
            attendance.setStatus(addAttendance.getStatus());
            Attendance saved = attendanceService.save(attendance);
            
            // Get student's user to send notification
            try {
                User currentUser = getCurrentUser();
                notificationService.notifyAttendanceUpdated(currentUser.getUsername(), "Attendance recorded for student ID: " + addAttendance.getStudentId());
            } catch (Exception e) {
                // Log but don't fail the request if notification fails
            }
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(saved));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest(ex.getMessage()));
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<ApiResponse<Attendance>> update(@RequestBody Attendance attendance) {
        try {
            Attendance updated = attendanceService.update(attendance);
            
            try {
                User currentUser = getCurrentUser();
                notificationService.notifyAttendanceUpdated(currentUser.getUsername(), "Attendance updated for student ID: " + attendance.getStudentId());
            } catch (Exception e) {
                // Log but don't fail the request if notification fails
            }
            
            return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", updated));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ex.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable long id) {
        try {
            boolean deleted = attendanceService.delete(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Deleted successfully", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Attendance not found"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Attendance>> findById(@PathVariable long id) {
        try {
            Attendance attendance = attendanceService.findById(id);
            return ResponseEntity.ok(ApiResponse.success(attendance));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Attendance not found"));
        }
    }

    @PostMapping("/excel")
    public ResponseEntity<ApiResponse<String>> importExcel(@RequestParam MultipartFile file) {
        try {
            boolean result = attendanceService.saveAllByExcel(file);
            if (result) {
                return ResponseEntity.ok(ApiResponse.success("Imported successfully", null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.badRequest("Import failed"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAttendance(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long groupId) {

        try {
            User user = getCurrentUser();
            long userId = user.getId();
            
            if (year < 2000 || year > 2100) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid year. Must be between 2000-2100".getBytes());
            }
            if (month < 1 || month > 12) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid month. Must be between 1-12".getBytes());
            }

            byte[] data = attendanceService.exportToExcelByMonth(userId, courseId, groupId, year, month);
            String filename = String.format("attendance_%d_%d_%d.xlsx", userId, year, month);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("File creation failed: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Export failed: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<PageResponse<Attendance>>> getByStudent(
            @PathVariable long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Attendance> attendancePage = attendanceService.getAllByStudentIdPaginated(studentId, pageable);
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(attendancePage)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}