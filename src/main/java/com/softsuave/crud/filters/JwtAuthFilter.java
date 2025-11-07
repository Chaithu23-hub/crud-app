package com.softsuave.crud.filters;

import com.softsuave.crud.service.CustomUserDetailsService;
import com.softsuave.crud.utility.JWTutil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Memory Comment:
 * JwtAuthFilter is a custom filter that intercepts every incoming HTTP request.
 *
 * Purpose:
 * 1. Extract the JWT token from the "Authorization" header.
 * 2. Validate the token.
 * 3. Set the authentication object in Spring’s SecurityContext.
 *
 * OncePerRequestFilter → ensures the filter runs once per request.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    // ✅ Step 1: Logger setup for debugging and tracking
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JWTutil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Memory Comment:
     * doFilterInternal() runs for every HTTP request.
     * It checks if the Authorization header has a valid JWT token.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Get the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        //  Step 2: Check if the header starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7); // Remove "Bearer " prefix
            try {
                username = jwtUtil.extractUsername(jwtToken);
                logger.info("Extracted username '{}' from JWT token.", username);
            } catch (Exception e) {
                logger.error("Failed to extract username from token: {}", e.getMessage());
            }
        } else {
            logger.debug("No valid Authorization header found for request URI: {}", request.getRequestURI());
        }

        //  Step 3: If we got a username and authentication is not yet set, proceed to validate
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.debug("Loaded UserDetails for '{}'. Now validating token.", username);

            // Step 4: Validate the token against the username
            if (jwtUtil.validateToken(jwtToken, userDetails.getUsername())) {
                logger.info("JWT token for user '{}' is valid. Setting authentication context.", username);

                // Create the authentication object
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 🧠 Step 5: Set it into the security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.warn("JWT token for user '{}' is invalid or expired.", username);
            }
        }

        // 🧠 Step 6: Continue with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
