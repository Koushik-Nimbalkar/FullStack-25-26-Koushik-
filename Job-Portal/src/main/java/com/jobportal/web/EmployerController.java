package com.jobportal.web;

import com.jobportal.exception.BadRequestException;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.service.JobApplicationService;
import com.jobportal.service.JobPostingService;
import com.jobportal.service.UserService;
import com.jobportal.web.dto.JobForm;
import com.jobportal.web.dto.ProfileForm;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    private final UserService userService;
    private final JobPostingService jobPostingService;
    private final JobApplicationService jobApplicationService;

    public EmployerController(
            UserService userService,
            JobPostingService jobPostingService,
            JobApplicationService jobApplicationService
    ) {
        this.userService = userService;
        this.jobPostingService = jobPostingService;
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails user, Model model) {
        var u = userService.requireByEmail(user.getUsername());
        var jobs = jobPostingService.listForEmployer(user.getUsername());
        model.addAttribute("user", u);
        model.addAttribute("jobs", jobs);
        model.addAttribute("jobCount", jobs.size());
        model.addAttribute("applicationCount", jobApplicationService.countApplicationsForEmployer(u.getId()));
        model.addAttribute("shortlistCount", jobApplicationService.countShortlistedForEmployer(u.getId()));
        return "employer/dashboard";
    }

    @GetMapping("/jobs/new")
    public String newJobForm(Model model) {
        JobForm form = new JobForm();
        form.setMinExperienceYears(0);
        model.addAttribute("jobForm", form);
        return "employer/job-form";
    }

    @PostMapping("/jobs/new")
    public String createJob(
            @AuthenticationPrincipal UserDetails user,
            @Valid @ModelAttribute("jobForm") JobForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "employer/job-form";
        }
        try {
            jobPostingService.create(user.getUsername(), form);
        } catch (BadRequestException e) {
            bindingResult.reject("jobError", e.getMessage());
            return "employer/job-form";
        }
        return "redirect:/employer/dashboard?created";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@AuthenticationPrincipal UserDetails user, @PathVariable Long id, Model model) {
        var job = jobPostingService.getForEmployer(id, user.getUsername());
        JobForm form = new JobForm();
        form.setTitle(job.getTitle());
        form.setDescription(job.getDescription());
        form.setSkillsRequired(job.getSkillsRequired());
        form.setSalaryMin(job.getSalaryMin());
        form.setSalaryMax(job.getSalaryMax());
        form.setLocation(job.getLocation());
        form.setCategory(job.getCategory());
        form.setMinExperienceYears(job.getMinExperienceYears());
        model.addAttribute("jobForm", form);
        model.addAttribute("jobId", id);
        return "employer/job-form";
    }

    @PostMapping("/jobs/{id}/edit")
    public String updateJob(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @Valid @ModelAttribute("jobForm") JobForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("jobId", id);
            return "employer/job-form";
        }
        try {
            jobPostingService.update(user.getUsername(), id, form);
        } catch (BadRequestException e) {
            bindingResult.reject("jobError", e.getMessage());
            model.addAttribute("jobId", id);
            return "employer/job-form";
        }
        return "redirect:/employer/dashboard?updated";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            jobPostingService.delete(user.getUsername(), id);
            redirectAttributes.addFlashAttribute("message", "Job removed from active listings.");
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employer/dashboard";
    }

    @GetMapping("/jobs/{id}/applications")
    public String applications(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute("job", jobPostingService.getForEmployer(id, user.getUsername()));
        model.addAttribute("applications", jobApplicationService.listForJob(id, user.getUsername()));
        return "employer/applications";
    }

    @PostMapping("/applications/{applicationId}/status")
    public String updateApplicationStatus(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status,
            @RequestParam Long jobId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            jobApplicationService.updateStatus(applicationId, status, user.getUsername());
            redirectAttributes.addFlashAttribute("message",
                    status == ApplicationStatus.SHORTLISTED ? "Candidate shortlisted." : "Application updated.");
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employer/jobs/" + jobId + "/applications";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails user, Model model) {
        var u = userService.requireByEmail(user.getUsername());
        ProfileForm form = new ProfileForm();
        form.setFullName(u.getFullName());
        form.setPhone(u.getPhone());
        form.setCompanyName(u.getCompanyName());
        model.addAttribute("profileForm", form);
        model.addAttribute("user", u);
        return "employer/profile";
    }

    @PostMapping("/profile")
    public String saveProfile(
            @AuthenticationPrincipal UserDetails user,
            @Valid @ModelAttribute("profileForm") ProfileForm form,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userService.requireByEmail(user.getUsername()));
            return "employer/profile";
        }
        userService.updateProfile(user.getUsername(), form.getFullName(), form.getPhone(), form.getCompanyName());
        return "redirect:/employer/profile?saved";
    }
}
