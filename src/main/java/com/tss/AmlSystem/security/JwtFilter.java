package com.tss.AmlSystem.security;

import com.tss.AmlSystem.config.multitenancy.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@NoArgsConstructor
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                // 1. Extract the Schema Name and Username
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                String schemaName = jwtUtils.getSchemaFromJwtToken(jwt);

                // 2. Set the TenantContext so Hibernate knows which schema to use
                // This must happen before any DB calls
                TenantContext.setCurrentTenant(schemaName);

                // 3. Set standard Spring Security Authentication
                // We leave authorities as null for now; we will handle Roles in Step 5
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, null);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Silently failing as requested (no logger)
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 4. VERY IMPORTANT: Clear the context after the request finishes.
            // This prevents the schema name from leaking to other threads in the pool.
            TenantContext.clear();
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
