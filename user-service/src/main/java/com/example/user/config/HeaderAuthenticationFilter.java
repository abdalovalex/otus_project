package com.example.user.config;

import java.io.IOException;

import com.example.user.dto.UserDto;
import com.example.user.entity.Role;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class HeaderAuthenticationFilter extends OncePerRequestFilter {
    private static final String X_USERNAME = "X-USERNAME";
    private static final String X_USER_ID = "X-USER-ID";
    private static final String X_ROLE = "X-ROLE";

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String userIdHeader = request.getHeader(X_USER_ID);

        if (StringUtils.hasText(userIdHeader) && SecurityContextHolder.getContext().getAuthentication() == null) {
            Integer userId = Integer.parseInt(userIdHeader);
            String usernameHeader = request.getHeader(X_USERNAME);

            // Если пользователя еще нет, то создаем его
            if (!userRepository.existsById(userId)) {
                UserDto userDto = new UserDto();
                userDto.setId(userId);
                userDto.setUsername(request.getHeader(X_USERNAME));

                try {
                    userService.createUser(userDto);
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new Exception("Пользователь не найден"));
                    user.setRole(Role.CUSTOMER.name()
                                         .equals(request.getHeader(X_ROLE)) ? Role.CUSTOMER : Role.SUPPLIER);
                    userRepository.save(user);
                } catch (Exception e) {
                    filterChain.doFilter(request, response);
                }
            }

            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(usernameHeader);

            SecurityContext context = SecurityContextHolder.createEmptyContext();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
        }
        filterChain.doFilter(request, response);
    }
}
