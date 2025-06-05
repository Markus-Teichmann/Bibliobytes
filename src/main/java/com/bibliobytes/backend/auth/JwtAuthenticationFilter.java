package com.bibliobytes.backend.auth;

import com.bibliobytes.backend.users.entities.Role;
import com.bibliobytes.backend.auth.services.JweService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JweService jweService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Kein Header angegeben.
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token abgelaufen oder nicht angegeben.
        var token = authHeader.replace("Bearer ", "");

        var jwt = jweService.parse(token);

        if (jwt == null || jwt.isExpired()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authentication setzen.
        var authentication = new UsernamePasswordAuthenticationToken(
                jwt.getSubject(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + jwt.get("role", Role.class)))
        );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
