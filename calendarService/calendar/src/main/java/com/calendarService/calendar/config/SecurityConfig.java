package com.calendarService.calendar.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class SecurityConfig {
        private Boolean otpAuthenticated = false;
        private Boolean hasRole = false;

        //Map the roles so that it can be parsed.
        @Bean
        JwtAuthenticationConverter jwtAuthenticationConverter() {
                JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

                converter.setJwtGrantedAuthoritiesConverter(jwt -> {
                        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

                        if (realmAccess == null) {
                                return List.of();
                        }

                        Object rolesObject = realmAccess.get("roles");

                        if (!(rolesObject instanceof List<?>)) {
                                return List.of();
                        }

                        List<?> roles = (List<?>) rolesObject;

                        return roles.stream()
                                        .map(role -> new SimpleGrantedAuthority(
                                                        "ROLE_" + role.toString()))
                                        .collect(Collectors.toList());
                });

                return converter;
        }

        //Check if role is present and authenticated using OTP else handle the error and send response.
        @Bean
        AccessDeniedHandler accessDeniedHandler() {

                return (request, response, accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                        Map<String, Object> body = new HashMap<>();

                        body.put("status", 403);
                        if (otpAuthenticated == false && hasRole == true) {
                                body.put(
                                                "message",
                                                "You are not authorized to view this resource as you have MFA disabled");
                        } else {
                                body.put(
                                                "message",
                                                "You are not authorized to view this resource");
                        }
                        new ObjectMapper()
                                        .writeValue(response.getOutputStream(), body);
                };
        }

        //Build filter chain to force the resource server to check for JWT.
        @Bean
        SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        JwtAuthenticationConverter jwtAuthenticationConverter,
                        AccessDeniedHandler accessDeniedHandler)
                        throws Exception {

                return http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> {
                                })
                                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                                .authorizeHttpRequests(requests -> requests
                                                .requestMatchers("/h2-console/**").permitAll()
                                                .requestMatchers("/calendar")
                                                .access((authentication, context) -> {

                                                        Authentication auth = authentication.get();

                                                        // Make sure the authentication is JWT authentication.
                                                        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
                                                                return new AuthorizationDecision(false);
                                                        }
                                                        Jwt jwt = jwtAuth.getToken();
                                                        /*
                                                         * Get acr from JWT.
                                                         *
                                                         * IF Password login: then acr = "password"
                                                         *
                                                         * IF OTP login: then acr = "otp"
                                                         */
                                                        String acr = jwt.getClaimAsString("acr");
                                                        /*
                                                         * Check that OTP was completed.
                                                         */
                                                        otpAuthenticated = "otp".equals(acr);

                                                        // Check that the user has the required role.
                                                        hasRole = auth.getAuthorities()
                                                                        .stream()
                                                                        .anyMatch(authority -> authority
                                                                                        .getAuthority()
                                                                                        .equals("ROLE_my-role"));
                                                        /*
                                                         * User must satisfy BOTH conditions:
                                                         * 1. Has my-role
                                                         * 2. Completed OTP
                                                         */
                                                        return new AuthorizationDecision(
                                                                        otpAuthenticated && hasRole);
                                                })
                                                .anyRequest()
                                                .authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(
                                                jwtAuthenticationConverter)))
                                .exceptionHandling(exception -> exception.accessDeniedHandler(
                                                accessDeniedHandler))
                                .build();
        }
}
