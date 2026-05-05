package com.jobportal.service;

import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.model.Role;
import com.jobportal.repository.UserRepository;
import com.jobportal.web.dto.RegisterForm;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public User register(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("This email is already registered. Try logging in.");
        }
        if (form.getRole() == Role.EMPLOYER && (form.getCompanyName() == null || form.getCompanyName().isBlank())) {
            throw new BadRequestException("Company name is required for employers.");
        }
        User user = new User();
        user.setEmail(form.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setFullName(form.getFullName().trim());
        user.setPhone(form.getPhone() != null ? form.getPhone().trim() : null);
        user.setRole(form.getRole());
        user.setCompanyName(form.getCompanyName() != null ? form.getCompanyName().trim() : null);
        return userRepository.save(user);
    }

    public User requireByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found."));
    }

    @Transactional
    public User updateProfile(String email, String fullName, String phone, String companyName) {
        User user = requireByEmail(email);
        user.setFullName(fullName.trim());
        user.setPhone(phone != null ? phone.trim() : null);
        if (user.getRole() == Role.EMPLOYER) {
            user.setCompanyName(companyName != null ? companyName.trim() : null);
        }
        return userRepository.save(user);
    }

    @Transactional
    public void uploadResume(String email, MultipartFile file) throws IOException {
        User user = requireByEmail(email);
        if (user.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students can upload a resume.");
        }
        String path = fileStorageService.storeResume(file, user.getId());
        user.setResumePath(path);
        userRepository.save(user);
    }
}
