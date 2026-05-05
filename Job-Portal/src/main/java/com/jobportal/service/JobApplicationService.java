package com.jobportal.service;

import com.jobportal.entity.JobApplication;
import com.jobportal.entity.JobPosting;
import com.jobportal.entity.User;
import com.jobportal.exception.BadRequestException;
import com.jobportal.exception.ResourceNotFoundException;
import com.jobportal.model.ApplicationStatus;
import com.jobportal.model.Role;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobPostingRepository;
import com.jobportal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobApplicationService {

    private static final Logger log = LoggerFactory.getLogger(JobApplicationService.class);

    private final JobApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;

    public JobApplicationService(JobApplicationRepository applicationRepository,
                                 JobPostingRepository jobPostingRepository,
                                 UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
    }

    public List<JobApplication> listForStudent(String email) {
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found."));
        return applicationRepository.findByStudentWithDetails(student.getId());
    }

    public List<JobApplication> listForJob(Long jobId, String employerEmail) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found."));
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));
        if (!job.getEmployer().getId().equals(employer.getId()) && employer.getRole() != Role.ADMIN) {
            throw new BadRequestException("You cannot view applications for this job.");
        }
        return applicationRepository.findByJobWithStudents(jobId);
    }

    @Transactional
    public JobApplication apply(String studentEmail, Long jobId) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));
        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only students can apply for jobs.");
        }
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found."));
        if (!job.isActive()) {
            throw new BadRequestException("This job is no longer accepting applications.");
        }
        if (student.getResumePath() == null || student.getResumePath().isBlank()) {
            throw new BadRequestException("Please upload your resume before applying.");
        }
        if (applicationRepository.findByJobIdAndStudentId(jobId, student.getId()).isPresent()) {
            throw new BadRequestException("You have already applied for this job.");
        }
        JobApplication app = new JobApplication();
        app.setJob(job);
        app.setStudent(student);
        app.setStatus(ApplicationStatus.PENDING);
        app.setResumePathAtApply(student.getResumePath());
        JobApplication saved = applicationRepository.save(app);
        return applicationRepository.findByIdWithDetails(saved.getId()).orElse(saved);
    }

    @Transactional
    public void withdraw(String studentEmail, Long applicationId) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));
        JobApplication app = applicationRepository.findByIdWithDetails(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found."));
        
        if (!app.getStudent().getId().equals(student.getId())) {
            throw new BadRequestException("You can only withdraw your own applications.");
        }
        
        applicationRepository.delete(app);
    }

    @Transactional
    public JobApplication updateStatus(Long applicationId, ApplicationStatus newStatus, String employerEmail) {
        JobApplication app = applicationRepository.findByIdWithDetails(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found."));
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new BadRequestException("User not found."));
        if (!app.getJob().getEmployer().getId().equals(employer.getId()) && employer.getRole() != Role.ADMIN) {
            throw new BadRequestException("You cannot update this application.");
        }
        app.setStatus(newStatus);
        JobApplication saved = applicationRepository.save(app);
        if (newStatus == ApplicationStatus.SHORTLISTED) {
            log.info("[Email stub] To: {} — Congratulations! You have been shortlisted for '{}' at {}.",
                    app.getStudent().getEmail(),
                    app.getJob().getTitle(),
                    app.getJob().getEmployer().getCompanyName() != null
                            ? app.getJob().getEmployer().getCompanyName()
                            : app.getJob().getEmployer().getFullName());
        }
        return saved;
    }

    public long countApplicationsForEmployer(Long employerId) {
        return applicationRepository.countByJob_Employer_Id(employerId);
    }

    public long countShortlistedForEmployer(Long employerId) {
        return applicationRepository.countByJob_Employer_IdAndStatus(employerId, ApplicationStatus.SHORTLISTED);
    }
}
