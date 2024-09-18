package com.sparta.filmfly.global.util;

import com.sparta.filmfly.domain.file.util.CustomMultipartFile;
import java.io.BufferedWriter;
import java.io.FileWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class FileUtils {

    public static final String uploadLocation = "/src/main/resources/static/temp/";
    public static final String sqlLocation = "/src/main/resources/dummy/";

    /**
     * 임시 저장된 이미지 폴더 경로
     */
    public static String getAbsoluteUploadFolder(String location) {
        try {
            File file = new File("");
            String currentAbsolutePath = file.getAbsolutePath() + location;
            Path path = Paths.get(currentAbsolutePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            return currentAbsolutePath;
        } catch (IOException e) {
            throw new RuntimeException("사진을 업로드할 폴더를 생성할 수 없습니다.", e);
        }
    }

    /**
     * 파일 이름 앞에 UUID 붙이기 36자 중에 10자만 사용
     */
    public static String createUuidFileName(String originalFileName) {
        String extension = extractExtension(originalFileName);
        return UUID.randomUUID() + "." + extension;
    }

    /**
     * url 끝에 FilmeName 추출
     * http://localhost:8080/temp/9bee7b11-3고양이.jpg  -> 9bee7b11-3고양이.jpg
     */
    public static Map<String, String> extractFileName(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        // URL에서 마지막 슬래시 위치를 찾습니다.
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return null;
        }
        // 슬래시 이후의 부분을 파일 이름으로 추출합니다.
        Map<String, String> urlMap = new HashMap<>();
        urlMap.put("url", url.substring(0,lastSlashIndex + 1));
        urlMap.put("file", url.substring(lastSlashIndex + 1));
        return urlMap;
    }

    /**
     * File -> MultipartFile 변경
     */
    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        return new CustomMultipartFile(file);
    }

    /**
     *  abc.jpg 에서 abc 부분
     */
    public static String extractOriginalName(String originalFileName) {
        return originalFileName.substring(0, originalFileName.indexOf("."));
    }

    /**
     * .jpg 같은
     */
    public static String extractExtension(String originalFileName) {
        int point = originalFileName.lastIndexOf(".");
        return originalFileName.substring(point + 1);
    }

    /**
     * local 파일 삭제
     */
    public static void deleteFileToLocal(String imageName) {
        Path path = Paths.get(getAbsoluteUploadFolder(uploadLocation), imageName);
        try {
            // 파일 삭제
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * url 한글로 표시
     * 7e5bbdd2-c%EA%B3%A0%EC%96%91%EC%9D%B4.jpg -> 7e5bbdd2-c고양이.jpg
     */
    public static String decodeUrlsInContent(String content) {
        Pattern pattern = Pattern.compile("src=\"(http[^\"]+)\"");
        Matcher matcher = pattern.matcher(content);
        StringBuilder decodedContent = new StringBuilder();

        while (matcher.find()) {
            String encodedUrl = matcher.group(1);
            String decodedUrl;
            try {
                decodedUrl = URLDecoder.decode(encodedUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("URL 디코딩 실패: " + encodedUrl, e);
            }
            matcher.appendReplacement(decodedContent, "src=\"" + decodedUrl + "\"");
        }
        matcher.appendTail(decodedContent);
        return decodedContent.toString();
    }

    /**
     * SQL 데이터를 파일로 저장하는 메서드
     * @param fileName 저장할 파일 이름
     * @param data 저장할 SQL 데이터
     */
    public static void saveSqlToFile(String fileName, String data) {
        String uploadFolder = getAbsoluteUploadFolder(sqlLocation);
        Path filePath = Paths.get(uploadFolder, fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(data);
        } catch (IOException e) {
            log.error("SQL 파일 저장 중 오류가 발생했습니다: {}", e.getMessage());
            throw new RuntimeException("SQL 파일 저장 중 오류가 발생했습니다.", e);
        }
    }
}