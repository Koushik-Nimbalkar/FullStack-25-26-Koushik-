package com.jobportal.api.dto;

import com.jobportal.entity.JobApplication;
import com.jobportal.model.ApplicationStatus;

import java.time.Instant;

public class ApplicationResponse {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private ApplicationStatus status;
    private Instant appliedAt;

    public static ApplicationResponse from(JobApplication a) {
        ApplicationResponse r = new ApplicationResponse();
        r.id = a.getId();
        r.jobId = a.getJob().getId();
        r.jobTitle = a.getJob().getTitle();
        r.studentId = a.getStudent().getId();
        r.studentName = a.getStudent().getFullName();
        r.studentEmail = a.getStudent().getEmail();
        r.status = a.getStatus();
        r.appliedAt = a.getAppliedAt();
        return r;
    }

    public Long getId() {
        return id;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }
}
