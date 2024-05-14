package org.example.purchaseservice.config;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class HeaderAuthenticationFilter extends OncePerRequestFilter {
    private static final String X_USER_ID = "X-USER-ID";
    private static final String X_ROLE = "X-ROLE";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String userIdHeader = request.getHeader(X_USER_ID);

        if (StringUtils.hasText(userIdHeader) && SecurityContextHolder.getContext().getAuthentication() == null) {
            Integer userId = Integer.parseInt(userIdHeader);
            String roleHeader = request.getHeader(X_ROLE);

            List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(roleHeader);
            PreAuthenticatedAuthenticationToken authentication =
                    new PreAuthenticatedAuthenticationToken(new Principal(userId), null, authorityList);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    @Getter
    @AllArgsConstructor
    public static class Principal {
        private Integer id;
    }
}
