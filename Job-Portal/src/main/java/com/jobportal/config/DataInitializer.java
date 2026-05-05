package com.jobportal.config;

import com.jobportal.entity.JobPosting;
import com.jobportal.entity.User;
import com.jobportal.model.JobCategory;
import com.jobportal.model.Role;
import com.jobportal.repository.JobPostingRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsersAndJobs(
            UserRepository userRepository,
            JobPostingRepository jobPostingRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.upload.dir}") String uploadDir
    ) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            Path resumeDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(resumeDir);
            Path sampleResume = resumeDir.resolve("seed-demo-resume.pdf");
            Files.writeString(sampleResume, "Demo resume\nSkills: Java, Spring Boot, SQL");

            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setFullName("System Administrator");
            admin.setPhone("555-0100");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            User employer = new User();
            employer.setEmail("hr@acmetech.example");
            employer.setPassword(passwordEncoder.encode("Employer@123"));
            employer.setFullName("Jordan Lee");
            employer.setPhone("555-0101");
            employer.setCompanyName("Acme Tech Inc.");
            employer.setRole(Role.EMPLOYER);
            userRepository.save(employer);

            User employer2 = new User();
            employer2.setEmail("recruiting@datawave.example");
            employer2.setPassword(passwordEncoder.encode("Employer@123"));
            employer2.setFullName("Sam Rivera");
            employer2.setPhone("555-0102");
            employer2.setCompanyName("DataWave Analytics");
            employer2.setRole(Role.EMPLOYER);
            userRepository.save(employer2);

            User student = new User();
            student.setEmail("alex.chen@example.com");
            student.setPassword(passwordEncoder.encode("Student@123"));
            student.setFullName("Alex Chen");
            student.setPhone("555-0199");
            student.setRole(Role.STUDENT);
            student.setResumePath(sampleResume.toString());
            userRepository.save(student);

            JobPosting j1 = new JobPosting();
            j1.setTitle("Senior Java Developer — Spring Boot");
            j1.setDescription("""
                    Join our product team. You will design and build REST APIs, work with relational databases, \
                    and collaborate with frontend engineers. Code reviews, agile practices, and learning budget included.
                    """);
            j1.setSkillsRequired("Java 17+, Spring Boot, Spring Data JPA, REST, SQL, Git, JUnit");
            j1.setSalaryMin(115_000L);
            j1.setSalaryMax(150_000L);
            j1.setLocation("Seattle, WA");
            j1.setCategory(JobCategory.IT_SOFTWARE);
            j1.setMinExperienceYears(3);
            j1.setEmployer(employer);
            jobPostingRepository.save(j1);

            JobPosting j2 = new JobPosting();
            j2.setTitle("Data Analyst");
            j2.setDescription("""
                    Build dashboards, support reporting, and partner with business teams. \
                    Strong SQL and Python skills preferred.
                    """);
            j2.setSkillsRequired("SQL, Python, Excel, Power BI, communication");
            j2.setSalaryMin(72_000L);
            j2.setSalaryMax(95_000L);
            j2.setLocation("Austin, TX");
            j2.setCategory(JobCategory.DATA_SCIENCE);
            j2.setMinExperienceYears(1);
            j2.setEmployer(employer2);
            jobPostingRepository.save(j2);

            JobPosting j3 = new JobPosting();
            j3.setTitle("HR Operations Specialist");
            j3.setDescription("""
                    Handle onboarding, payroll coordination, and employee engagement. \
                    Experience with HR systems and stakeholder communication is a plus.
                    """);
            j3.setSkillsRequired("HR operations, onboarding, MS Office, stakeholder management");
            j3.setSalaryMin(52_000L);
            j3.setSalaryMax(68_000L);
            j3.setLocation("Boston, MA");
            j3.setCategory(JobCategory.HR_OPERATIONS);
            j3.setMinExperienceYears(2);
            j3.setEmployer(employer);
            jobPostingRepository.save(j3);
        };
    }
}
