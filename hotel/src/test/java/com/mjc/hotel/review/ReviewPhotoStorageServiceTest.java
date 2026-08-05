package com.mjc.hotel.review;

import com.mjc.hotel.common.FileUtil;
import com.mjc.hotel.review.dto.ReviewPhotoDto;
import com.mjc.hotel.review.service.ReviewPhotoStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.Year;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewPhotoStorageServiceTest {

    @Mock
    private FileUtil fileUtil;

    @InjectMocks
    private ReviewPhotoStorageService reviewPhotoStorageService;

    @Test
    void uploadStoresImageAndReturnsPublicPath() {
        MockMultipartFile image = new MockMultipartFile(
                "files",
                "stay.png",
                "image/png",
                new byte[]{1, 2, 3}
        );
        String year = String.valueOf(Year.now().getValue());
        when(fileUtil.getExtension("stay.png")).thenReturn("png");
        when(fileUtil.getRandomStoreFileName(40)).thenReturn("stored-review-photo");
        when(fileUtil.copyFile(image, "reviews/" + year, "stored-review-photo.png")).thenReturn(true);

        List<ReviewPhotoDto> result = reviewPhotoStorageService.upload(List.of(image));

        assertEquals(1, result.size());
        assertEquals("/api/review/photos/" + year + "/stored-review-photo.png", result.get(0).getPhotoPath());
        assertEquals(1, result.get(0).getPhotoOrder());
        verify(fileUtil).copyFile(image, "reviews/" + year, "stored-review-photo.png");
    }

    @Test
    void uploadRejectsNonImageFile() {
        MockMultipartFile textFile = new MockMultipartFile(
                "files",
                "note.txt",
                "text/plain",
                "not an image".getBytes()
        );
        when(fileUtil.getExtension("note.txt")).thenReturn("txt");

        assertThrows(IllegalArgumentException.class,
                () -> reviewPhotoStorageService.upload(List.of(textFile)));
    }

    @Test
    void loadFindsPhotoAfterWorkingDirectoryChanges() throws Exception {
        Path parent = Path.of("").toAbsolutePath().normalize().getParent();
        Path uploadRoot = Files.createTempDirectory(parent, "review-photo-test-");
        Path reviewDirectory = Files.createDirectories(uploadRoot.resolve("reviews/2026"));
        Path photo = Files.write(reviewDirectory.resolve("photo.jpg"), new byte[]{1, 2, 3});
        try {
            when(fileUtil.loadFileAsResource("reviews/2026", "photo.jpg")).thenThrow(new IOException("missing"));
            when(fileUtil.getUploadPath()).thenReturn(uploadRoot.getFileName().toString());

            assertTrue(reviewPhotoStorageService.load("2026", "photo.jpg").exists());
        } finally {
            Files.deleteIfExists(photo);
            Files.deleteIfExists(reviewDirectory);
            Files.deleteIfExists(uploadRoot.resolve("reviews"));
            Files.deleteIfExists(uploadRoot);
        }
    }
}
