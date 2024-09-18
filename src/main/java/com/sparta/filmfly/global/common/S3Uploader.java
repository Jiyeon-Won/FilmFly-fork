package com.sparta.filmfly.global.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.sparta.filmfly.domain.media.entity.MediaTypeEnum;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.UploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

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

    public String upload(MultipartFile file, String dirName) throws IOException {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        log.info("Uploading file: {}", fileName);
        amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata);
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        String fileName = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
        log.info("Deleting file: {}", fileName);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    public boolean isFileSame(MultipartFile file, String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        String fileName = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
        try {
            ObjectMetadata metadata = amazonS3.getObjectMetadata(bucketName, fileName);
            boolean isSame = metadata.getContentLength() == file.getSize() &&
                    metadata.getContentType().equals(file.getContentType());
            log.info("Comparing files - is same: {}", isSame);
            return isSame;
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                // 파일이 존재하지 않으면 false 반환
                log.warn("File not found: {}", fileName);
                return false;
            } else {
                throw e; // 다른 예외는 다시 던짐
            }
        }
    }

    /**
     * s3 파일 업로드
     */
    public String boardFileUpload(MediaTypeEnum mediaType, Long typeId, MultipartFile file) throws IOException {
        String fileName = createMediaPath(mediaType,typeId,file.getOriginalFilename()); //같은 이름의 파일은 그 위에 다시 업로드됨

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3.putObject(bucketName, fileName, file.getInputStream(), metadata); // 파일 업로드
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    /**
     * s3 파일 삭제
     */
    public void boardFileDelete(MediaTypeEnum mediaType, Long typeId, String fileName){
        String key = createMediaPath(mediaType,typeId,fileName); //s3에 올라간 파일 이름
        try{
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        }catch (Exception e){
            throw new UploadException(ResponseCodeEnum.UPLOAD_FAILED);
        }
    }

    /**
     * s3에 저장하는 파일 이름 변경
     */
    private String createMediaPath(MediaTypeEnum mediaType, Long typeId, String fileName){
        StringBuilder sb = new StringBuilder();
        if(mediaType == MediaTypeEnum.BOARD) { //FileService.checkModifiedImageFile에 있는거랑 비슷함
            sb.append("boards/");
        } else {
            sb.append("etc/");
        }
        return sb.append(typeId).append("/").append(fileName).toString();
    }
}