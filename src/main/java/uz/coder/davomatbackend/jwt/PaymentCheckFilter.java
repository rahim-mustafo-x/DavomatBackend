package uz.coder.davomatbackend.jwt;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import uz.coder.davomatbackend.model.ApiResponse;
import uz.coder.davomatbackend.model.User;
import static uz.coder.davomatbackend.todo.Strings.ROLE_ADMIN;

@Component
public class PaymentCheckFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip payment check for public endpoints
        String path = request.getRequestURI();
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If user is authenticated
        if (authentication != null && authentication.isAuthenticated() 
                && authentication.getPrincipal() instanceof User) {
            
            User user = (User) authentication.getPrincipal();

            // Admin doesn't need to pay
            if (ROLE_ADMIN.equals(user.getRole())) {
                filterChain.doFilter(request, response);
                return;
            }

            // Check payment for TEACHER and STUDENT
            LocalDate payedDate = user.getPayedDate();
            LocalDate today = LocalDate.now();

            if (payedDate == null || payedDate.isBefore(today)) {
                // Payment expired or not set
                response.setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED); // 402
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                ApiResponse<String> apiResponse = ApiResponse.error(
                        "To'lov muddati tugagan. Iltimos, to'lovni yangilang. " +
                        "Payment expired. Please renew your subscription."
                );

                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/websocket-test.html") ||
               path.startsWith("/index.html") ||
               path.startsWith("/api/contact/") ||
               path.equals("/") ||
               path.startsWith("/assets/") ||
               path.startsWith("/favicon.ico");
    }
}
