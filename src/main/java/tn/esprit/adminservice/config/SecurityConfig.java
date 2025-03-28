package tn.esprit.adminservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/admin/users").hasAnyAuthority(
                                "VIEW_USERS_CENTRAL_ADMIN", "VIEW_USERS_LOCAL_ADMIN", "VIEW_USERS_TEACHER", "VIEW_USERS_STUDENT")
                        .requestMatchers(HttpMethod.POST, "/admin/users").hasAnyAuthority(
                                "ADD_USER_CENTRAL_ADMIN", "ADD_USER_LOCAL_ADMIN", "ADD_USER_TEACHER", "ADD_USER_STUDENT")
                        .requestMatchers(HttpMethod.PUT, "/admin/users/**").hasAnyAuthority(
                                "EDIT_USER_CENTRAL_ADMIN", "EDIT_USER_LOCAL_ADMIN", "EDIT_USER_TEACHER", "EDIT_USER_STUDENT")
                        .requestMatchers(HttpMethod.DELETE, "/admin/users/**").hasAnyAuthority(
                                "DELETE_USER_CENTRAL_ADMIN", "DELETE_USER_LOCAL_ADMIN", "DELETE_USER_TEACHER", "DELETE_USER_STUDENT")
                        .requestMatchers(HttpMethod.GET, "/admin/profiles").hasAuthority("VIEW_PROFILES")
                        .requestMatchers(HttpMethod.POST, "/admin/profiles").hasAuthority("CREATE_PROFILE")
                        .requestMatchers(HttpMethod.PUT, "/admin/profiles/**").hasAuthority("EDIT_PROFILE")
                        .requestMatchers(HttpMethod.DELETE, "/admin/profiles/**").hasAuthority("DELETE_PROFILE")
                        .requestMatchers(HttpMethod.GET, "/admin/permissions").hasAuthority("VIEW_PERMISSIONS")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter();
    }

    public static class JwtAuthorizationFilter extends OncePerRequestFilter {
        private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

        @Value("${jwt.secret}")
        private String secretKeyBase64;

        private byte[] secretKeyBytes;

        @PostConstruct
        public void init() {
            if (secretKeyBase64 == null) {
                throw new IllegalStateException("JWT secret key is not configured");
            }
            this.secretKeyBytes = Base64.getDecoder().decode(secretKeyBase64);
            logger.info("JwtAuthorizationFilter initialized with secret key");
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, jakarta.servlet.ServletException {
            String header = request.getHeader("Authorization");
            logger.debug("Authorization header: {}", header);

            String path = request.getRequestURI();
            logger.debug("Request URI: {}", path);
            if ("/api/auth/login".equals(path) || "/api/auth/register".equals(path)) {
                logger.debug("Bypassing JWT filter for: {}", path);
                chain.doFilter(request, response);
                return;
            }

            if (header == null || !header.startsWith("Bearer ")) {
                logger.warn("No Bearer token found in Authorization header");
                chain.doFilter(request, response);
                return;
            }

            String token = header.replace("Bearer ", "");
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secretKeyBytes)
                        .parseClaimsJws(token)
                        .getBody();
                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                String profileCode = claims.get("profileCode", String.class);
                List<String> permissions = claims.get("permissions", List.class) != null ?
                        claims.get("permissions", List.class) : Collections.emptyList();

                logger.debug("JWT Validation - Username: {}, Role: {}, ProfileCode: {}, Permissions: {}",
                        username, role, profileCode, permissions);

                if (username != null && role != null) {
                    // Combine role and permissions as authorities
                    var authorities = permissions.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    if (profileCode != null) {
                        authorities.add(new SimpleGrantedAuthority("PROFILE_" + profileCode));
                    }

                    var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Authenticated user: {}, authorities: {}", username, authorities);
                } else {
                    logger.warn("Username or role missing in JWT claims");
                }
            } catch (Exception e) {
                logger.error("JWT Validation Failed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }

            chain.doFilter(request, response);
        }
    }
}