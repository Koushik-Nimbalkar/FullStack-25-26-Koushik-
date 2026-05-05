package com.jobportal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED = Set.of("pdf", "doc", "docx");

    private final Path root;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String storeResume(MultipartFile file, Long userId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please choose a file to upload.");
        }
        String original = file.getOriginalFilename();
        String ext = extension(original);
        if (!ALLOWED.contains(ext.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Only PDF or Word documents (.pdf, .doc, .docx) are allowed.");
        }
        Files.createDirectories(root);
        String name = "resume-" + userId + "-" + System.currentTimeMillis() + "." + ext.toLowerCase(Locale.ROOT);
        Path target = root.resolve(name);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }

    private static String extension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
