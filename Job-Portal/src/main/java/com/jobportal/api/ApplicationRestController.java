package com.jobportal.api;

import com.jobportal.api.dto.ApplicationResponse;
import com.jobportal.api.dto.ApplicationStatusUpdateRequest;
import com.jobportal.entity.JobApplication;
import com.jobportal.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationRestController {

    private final JobApplicationService jobApplicationService;

    public ApplicationRestController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('STUDENT')")
    public List<ApplicationResponse> myApplications(@AuthenticationPrincipal UserDetails user) {
        return jobApplicationService.listForStudent(user.getUsername()).stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    @GetMapping("/jobs/{jobId}/applications")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    public List<ApplicationResponse> listForJob(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long jobId
    ) {
        return jobApplicationService.listForJob(jobId, user.getUsername()).stream()
                .map(ApplicationResponse::from)
                .toList();
    }

    @PostMapping("/jobs/{jobId}/applications")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponse apply(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long jobId
    ) {
        JobApplication app = jobApplicationService.apply(user.getUsername(), jobId);
        return ApplicationResponse.from(app);
    }

    @PatchMapping("/applications/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    public ApplicationResponse updateStatus(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest body
    ) {
        JobApplication app = jobApplicationService.updateStatus(id, body.getStatus(), user.getUsername());
        return ApplicationResponse.from(app);
    }
}
