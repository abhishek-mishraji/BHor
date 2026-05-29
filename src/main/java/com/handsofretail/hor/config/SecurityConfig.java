package com.handsofretail.hor.config;

import com.handsofretail.hor.security.handler.AccessDeniedHandlerImpl;
import com.handsofretail.hor.security.jwt.JwtAuthenticationEntryPoint;
import com.handsofretail.hor.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
        private final AccessDeniedHandlerImpl accessDeniedHandlerImpl;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)
                        throws Exception {

                http
                                .csrf(csrf -> csrf.disable())
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                                .accessDeniedHandler(accessDeniedHandlerImpl))
                                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                .requestMatchers(

                                                                "/api/v1/auth/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui",
                                                                "/swagger-ui.html",
                                                                "/api-docs/**",
                                                                "/h2-console/**")
                                                .permitAll()

                                                .requestMatchers("/api/v1/admin/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers("/api/v1/client/**")
                                                .hasRole("CLIENT")

                                                .anyRequest()
                                                .authenticated())

                                .formLogin(form -> form.disable())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}