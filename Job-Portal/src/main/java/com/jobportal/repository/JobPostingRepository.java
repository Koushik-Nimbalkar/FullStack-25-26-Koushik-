package com.jobportal.repository;

import com.jobportal.entity.JobPosting;
import com.jobportal.model.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    @Query("""
            SELECT DISTINCT j FROM JobPosting j
            JOIN FETCH j.employer
            WHERE j.active = true
            ORDER BY j.postedAt DESC
            """)
    List<JobPosting> findActiveWithEmployer();

    List<JobPosting> findByEmployerIdOrderByPostedAtDesc(Long employerId);

    long countByEmployerId(Long employerId);

    @Query("""
            SELECT DISTINCT j FROM JobPosting j
            JOIN FETCH j.employer
            WHERE j.active = true
              AND (:category IS NULL OR j.category = :category)
              AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND (:maxExp IS NULL OR j.minExperienceYears <= :maxExp)
            ORDER BY j.postedAt DESC
            """)
    List<JobPosting> search(
            @Param("category") JobCategory category,
            @Param("location") String location,
            @Param("maxExp") Integer maxExp
    );

    @Query("SELECT j FROM JobPosting j JOIN FETCH j.employer WHERE j.id = :id")
    Optional<JobPosting> findByIdWithEmployer(@Param("id") Long id);
}
