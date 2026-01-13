package fr.elikia.backend.security.jwt;

import fr.elikia.backend.bo.LogicResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor responsible for:
 * - Checking the presence of JWT tokens
 * - Validating token integrity and expiration
 * - Verifying user roles when required

 * This interceptor is triggered before controller method execution.
 */
@Component
public class JwtAuthRequestInterceptor implements HandlerInterceptor {
    // Service responsible for JWT operations
    private final JwtService jwtService;

    /**
     * Constructor injection
     *
     * @param jwtService JWT service
     */
    public JwtAuthRequestInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Executed before controller method invocation.
     *
     * @param request  incoming HTTP request
     * @param response HTTP response
     * @param handler  target handler (controller method)
     * @return true if request can continue, false otherwise
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        // Ignore non-controller handlers (e.g. static resources)
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // Check if JWT authentication is required on method or controller class
        boolean jwtRequired =
                handlerMethod.hasMethodAnnotation(RequiredJWTAuth.class)
                        || handlerMethod.getBeanType().isAnnotationPresent(RequiredJWTAuth.class);

        // If JWT is not required, allow request
        if (!jwtRequired) {
            return true;
        }

        // Retrieve Authorization header
        String authHeader = request.getHeader("Authorization");

        // If missing or malformed, deny access
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            JsonResponseUtil.sendJson(
                    response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    new LogicResult<>("401", "Authorization header missing", null)
            );
            return false;
        }

        // Extract token without "Bearer "
        String token = authHeader.substring(7);

        // Validate token integrity and expiration
        LogicResult<Boolean> result = jwtService.verifyToken(token);
        if (!"204".equals(result.getCode())) {
            JsonResponseUtil.sendJson(
                    response,
                    HttpServletResponse.SC_FORBIDDEN,
                    result
            );
            return false;
        }


        // ============================
        // ROLE CHECK
        // ============================

        // Look for role requirement on method
        RequiredRole requiredRole =
                handlerMethod.getMethodAnnotation(RequiredRole.class);

        // If not found on method, check controller class
        if (requiredRole == null) {
            requiredRole = handlerMethod.getBeanType()
                    .getAnnotation(RequiredRole.class);
        }

        // If a role is required, validate it
        if (requiredRole != null) {
            String roleFromToken = jwtService.extractRole(token);

            // If role mismatch, deny access
            if (roleFromToken == null || !roleFromToken.equals(requiredRole.value())) {
                JsonResponseUtil.sendJson(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        new LogicResult<>("403", "Access denied", null)
                );
                return false;
            }
        }

        // All checks passed
        return true;
    }

}