package com.jobportal.api;

import com.jobportal.api.dto.JobRequest;
import com.jobportal.api.dto.JobResponse;
import com.jobportal.entity.JobPosting;
import com.jobportal.model.JobCategory;
import com.jobportal.service.JobPostingService;
import com.jobportal.web.dto.JobForm;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobRestController {

    private final JobPostingService jobPostingService;

    public JobRestController(JobPostingService jobPostingService) {
        this.jobPostingService = jobPostingService;
    }

    @GetMapping
    public List<JobResponse> list(
            @RequestParam(required = false) JobCategory category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer maxExperienceYears
    ) {
        return jobPostingService.searchPublic(category, location, maxExperienceYears).stream()
                .map(JobResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public JobResponse get(@PathVariable Long id) {
        JobPosting job = jobPostingService.getActive(id);
        return JobResponse.from(job);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse create(@AuthenticationPrincipal UserDetails user, @Valid @RequestBody JobRequest request) {
        JobForm form = JobForm.fromRequest(request);
        JobPosting job = jobPostingService.create(user.getUsername(), form);
        return JobResponse.from(jobPostingService.getActive(job.getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    public JobResponse update(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @Valid @RequestBody JobRequest request
    ) {
        JobForm form = JobForm.fromRequest(request);
        jobPostingService.update(user.getUsername(), id, form);
        return JobResponse.from(jobPostingService.getForEmployer(id, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYER','ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        jobPostingService.delete(user.getUsername(), id);
    }
}
