package uz.coder.davomatbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uz.coder.davomatbackend.model.*;
import uz.coder.davomatbackend.service.UserService;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return service.findByEmail(username);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Integer>> deleteMe() {
        try {
            User currentUser = getCurrentUser();
            int data = service.deleteById(currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe() {
        try {
            User currentUser = getCurrentUser();
            UserResponse userResponse = new UserResponse(
                    currentUser.getId(),
                    currentUser.getFirstName(),
                    currentUser.getLastName(),
                    currentUser.getEmail(),
                    currentUser.getPassword(),
                    currentUser.getPhoneNumber(),
                    currentUser.getRole(),
                    currentUser.getPayedDate()
            );
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<UserResponse>> updateMe(@RequestBody UpdateUser updateUser) {
        try {
            User currentUser = getCurrentUser();
            User user = new User();
            user.setFirstName(updateUser.getFirstName());
            user.setLastName(updateUser.getLastName());
            user.setEmail(updateUser.getEmail());
            user.setPassword(updateUser.getPassword());
            user.setPhoneNumber(updateUser.getPhoneNumber());
            user.setId(currentUser.getId());
            user.setRole(currentUser.getRole());
            User updatedUser = service.edit(user);
            UserResponse userResponse = new UserResponse(
                    updatedUser.getId(),
                    updatedUser.getFirstName(),
                    updatedUser.getLastName(),
                    updatedUser.getEmail(),
                    updatedUser.getPassword(),
                    updatedUser.getPhoneNumber(),
                    updatedUser.getRole(),
                    updatedUser.getPayedDate()
            );
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/find-by-phone-number/{phoneNumber}")
    public ResponseEntity<ApiResponse<UserResponse>> findByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            User result = service.findByPhoneNumber(phoneNumber);
            UserResponse userResponse = new UserResponse(
                    result.getId(),
                    result.getFirstName(),
                    result.getLastName(),
                    result.getEmail(),
                    result.getPassword(),
                    result.getPhoneNumber(),
                    result.getRole(),
                    result.getPayedDate()
            );
            return ResponseEntity.ok(ApiResponse.success(userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound(e.getMessage()));
        }
    }
}
