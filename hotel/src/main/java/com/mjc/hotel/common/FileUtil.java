package com.mjc.hotel.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Getter
@Component
public class FileUtil {
	@Value("${hotel.save-folder}")
	private String uploadPath;

	/**
	 * 디렉토리가 존재하면 true 를 리턴한다.
	 * @param dir 디렉존제 여부를 체크할 디렉토리 path 문자열
	 * @return
	 */
	public boolean checkDirectory(String dir) {
		if ( dir == null ) {
			return false;
		}
		File f = new File(dir);
		if (!f.exists()) {
			return false;
		}
		return true;
	}

	/**
	 * 디렉토리를 재귀적으로 생성해준다.
	 * @param dir "d:/temp/a/b/c"
	 * @return
	 */
	public boolean makeRecursiveDirectory(String dir) throws IOException {
		if ( dir == null ) {
			return false;
		}
		Path path = Paths.get(dir);
		Files.createDirectories(path);
		return true;
	}

	/**
	 * 디렉토리를 맨 마지막 디렉토리를 삭제해준다.
	 * @param dir "d:/temp/a/b/c" => "d:/temp/a/b"
	 * @return
	 */
	public boolean deleteRecursiveDirectory(String dir) throws IOException {
		if ( dir == null ) {
			return false;
		}
		// 디렉토리 삭제하는 코딩
		return true;
	}

	public String getRandomStoreFileName(int length) {
		String arrs = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random rnd = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int index = rnd.nextInt(arrs.length());
			sb.append(arrs.charAt(index));
		}
		return sb.toString();
	}

	public String getExtension(String fileName) {
		if (fileName == null) {
			return "";
		}
		int lastIndex = fileName.lastIndexOf('.');
		if (lastIndex == -1) {
			return "";
		}
		return fileName.substring(lastIndex + 1).toLowerCase();
	}

	public void deleteFile(String path, String fileName) {
		String fullPath = this.uploadPath + "/" + path;
		Path filePath = Paths.get(fullPath + "/" + fileName);
		try {
			Files.delete(filePath);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public boolean deleteFile(String fileName) {
		if ( !this.checkDirectory(fileName) ) {
			return false;
		}
		try {
			Path path = Paths.get(fileName);
			Files.delete(path);
		} catch (IOException e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 첨부파일을 path/dest 경로로 파일 복사를 한다. 결과는 복사된 바이트 수를 리턴한다.
	 * @param src 첨부파일 객체인 MultipartFile
	 * @param path 저장될 디렉토리
	 * @param destFileName 저장될 파일명
	 * @return 저장된 바이트수
	 * @throws IOException
	 */
	public boolean copyFile(MultipartFile src, String path, String destFileName) throws RuntimeException {
		if ( src == null || path == null || destFileName == null || src.isEmpty() || destFileName.isEmpty() ) {
			return false;
		}
		try {
			String fullPath = this.uploadPath + "/" + path;
			if (!this.checkDirectory(fullPath)) {
				this.makeRecursiveDirectory(fullPath);
			}
			Path destPath = Paths.get(fullPath + "/" + destFileName);
			long lResult = Files.copy(src.getInputStream()
					, destPath
					, StandardCopyOption.REPLACE_EXISTING);
			if (lResult <= 0) {
				return false;
			}
		}  catch (IOException e) {
			log.error(e.toString());
			throw new RuntimeException(e);
		}
		return true;
	}

	public Resource loadFileAsResource(String path, String fileName) throws IOException {
		String fullPath = this.uploadPath + "/" + path;
		Path filePath = Paths.get(fullPath + "/" + fileName);
		Resource resource = new UrlResource(filePath.toUri());
		if (!resource.exists() || !resource.isReadable()) {
			String msg = String.format("can't read file %s", filePath.toString());
			log.error(msg);
			throw new IOException(msg);
		}
		return resource;
	}

	public byte[] loadFileAsBytes(String path, String fileName) throws IOException {
		String fullPath = this.uploadPath + "/" + path;
		Path filePath = Paths.get(fullPath + "/" + fileName);
		return Files.readAllBytes(filePath);
	}
}
