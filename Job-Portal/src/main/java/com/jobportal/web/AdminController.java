package com.jobportal.web;

import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobPostingRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public AdminController(
            UserRepository userRepository,
            JobPostingRepository jobPostingRepository,
            JobApplicationRepository jobApplicationRepository
    ) {
        this.userRepository = userRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("jobCount", jobPostingRepository.count());
        model.addAttribute("applicationCount", jobApplicationRepository.count());
        return "admin/dashboard";
    }
}
