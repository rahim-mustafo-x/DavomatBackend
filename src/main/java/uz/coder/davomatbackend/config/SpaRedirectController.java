package uz.coder.davomatbackend.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaRedirectController {

    /**
     * Forward all non-API routes to index.html for React Router
     * This allows React to handle client-side routing
     */
    @GetMapping(value = {
            "/",
            "/login",
            "/admin/**",
            "/teacher/**",
            "/student/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
