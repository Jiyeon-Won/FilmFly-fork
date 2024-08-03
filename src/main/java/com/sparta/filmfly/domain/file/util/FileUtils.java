package com.sparta.filmfly.domain.file.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class FileUtils {

    private final String uploadLocation = "/src/main/resources/static/temp/";

    /**
     * 임시 저장된 이미지 폴더 경로
     */
    public String getAbsoluteUploadFolder() {
        try {
            File file = new File("");
            String currentAbsolutePath = file.getAbsolutePath() + uploadLocation;
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
    public String createUuidFileName(String originalFileName) {
        //String extension = extractExtension(originalFileName);
        //return UUID.randomUUID() + "." + extension;
        return UUID.randomUUID().toString().substring(0, 10) + originalFileName;
    }

    /**
     * url 끝에 FilmeName 추출
     * http://localhost:8080/temp/9bee7b11-3고양이.jpg  -> 9bee7b11-3고양이.jpg
     */
    public String extractFileName(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        // URL에서 마지막 슬래시 위치를 찾습니다.
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex == -1) {
            return null;
        }
        // 슬래시 이후의 부분을 파일 이름으로 추출합니다.
        return url.substring(lastSlashIndex + 1);
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
    public String extractOriginalName(String originalFileName) {
        return originalFileName.substring(0, originalFileName.indexOf("."));
    }

    /**
     * .jpg 같은
     */
    public String extractExtension(String originalFileName) {
        int point = originalFileName.lastIndexOf(".");
        return originalFileName.substring(point + 1);
    }

    /**
     * local 파일 삭제
     */
    public void deleteFileToLocal(String imageName) {
        // 주어진 파일 경로와 이름을 기반으로 파일 경로 객체 생성
        log.error("1 : {} \n 2 : {}",getAbsoluteUploadFolder(),imageName);
        Path path = Paths.get(getAbsoluteUploadFolder(), imageName);
        log.error("3 : {}",path);
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
    public String decodeUrlsInContent(String content) {
        Pattern pattern = Pattern.compile("src=\"(http[^\"]+)\"");
        Matcher matcher = pattern.matcher(content);
        StringBuffer decodedContent = new StringBuffer();

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
}