package uz.coder.davomatbackend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoginResponse {
    private String token;
    private int code;
    private String message;
    private UserResponse user;
    
    public LoginResponse(String token, int code, String message) {
        this.token = token;
        this.code = code;
        this.message = message;
    }
}
