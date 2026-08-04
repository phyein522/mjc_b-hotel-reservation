package com.mjc.hotel.config;

import com.mjc.hotel.auth.jwt.JwtIllegalException;
import com.mjc.hotel.auth.jwt.JwtUtils;
import com.mjc.hotel.user.dto.UserDto;
import com.mjc.hotel.user.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class HotelAuthenticationFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        try {
            String jwtAccessToken = this.jwtUtils.resolveJwtTokenFromBearerToken(authHeader);
            if ( jwtAccessToken != null ) {
                String email = this.jwtUtils.getEmail(jwtAccessToken);
                UserDto find = this.userService.findByEmail(email);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        find, null, find.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (ExpiredJwtException e ) {
            log.error(e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            SecurityContextHolder.clearContext();
            return;
        } catch (JwtIllegalException | JwtException e) {
            log.error(e.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
