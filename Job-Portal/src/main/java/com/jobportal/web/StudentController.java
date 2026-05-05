package com.jobportal.web;

import com.jobportal.exception.BadRequestException;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.service.JobApplicationService;
import com.jobportal.service.UserService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final JobApplicationService jobApplicationService;
    public StudentController(
            UserService userService,
            JobApplicationService jobApplicationService
    ) {
        this.userService = userService;
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails user, Model model) {
        var u = userService.requireByEmail(user.getUsername());
        model.addAttribute("user", u);
        var apps = jobApplicationService.listForStudent(user.getUsername());
        model.addAttribute("applications", apps);
        model.addAttribute("shortlisted", apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.SHORTLISTED)
                .toList());
        return "student/dashboard";
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
        return "student/profile";
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
            return "student/profile";
        }
        userService.updateProfile(user.getUsername(), form.getFullName(), form.getPhone(), form.getCompanyName());
        return "redirect:/student/profile?saved";
    }

    @PostMapping("/resume")
    public String uploadResume(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.uploadResume(user.getUsername(), file);
            redirectAttributes.addFlashAttribute("message", "Resume uploaded successfully.");
        } catch (IOException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/applications")
    public String applications(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("applications", jobApplicationService.listForStudent(user.getUsername()));
        return "student/applications";
    }

    @PostMapping("/jobs/{jobId}/apply")
    public String apply(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long jobId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            jobApplicationService.apply(user.getUsername(), jobId);
            redirectAttributes.addFlashAttribute("message", "Application submitted successfully.");
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/jobs/" + jobId;
    }

    @PostMapping("/applications/{id}/withdraw")
    public String withdraw(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            jobApplicationService.withdraw(user.getUsername(), id);
            redirectAttributes.addFlashAttribute("message", "Application withdrawn successfully.");
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/applications";
    }
}
