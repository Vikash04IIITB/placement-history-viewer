package com.vikash.esd_final_project.helper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestInterceptorSecure implements HandlerInterceptor {
    private final JWTHelperSecure jwtHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Allow OPTIONS requests for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        try {
            String authorizationHeader = request.getHeader("Authorization");

            // Check if Authorization header is present and properly formatted
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header from IP: {}", getClientIpAddress(request));
                setUnauthorizedResponse(response, "Missing or invalid Authorization header");
                return false;
            }

            // Extract token from header
            String token = authorizationHeader.substring(7).trim();

            if (token.isEmpty()) {
                log.warn("Empty JWT token from IP: {}", getClientIpAddress(request));
                setUnauthorizedResponse(response, "Empty JWT token");
                return false;
            }

            // Validate token
            if (!jwtHelper.validateToken(token)) {
                log.warn("Invalid JWT token from IP: {}", getClientIpAddress(request));
                setUnauthorizedResponse(response, "Invalid or expired JWT token");
                return false;
            }

            // Extract username and add to request attributes for downstream use
            String username = jwtHelper.extractUsername(token);
            request.setAttribute("username", username);
            request.setAttribute("jwtToken", token);

            log.debug("Valid JWT token for user: {} from IP: {}", username, getClientIpAddress(request));
            return true;

        } catch (Exception e) {
            log.error("Error processing JWT token from IP: {}", getClientIpAddress(request), e);
            setUnauthorizedResponse(response, "Token processing error");
            return false;
        }
    }

    /**
     * Set unauthorized response with proper headers
     */
    private void setUnauthorizedResponse(HttpServletResponse response, String message) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setHeader("WWW-Authenticate", "Bearer");
        try {
            response.getWriter().write(String.format(
                    "{\"error\":\"Unauthorized\",\"message\":\"%s\",\"status\":401}",
                    message));
        } catch (Exception e) {
            log.error("Error writing unauthorized response", e);
        }
    }

    /**
     * Get client IP address considering proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
