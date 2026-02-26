package uz.coder.davomatbackend.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import uz.coder.davomatbackend.service.SystemLogService;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final SystemLogService systemLogService;

    @Around("execution(* uz.coder.davomatbackend.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        String methodName = joinPoint.getSignature().toShortString();
        String username = getCurrentUsername();
        String ipAddress = getClientIpAddress();
        HttpServletRequest request = getCurrentRequest();
        
        logger.info("→ Request: {} | User: {} | IP: {}", methodName, username, ipAddress);
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("← Response: {} | Time: {}ms | Status: SUCCESS", methodName, executionTime);
            
            // Save to database
            if (request != null) {
                systemLogService.logRequest(
                    "INFO",
                    username,
                    methodName,
                    "Request completed successfully",
                    request,
                    200,
                    executionTime
                );
            }
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("← Response: {} | Time: {}ms | Status: ERROR | Message: {}", 
                methodName, executionTime, e.getMessage());
            
            // Save error to database
            systemLogService.logError(
                username,
                methodName,
                "Request failed: " + e.getMessage(),
                e
            );
            
            throw e;
        }
    }

    @AfterThrowing(pointcut = "execution(* uz.coder.davomatbackend.service..*(..))", throwing = "ex")
    public void logServiceExceptions(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().toShortString();
        String username = getCurrentUsername();
        logger.error("Service Exception in {}: {}", methodName, ex.getMessage(), ex);
        
        // Save service exception to database
        systemLogService.logError(
            username,
            methodName,
            "Service exception: " + ex.getMessage(),
            (Exception) ex
        );
    }

    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // Ignore
        }
        return "anonymous";
    }

    private String getClientIpAddress() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                    return xForwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // Ignore
        }
        return "unknown";
    }
    
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
