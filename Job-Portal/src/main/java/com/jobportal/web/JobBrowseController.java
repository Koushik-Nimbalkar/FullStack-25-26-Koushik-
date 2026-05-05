package com.jobportal.web;

import com.jobportal.model.JobCategory;
import com.jobportal.service.JobPostingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JobBrowseController {

    private final JobPostingService jobPostingService;

    public JobBrowseController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping("/jobs")
    public String jobs(
            Model model,
            @RequestParam(required = false) JobCategory category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxExperienceYears
    ) {
        model.addAttribute("jobs", jobPostingService.searchPublic(category, location, maxExperienceYears));
        model.addAttribute("categories", JobCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedLocation", location != null ? location : "");
        model.addAttribute("selectedMaxExp", maxExperienceYears);
        return "jobs";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, Model model) {
        model.addAttribute("job", jobPostingService.getActive(id));
        return "job-detail";
    }
}
