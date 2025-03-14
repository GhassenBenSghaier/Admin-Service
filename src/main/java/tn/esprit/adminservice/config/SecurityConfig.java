//package tn.esprit.adminservice.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Collections;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import tn.esprit.adminservice.entity.User;
//import tn.esprit.adminservice.repository.UserRepository;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final UserRepository userRepository;
//    private final JwtAuthorizationFilter jwtAuthorizationFilter;
//
//    @Autowired
//    public SecurityConfig(UserRepository userRepository) {
//        this.userRepository = userRepository;
//        this.jwtAuthorizationFilter = new JwtAuthorizationFilter(userRepository); // Instantiate with dependency
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/login").permitAll()
//                        .requestMatchers("/api/admin/**").authenticated()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthorizationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    private static class JwtAuthorizationFilter extends OncePerRequestFilter {
//
//        private final UserRepository userRepository;
//        private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
//
//        public JwtAuthorizationFilter(UserRepository userRepository) {
//            this.userRepository = userRepository;
//        }
//
//        @Override
//        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//                throws IOException, jakarta.servlet.ServletException {
//            String authUser = request.getHeader("X-Authenticated-User");
//            logger.debug("X-Authenticated-User header: {}", authUser);
//            if (authUser != null) {
//                User user = userRepository.findByUsernameIgnoreCase(authUser);
//                if (user != null) {
//                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
//                    var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
//                            authUser, null, authorities);
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    logger.debug("Authenticated user: {}, authorities: {}", authUser, authorities);
//                } else {
//                    logger.warn("User not found for username: {}", authUser);
//                }
//            } else {
//                logger.warn("X-Authenticated-User header is null");
//            }
//            chain.doFilter(request, response);
//        }
//    }
//}

package tn.esprit.adminservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tn.esprit.adminservice.entity.User;
import tn.esprit.adminservice.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.jwtAuthorizationFilter = new JwtAuthorizationFilter(userRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthorizationFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static class JwtAuthorizationFilter extends OncePerRequestFilter {
        private final UserRepository userRepository;
        private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

        public JwtAuthorizationFilter(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                throws IOException, jakarta.servlet.ServletException {
            String authUser = request.getHeader("X-Authenticated-User");
            logger.debug("X-Authenticated-User header: {}", authUser);
            if (authUser != null) {
                User user = userRepository.findByUsernameIgnoreCase(authUser);
                if (user != null) {
                    var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
                    var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            authUser, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Authenticated user: {}, authorities: {}", authUser, authorities);
                } else {
                    logger.warn("User not found for username: {}", authUser);
                }
            } else {
                logger.warn("X-Authenticated-User header is null");
            }
            chain.doFilter(request, response);
        }
    }
}