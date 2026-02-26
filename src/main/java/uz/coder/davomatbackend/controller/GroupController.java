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
import uz.coder.davomatbackend.model.AddGroup;
import uz.coder.davomatbackend.model.ApiResponse;
import uz.coder.davomatbackend.model.Group;
import uz.coder.davomatbackend.model.PageResponse;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.service.GroupService;
import uz.coder.davomatbackend.service.UserService;
import uz.coder.davomatbackend.service.WebSocketNotificationService;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {
    private final GroupService service;
    private final WebSocketNotificationService notificationService;
    private final UserService userService;

    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userService.findByEmail(userDetails.getUsername());
    }

    @PostMapping("/addGroup")
    public ResponseEntity<ApiResponse<Group>> addGroup(@RequestBody AddGroup addGroup) {
        try {
            Group group = new Group();
            group.setTitle(addGroup.getTitle());
            group.setCourseId(addGroup.getCourseId());
            Group saved = service.save(group);
            
            try {
                User currentUser = getCurrentUser();
                notificationService.notifyGroupUpdated(currentUser.getUsername(), "New group added: " + saved.getTitle());
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

    @PutMapping("/editGroup")
    public ResponseEntity<ApiResponse<Group>> editGroup(@RequestBody Group group) {
        try {
            Group updated = service.edit(group);
            
            try {
                User currentUser = getCurrentUser();
                notificationService.notifyGroupUpdated(currentUser.getUsername(), "Group updated: " + updated.getTitle());
            } catch (Exception e) {
                // Log but don't fail the request if notification fails
            }
            
            return ResponseEntity.ok(ApiResponse.success("Group updated successfully", updated));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }

    @DeleteMapping("/deleteGroup/{id}")
    public ResponseEntity<ApiResponse<Integer>> deleteGroup(@PathVariable long id) {
        try {
            int result = service.deleteById(id);
            if (result > 0) {
                return ResponseEntity.ok(ApiResponse.success("Group deleted successfully", result));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("Group not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Group>> findById(@PathVariable long id) {
        try {
            Group group = service.findById(id);
            return ResponseEntity.ok(ApiResponse.success(group));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Group not found"));
        }
    }

    @GetMapping("/findByCourseId/{courseId}")
    public ResponseEntity<ApiResponse<PageResponse<Group>>> findByCourseId(
            @PathVariable long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Group> groupPage = service.findAllGroupByCourseIdPaginated(courseId, pageable);
            return ResponseEntity.ok(ApiResponse.success(PageResponse.of(groupPage)));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(ex.getMessage()));
        }
    }
}
