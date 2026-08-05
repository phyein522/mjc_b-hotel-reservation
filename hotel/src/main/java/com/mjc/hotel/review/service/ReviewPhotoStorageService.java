package com.mjc.hotel.review.service;

import com.mjc.hotel.common.FileUtil;
import com.mjc.hotel.review.dto.ReviewPhotoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReviewPhotoStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    private final FileUtil fileUtil;

    public List<ReviewPhotoDto> upload(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        String year = String.valueOf(Year.now().getValue());
        String directory = "reviews/" + year;
        List<ReviewPhotoDto> uploaded = new ArrayList<>();

        for (int index = 0; index < files.size(); index++) {
            MultipartFile file = files.get(index);
            validateImage(file);
            String extension = fileUtil.getExtension(file.getOriginalFilename());
            String storedFileName = fileUtil.getRandomStoreFileName(40) + "." + extension;
            if (!fileUtil.copyFile(file, directory, storedFileName)) {
                throw new IllegalStateException("리뷰 사진을 저장하지 못했습니다.");
            }
            uploaded.add(ReviewPhotoDto.builder()
                    .photoPath("/api/review/photos/" + year + "/" + storedFileName)
                    .photoOrder(index + 1)
                    .build());
        }

        return uploaded;
    }

    public Resource load(String year, String fileName) throws IOException {
        if (!year.matches("\\d{4}") || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new IllegalArgumentException("잘못된 리뷰 사진 경로입니다.");
        }
        try {
            return fileUtil.loadFileAsResource("reviews/" + year, fileName);
        } catch (IOException primaryError) {
            Resource fallback = loadFromAlternateWorkingDirectory(year, fileName);
            if (fallback != null) {
                return fallback;
            }
            throw primaryError;
        }
    }

    private Resource loadFromAlternateWorkingDirectory(String year, String fileName) throws IOException {
        Path configured = Path.of(fileUtil.getUploadPath());
        if (configured.isAbsolute()) {
            return null;
        }

        Path workingDirectory = Path.of("").toAbsolutePath().normalize();
        List<Path> alternateRoots = new ArrayList<>();
        if (workingDirectory.getParent() != null) {
            alternateRoots.add(workingDirectory.getParent().resolve(configured).normalize());
        }
        alternateRoots.add(workingDirectory.resolve("hotel").resolve(configured).normalize());

        for (Path root : alternateRoots) {
            Path file = root.resolve("reviews").resolve(year).resolve(fileName).normalize();
            if (!file.startsWith(root) || !Files.isRegularFile(file) || !Files.isReadable(file)) {
                continue;
            }
            return new UrlResource(file.toUri());
        }
        return null;
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 리뷰 사진은 업로드할 수 없습니다.");
        }
        String extension = fileUtil.getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("리뷰 사진은 JPG, PNG, WEBP, GIF 형식만 업로드할 수 있습니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
    }
}
