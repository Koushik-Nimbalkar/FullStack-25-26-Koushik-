package com.jobportal.web;

import com.jobportal.exception.BadRequestException;
import com.jobportal.service.UserService;
import com.jobportal.web.dto.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;
    private final com.jobportal.service.EmailService emailService;

    public AuthController(UserService userService, com.jobportal.service.EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerForm") RegisterForm form,
            BindingResult bindingResult,
            jakarta.servlet.http.HttpSession session
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        
        // Generate a 6-digit OTP
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));
        session.setAttribute("pendingUser", form);
        session.setAttribute("registrationOtp", otp);
        
        try {
            emailService.sendOtpEmail(form.getEmail(), otp);
        } catch (Exception e) {
            bindingResult.reject("registerError", "Could not send OTP email. Please check your email configuration.");
            return "register";
        }
        
        return "redirect:/register/otp";
    }

    @GetMapping("/register/otp")
    public String otpForm(jakarta.servlet.http.HttpSession session, Model model) {
        if (session.getAttribute("pendingUser") == null) {
            return "redirect:/register";
        }
        return "register-otp";
    }

    @PostMapping("/register/otp")
    public String verifyOtp(
            @org.springframework.web.bind.annotation.RequestParam("otp") String otp,
            jakarta.servlet.http.HttpSession session,
            Model model
    ) {
        RegisterForm form = (RegisterForm) session.getAttribute("pendingUser");
        String sessionOtp = (String) session.getAttribute("registrationOtp");
        
        if (form == null || sessionOtp == null) {
            return "redirect:/register";
        }
        
        if (!sessionOtp.equals(otp)) {
            model.addAttribute("error", "Invalid OTP. Please try again.");
            return "register-otp";
        }
        
        try {
            userService.register(form);
            session.removeAttribute("pendingUser");
            session.removeAttribute("registrationOtp");
        } catch (BadRequestException e) {
            model.addAttribute("error", e.getMessage());
            return "register-otp";
        }
        
        return "redirect:/login?registered";
    }
}
