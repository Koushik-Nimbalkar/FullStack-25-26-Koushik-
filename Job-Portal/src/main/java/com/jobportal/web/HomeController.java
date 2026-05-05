package com.jobportal.web;

import com.jobportal.service.JobPostingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final JobPostingService jobPostingService;

    public HomeController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("jobs", jobPostingService.listActive().stream().limit(8).toList());
        return "index";
    }
}
