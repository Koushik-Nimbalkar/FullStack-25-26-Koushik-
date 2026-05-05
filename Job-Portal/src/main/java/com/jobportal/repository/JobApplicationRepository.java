package com.jobportal.repository;

import com.jobportal.entity.JobApplication;
import com.jobportal.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    @Query("""
            SELECT DISTINCT a FROM JobApplication a
            JOIN FETCH a.job j
            JOIN FETCH j.employer
            WHERE a.student.id = :sid
            ORDER BY a.appliedAt DESC
            """)
    List<JobApplication> findByStudentWithDetails(@Param("sid") Long studentId);

    @Query("""
            SELECT DISTINCT a FROM JobApplication a
            JOIN FETCH a.student
            WHERE a.job.id = :jobId
            ORDER BY a.appliedAt DESC
            """)
    List<JobApplication> findByJobWithStudents(@Param("jobId") Long jobId);

    Optional<JobApplication> findByJobIdAndStudentId(Long jobId, Long studentId);

    @Query("""
            SELECT a FROM JobApplication a
            JOIN FETCH a.job j
            JOIN FETCH j.employer
            JOIN FETCH a.student
            WHERE a.id = :id
            """)
    Optional<JobApplication> findByIdWithDetails(@Param("id") Long id);

    long countByJob_Employer_Id(Long employerId);

    long countByJob_Employer_IdAndStatus(Long employerId, ApplicationStatus status);
}
