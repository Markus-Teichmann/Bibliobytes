package com.bibliobytes.backend.auth.config;

import com.bibliobytes.backend.auth.JwtAuthenticationFilter;
import com.bibliobytes.backend.users.entities.Role;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(c ->
                c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ).csrf(c -> c.disable())
            .authorizeHttpRequests(c -> c
                .requestMatchers(HttpMethod.POST,"/users/register").permitAll()
                .requestMatchers(HttpMethod.POST,"/users/register/confirm").permitAll()
                .requestMatchers(HttpMethod.POST,"/users/login").permitAll()
                .requestMatchers(HttpMethod.POST,"/users/refresh").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/users/{id}").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/{id}/firstname").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/{id}/lastname").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/{id}/email").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/{id}/password").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/users/{id}/donations").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/{id}/donations").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/users/{id}/rentals").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/users/search").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/users/new").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/{id}/role").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE,"/users/{id}").hasRole(Role.ADMIN.name())

                .requestMatchers(HttpMethod.GET,"/me").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/me/firstname").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/me/lastname").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST,"/me/email").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/me/email").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST,"/me/password").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/me/password").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/me/donations").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/me/donations").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/me/rentals").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE,"/me").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/items").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/items/search").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/items/new").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/items/donate").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/items/{id}").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/title").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/place").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/topic").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/note").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/addTag").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/removeTag").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/author").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/publisher").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/isbn").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/runtime").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/label").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/production").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/addActor").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/removeActor").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/addLanguage").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/removeLanguage").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/addSubtitle").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/removeSubtitle").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/items/{id}/donate").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/items/{id}/rent").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/items/{id}/rent").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/items/{id}").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/rentals/{id}").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/rentals/{id}/status").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/rentals/{id}/external").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/rentals/{id}/end").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/donations/{id}").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/donations/{id}/status").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/donations/{id}/item").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/donations/{id}/owner").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/donations/{id}/condition").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())

                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(c ->
                {
                    c.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                    );
                    c.accessDeniedHandler((request, response, accessDeniedException) ->
                        response.setStatus(HttpStatus.FORBIDDEN.value())
                    );
                }
            );
        return http.build();
    }
}
/*
.requestMatchers(HttpMethod.GET,"/users").hasRole(Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/update/credentials").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/update/credentials/confirm").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/update/profile").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.PUT,"/users/update/role").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE,"/users").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/users/applicants").hasAnyRole(Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST,"/auth/refresh").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.GET,"/auth/me").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/items/donate").hasAnyRole(Role.USER.name(), Role.SERVICE.name(), Role.ADMIN.name())
 */
