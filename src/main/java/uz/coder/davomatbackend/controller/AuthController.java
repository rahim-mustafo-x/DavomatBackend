package uz.coder.davomatbackend.controller;

import java.time.LocalDate;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uz.coder.davomatbackend.jwt.JwtService;
import uz.coder.davomatbackend.model.AddUser;
import uz.coder.davomatbackend.model.LoginRequest;
import uz.coder.davomatbackend.model.LoginResponse;
import uz.coder.davomatbackend.model.Response;
import uz.coder.davomatbackend.model.User;
import uz.coder.davomatbackend.model.UserResponse;
import uz.coder.davomatbackend.service.UserService;
import static uz.coder.davomatbackend.todo.Strings.ROLE_STUDENT;
import static uz.coder.davomatbackend.todo.Strings.THIS_PHONE_NUMBER_TAKEN;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService service;

    public AuthController(
            AuthenticationManager authenticationManager,
            @Lazy JwtService jwtService,
            UserService service
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.service = service;
    }

    // 🔐 LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = (User) userDetails;

        String token = jwtService.generateToken(userDetails);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                null, // Don't send password
                user.getPhoneNumber(),
                user.getRole(),
                user.getPayedDate()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginResponse(token, 200, "Login successful", userResponse));
    }

    // 📝 REGISTER
    @PostMapping("/register")
    public ResponseEntity<Response<UserResponse>> register(@RequestBody AddUser addUser) {
        try {
            User user = new User();
            user.setFirstName(addUser.getFirstName());
            user.setLastName(addUser.getLastName());
            user.setEmail(addUser.getEmail());
            user.setPassword(addUser.getPassword());
            user.setRole(addUser.getRole());
            user.setPhoneNumber(addUser.getPhoneNumber());
            User userByNumberOfPhone = service.findByPhoneNumber(user.getPhoneNumber());

            if (userByNumberOfPhone != null) {

                if (userByNumberOfPhone.getEmail() == null) {

                    userByNumberOfPhone.setLastName(user.getLastName());
                    userByNumberOfPhone.setFirstName(user.getFirstName());
                    userByNumberOfPhone.setEmail(user.getEmail());
                    userByNumberOfPhone.setRole(ROLE_STUDENT);

                    if (user.getPassword() != null && !user.getPassword().isBlank()) {
                        userByNumberOfPhone.setPassword(user.getPassword());
                    }

                    userByNumberOfPhone.setPayedDate(
                            LocalDate.now().plusWeeks(1)
                    );

                    User edit = service.edit(userByNumberOfPhone);

                    return ResponseEntity.ok(
                            new Response<>(200, new UserResponse(edit.getId(), edit.getFirstName(), edit.getLastName(), edit.getEmail(), edit.getPassword(), edit.getPhoneNumber(), edit.getRole(), edit.getPayedDate()))
                    );

                } else {
                    return ResponseEntity.ok(
                            new Response<>(500, THIS_PHONE_NUMBER_TAKEN)
                    );
                }

            } else {
                User save = service.save(user);

                return ResponseEntity.ok(
                        new Response<>(200, new UserResponse(save.getId(), save.getFirstName(), save.getLastName(), save.getEmail(), save.getPassword(),  save.getPhoneNumber(), save.getRole(), save.getPayedDate()))
                );
            }

        } catch (Exception e) {
            return ResponseEntity.ok(
                    new Response<>(500, e.getMessage())
            );
        }
    }
}