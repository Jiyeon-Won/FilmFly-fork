package com.sparta.filmfly.global.infra;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.sparta.filmfly.domain.file.etc.MediaTypeEnum;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.UploadException;
import com.sparta.filmfly.global.util.FileUtils;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class S3Uploader {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    @Autowired
    public S3Uploader(AmazonS3 amazonS3, @Value("${cloud.aws.s3.bucket}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    Map<String, String> contentTypeMap = Map.ofEntries(
        Map.entry(".png","image/png"),
        Map.entry(".jpg","image/jpg"),
        Map.entry(".jpeg","image/jpeg"),
        Map.entry(".gif","image/gif")
    );

    public boolean isFileSame(MultipartFile file, String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        String fileName = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
        try {
            ObjectMetadata metadata = amazonS3.getObjectMetadata(bucketName, fileName);
            return metadata.getContentLength() == file.getSize()
                && metadata.getContentType().equals(file.getContentType());
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                // 파일이 존재하지 않으면 false 반환
                return false;
            } else {
                throw e; // 다른 예외는 다시 던짐
            }
        }
    }

    /**
     * s3 파일 업로드
     */
    public String uploadFile(MediaTypeEnum mediaType, Long typeId, MultipartFile file) throws IOException {
        String fileName = createMediaPath(mediaType, typeId, file.getOriginalFilename()); //같은 이름의 파일은 그 위에 다시 업로드됨

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(determineContentType(fileName));
        metadata.setContentLength(file.getSize());
        metadata.setContentDisposition("inline");

        amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
        return amazonS3.getUrl(bucketName, fileName).toString()
//            URL 시작부터 .com 으로 끝나는 곳 까지를 https://img.filmfly.shop 로 교체
            .replaceFirst(".*\\.com/", FileUtils.S3_URL);
    }

    /**
     * s3 파일 삭제
     */
    public void deleteFile(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return;
        }
        String fileName = fileUrl.substring(fileUrl.indexOf(".shop/") + 6);
        try {
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        } catch (Exception e){
            throw new UploadException(ResponseCodeEnum.UPLOAD_FAILED);
        }
    }

    /**
     * s3에 저장하는 파일 이름 변경
     */
    private String createMediaPath(MediaTypeEnum mediaType, Long typeId, String fileName){
        StringBuilder sb = new StringBuilder();

        sb.append(mediaType.getS3FolderPrefix());

        return sb.append(typeId).append("/").append(fileName).toString();
    }

    private String determineContentType(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex != -1) {
            String substring = fileName.substring(lastIndex);
            return contentTypeMap.get(substring);
        }
        return "application/octet-stream";
    }
}