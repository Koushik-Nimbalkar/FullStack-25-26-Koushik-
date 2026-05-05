package com.jobportal.web.dto;

import com.jobportal.api.dto.JobRequest;
import com.jobportal.model.JobCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class JobForm {

    @NotBlank(message = "Job title is required")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 8000)
    private String description;

    @NotBlank(message = "Skills are required")
    @Size(max = 2000)
    private String skillsRequired;

    @NotNull(message = "Minimum annual salary is required")
    @Min(value = 0, message = "Salary must be positive")
    private Long salaryMin;

    @NotNull(message = "Maximum annual salary is required")
    @Min(value = 0, message = "Salary must be positive")
    private Long salaryMax;

    @NotBlank(message = "Location is required")
    @Size(max = 200)
    private String location;

    @NotNull(message = "Category is required")
    private JobCategory category;

    @NotNull(message = "Minimum experience (years) is required")
    @Min(value = 0, message = "Experience cannot be negative")
    private Integer minExperienceYears;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(String skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public Long getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Long salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Long getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Long salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public JobCategory getCategory() {
        return category;
    }

    public void setCategory(JobCategory category) {
        this.category = category;
    }

    public Integer getMinExperienceYears() {
        return minExperienceYears;
    }

    public void setMinExperienceYears(Integer minExperienceYears) {
        this.minExperienceYears = minExperienceYears;
    }

    public static JobForm fromRequest(JobRequest r) {
        JobForm f = new JobForm();
        f.setTitle(r.getTitle());
        f.setDescription(r.getDescription());
        f.setSkillsRequired(r.getSkillsRequired());
        f.setSalaryMin(r.getSalaryMin());
        f.setSalaryMax(r.getSalaryMax());
        f.setLocation(r.getLocation());
        f.setCategory(r.getCategory());
        f.setMinExperienceYears(r.getMinExperienceYears());
        return f;
    }
}
