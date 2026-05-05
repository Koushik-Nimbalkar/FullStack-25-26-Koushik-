package com.jobportal.config;

import com.jobportal.model.Role;
import com.jobportal.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String target = userRepository.findByEmail(authentication.getName())
                .map(u -> switch (u.getRole()) {
                    case STUDENT -> "/student/dashboard";
                    case EMPLOYER -> "/employer/dashboard";
                    case ADMIN -> "/admin/dashboard";
                })
                .orElse("/");
        getRedirectStrategy().sendRedirect(request, response, request.getContextPath() + target);
    }
}
