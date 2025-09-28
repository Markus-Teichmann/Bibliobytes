package com.bibliobytes.backend.auth;

import com.bibliobytes.backend.auth.dtos.AccessTokenDto;
import com.bibliobytes.backend.auth.services.jwe.Jwe;
import com.bibliobytes.backend.auth.services.jwe.JweService;
import com.bibliobytes.backend.users.entities.Role;
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
import java.util.UUID;

@AllArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JweService jweService;

    private boolean jwtHeader(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            return false;
        }
        return authHeader.startsWith("Bearer ");
    }

    private Jwe getValidToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Jwe jwe = jweService.parse(token);
        if (jwe == null || jwe.isExpired()) {
            return null;
        }
        return jwe;
    }

    private UsernamePasswordAuthenticationToken generateAuthentication(UUID id, Role role) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.name());
        return new UsernamePasswordAuthenticationToken(id.toString(), null, List.of(authority));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Kein Header angegeben.
        if (!jwtHeader(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        //Kein Valider Token gefunden.
        Jwe jwe = getValidToken(request);
        if (jwe == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //Kein passendes DTO hinterlegt.
        AccessTokenDto dto = jwe.toDto();
        if (dto == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Authentication setzen.
        var authentication = generateAuthentication(dto.getId(), dto.getRole());
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
