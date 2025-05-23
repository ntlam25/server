package com.example.crabfood_api.service.auth;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.crabfood_api.security.CustomUserDetails;
import com.example.crabfood_api.security.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Lấy header Authorization từ request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Kiểm tra xem header Authorization có tồn tại và bắt đầu bằng "Bearer " không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Trích xuất token từ header (bỏ qua "Bearer ")
        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(new ObjectMapper().writeValueAsString(
                    Map.of(
                            "status", HttpServletResponse.SC_UNAUTHORIZED,
                            "error", "JWT expired",
                            "message", ex.getMessage(),
                            "timestamp", LocalDateTime.now().toString()
                    )
            ));
            return;
        }
        // Trích xuất email từ token JWT

        // Nếu email tồn tại và chưa có authentication trong SecurityContext
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Lấy thông tin user từ database
            CustomUserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Kiểm tra tính hợp lệ của token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Tạo authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Thêm thông tin request vào authentication token
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Lưu authentication token vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Chuyển request đến filter tiếp theo trong chain
        filterChain.doFilter(request, response);
    }
}