package com.jobportal.api.dto;

import com.jobportal.entity.JobPosting;
import com.jobportal.model.JobCategory;

import java.time.Instant;

public class JobResponse {

    private Long id;
    private String title;
    private String description;
    private String skillsRequired;
    private Long salaryMin;
    private Long salaryMax;
    private String location;
    private JobCategory category;
    private Integer minExperienceYears;
    private String employerName;
    private String companyName;
    private Instant postedAt;
    private boolean active;

    public static JobResponse from(JobPosting j) {
        JobResponse r = new JobResponse();
        r.id = j.getId();
        r.title = j.getTitle();
        r.description = j.getDescription();
        r.skillsRequired = j.getSkillsRequired();
        r.salaryMin = j.getSalaryMin();
        r.salaryMax = j.getSalaryMax();
        r.location = j.getLocation();
        r.category = j.getCategory();
        r.minExperienceYears = j.getMinExperienceYears();
        r.employerName = j.getEmployer().getFullName();
        r.companyName = j.getEmployer().getCompanyName();
        r.postedAt = j.getPostedAt();
        r.active = j.isActive();
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public Long getSalaryMin() {
        return salaryMin;
    }

    public Long getSalaryMax() {
        return salaryMax;
    }

    public String getLocation() {
        return location;
    }

    public JobCategory getCategory() {
        return category;
    }

    public Integer getMinExperienceYears() {
        return minExperienceYears;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Instant getPostedAt() {
        return postedAt;
    }

    public boolean isActive() {
        return active;
    }
}
