package uz.coder.davomatbackend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
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
import uz.coder.davomatbackend.model.AddStudent;
import uz.coder.davomatbackend.model.ApiResponse;
import uz.coder.davomatbackend.model.Balance;
import uz.coder.davomatbackend.model.PageResponse;
import uz.coder.davomatbackend.model.Student;
import uz.coder.davomatbackend.model.StudentCourseGroup;
import uz.coder.davomatbackend.model.TelegramUser;
import uz.coder.davomatbackend.model.UpdateStudent;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.StudentService;
import uz.coder.davomatbackend.service.TelegramUserService;
import uz.coder.davomatbackend.service.UserService;
import uz.coder.davomatbackend.service.WebSocketNotificationService;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;
    private final UserService userService;
    private final TelegramUserService telegramUserService;
    private final WebSocketNotificationService notificationService;

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @PostMapping("/addStudent")
    public ResponseEntity<ApiResponse<Student>> addStudent(@RequestBody AddStudent addStudent) {
        try {
            Student student = new Student();
            student.setFullName(addStudent.getFullName());
            student.setPhoneNumber(addStudent.getPhoneNumber());
            student.setGroupId(addStudent.getGroupId());
            Student saved = service.save(student);
            
            try {
                User user = getCurrentUser();
                notificationService.notifyStudentAdded(user.getUsername(), "New student added: " + saved.getFullName());
            } catch (Exception e) {
                // Log but don't fail the request if notification fails
            }
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(saved));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PutMapping("/editStudent")
    public ResponseEntity<ApiResponse<Student>> editStudent(@RequestBody UpdateStudent updateStudent) {
        try {
            Student student = new Student();
            student.setFullName(updateStudent.getFullName());
            student.setPhoneNumber(updateStudent.getPhoneNumber());
            student.setGroupId(updateStudent.getGroupId());
            student.setId(updateStudent.getId());
            Student updated = service.edit(student);
            
            try {
                User user = getCurrentUser();
                notificationService.notifyStudentAdded(user.getUsername(), "Student updated: " + updated.getFullName());
            } catch (Exception e) {
                // Log but don't fail the request if notification fails
            }
            
            return ResponseEntity.ok(ApiResponse.success("Student updated successfully", updated));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/deleteStudent/{id}")
    public ResponseEntity<ApiResponse<Integer>> deleteStudent(@PathVariable long id) {
        try {
            int result = service.deleteById(id);
            if (result > 0) {
                return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", result));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Student not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Student>> findById(@PathVariable long id) {
        try {
            Student student = service.findById(id);
            return ResponseEntity.ok(ApiResponse.success(student));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Student not found"));
        }
    }

    @GetMapping("/findByGroupId/{groupId}")
    public ResponseEntity<ApiResponse<PageResponse<Student>>> findByGroupId(
            @PathVariable long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Student> studentPage = service.findAllStudentByGroupIdPaginated(groupId, pageable);
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(studentPage)));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping(value = "/upload-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadExcel(@RequestParam MultipartFile file) {
        try {
            User user = getCurrentUser();
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.badRequest("Only .xlsx format files are allowed"));
            }

            boolean result = service.saveAllByExcel(file, user.getId());
            if (result) {
                notificationService.notifyStudentAdded(user.getUsername(), "Students imported from Excel");
                return ResponseEntity.ok(ApiResponse.success("File saved successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("An error occurred"));
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error: " + ex.getMessage()));
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportXlsx() throws IOException {
        User user = getCurrentUser();
        List<Student> students = service.getStudentsByUserId(user.getId());
        byte[] fileBytes = service.exportStudentsToXlsx(students);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("students_user_" + user.getId() + ".xlsx").build());

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/seeCourses")
    public ResponseEntity<ApiResponse<List<StudentCourseGroup>>> findAllCourses() {
        try {
            User user = getCurrentUser();
            List<StudentCourseGroup> result = service.getCourseAndGroupByUserId(user.getId());
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.forbidden(ex.getMessage()));
        }
    }

    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Balance>> getUserBalanceByTelegram() {
        try {
            User user = getCurrentUser();
            TelegramUser telegramUserServiceByUserId = telegramUserService.findByUserId(user.getId());
            Balance balance = service.getUserBalanceByTelegramUserId(telegramUserServiceByUserId.getTelegramUserId());
            return ResponseEntity.ok(ApiResponse.success(balance));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(ex.getMessage()));
        }
    }

    @GetMapping("/findByGroupIdAndUserId")
    public ResponseEntity<ApiResponse<Student>> findByGroupIdAndUserId(@RequestParam long groupId) {
        try {
            User user = getCurrentUser();
            Student student = service.findByGroupIdAndUserId(user.getId(), groupId);
            return ResponseEntity.ok(ApiResponse.success(student));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }
    }
}
