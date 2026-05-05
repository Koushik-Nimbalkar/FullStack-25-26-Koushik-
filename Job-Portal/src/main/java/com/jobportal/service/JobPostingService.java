package com.jobportal.service;

import com.jobportal.entity.JobPosting;
import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.JobCategory;
import com.jobportal.model.Role;
import com.jobportal.repository.JobPostingRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.web.dto.JobForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    public JobPostingService(JobPostingRepository jobPostingRepository, UserRepository userRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
    }

    public List<JobPosting> searchPublic(JobCategory category, String location, Integer maxExperienceYears) {
        String loc = location != null && !location.isBlank() ? location.trim() : null;
        return jobPostingRepository.search(category, loc, maxExperienceYears);
    }

    public List<JobPosting> listActive() {
        return jobPostingRepository.findActiveWithEmployer();
    }

    public JobPosting getActive(Long id) {
        JobPosting job = jobPostingRepository.findByIdWithEmployer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found."));
        if (!job.isActive()) {
            throw new ResourceNotFoundException("Job not found.");
        }
        return job;
    }

    public JobPosting getForEmployer(Long jobId, String employerEmail) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found."));
        if (!job.getEmployer().getId().equals(employer.getId()) && employer.getRole() != Role.ADMIN) {
            throw new BadRequestException("You cannot manage this job.");
        }
        return job;
    }

    public List<JobPosting> listForEmployer(String employerEmail) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));
        return jobPostingRepository.findByEmployerIdOrderByPostedAtDesc(employer.getId());
    }

    @Transactional
    public JobPosting create(String employerEmail, JobForm form) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));
        if (employer.getRole() != Role.EMPLOYER && employer.getRole() != Role.ADMIN) {
            throw new BadRequestException("Only employers can post jobs.");
        }
        JobPosting job = new JobPosting();
        applyForm(job, form);
        job.setEmployer(employer);
        return jobPostingRepository.save(job);
    }

    @Transactional
    public JobPosting update(String employerEmail, Long jobId, JobForm form) {
        JobPosting job = getForEmployer(jobId, employerEmail);
        applyForm(job, form);
        return jobPostingRepository.save(job);
    }

    @Transactional
    public void delete(String employerEmail, Long jobId) {
        JobPosting job = getForEmployer(jobId, employerEmail);
        job.setActive(false);
        jobPostingRepository.save(job);
    }

    private void applyForm(JobPosting job, JobForm form) {
        job.setTitle(form.getTitle().trim());
        job.setDescription(form.getDescription().trim());
        job.setSkillsRequired(form.getSkillsRequired().trim());
        job.setSalaryMin(form.getSalaryMin());
        job.setSalaryMax(form.getSalaryMax());
        if (job.getSalaryMin() > job.getSalaryMax()) {
            throw new BadRequestException("Minimum salary cannot be greater than maximum salary.");
        }
        job.setLocation(form.getLocation().trim());
        job.setCategory(form.getCategory());
        job.setMinExperienceYears(form.getMinExperienceYears() != null ? form.getMinExperienceYears() : 0);
    }
}
