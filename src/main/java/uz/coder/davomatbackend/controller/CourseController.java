package uz.coder.davomatbackend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

import lombok.RequiredArgsConstructor;
import uz.coder.davomatbackend.model.AddCourse;
import uz.coder.davomatbackend.model.ApiResponse;
import uz.coder.davomatbackend.model.Course;
import uz.coder.davomatbackend.model.PageResponse;
import uz.coder.davomatbackend.model.UpdateCourse;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.CourseService;
import uz.coder.davomatbackend.service.UserService;
import uz.coder.davomatbackend.service.WebSocketNotificationService;
import static uz.coder.davomatbackend.todo.Strings.ROLE_ADMIN;
import static uz.coder.davomatbackend.todo.Strings.ROLE_TEACHER;
import static uz.coder.davomatbackend.todo.Strings.YOU_ARE_A_STUDENT;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;
    private final UserService userService;
    private final WebSocketNotificationService notificationService;

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Integer>> deleteById(@PathVariable long id) {
        try {
            User user = getCurrentUser();
            if (user.getRole().equals(ROLE_ADMIN) || user.getRole().equals(ROLE_TEACHER)) {
                int data = service.deleteById(id);
                notificationService.notifyCourseUpdated(user.getUsername(), "Course deleted");
                return ResponseEntity.ok(ApiResponse.success("Course deleted successfully", data));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden(YOU_ARE_A_STUDENT));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> findById(@PathVariable long id) {
        try {
            Course course = service.findById(id);
            return ResponseEntity.ok(ApiResponse.success(course));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Course not found"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Course>> create(@RequestBody AddCourse addCourse) {
        try {
            Course course = new Course();
            User user = getCurrentUser();
            course.setTitle(addCourse.getTitle());
            course.setDescription(addCourse.getDescription());
            course.setUserId(user.getId());
            if (user.getRole().equals(ROLE_ADMIN) || user.getRole().equals(ROLE_TEACHER)) {
                Course save = service.save(course);
                notificationService.notifyCourseUpdated(user.getUsername(), "New course created: " + save.getTitle());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.created(save));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden(YOU_ARE_A_STUDENT));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Course>> update(@RequestBody UpdateCourse updateCourse) {
        try {
            Course course = new Course();
            course.setTitle(updateCourse.getTitle());
            course.setDescription(updateCourse.getDescription());
            course.setId(updateCourse.getId());
            User user = getCurrentUser();
            course.setUserId(user.getId());
            if (user.getRole().equals(ROLE_ADMIN) || user.getRole().equals(ROLE_TEACHER)) {
                Course edit = service.edit(course);
                notificationService.notifyCourseUpdated(user.getUsername(), "Course updated: " + edit.getTitle());
                return ResponseEntity.ok(ApiResponse.success("Course updated successfully", edit));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.forbidden(YOU_ARE_A_STUDENT));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/getAllCourses")
    public ResponseEntity<ApiResponse<PageResponse<Course>>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = getCurrentUser();
            Pageable pageable = PageRequest.of(page, size);
            Page<Course> coursePage = service.findAllPaginated(user.getId(), pageable);
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(coursePage)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}