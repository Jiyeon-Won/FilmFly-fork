package com.sparta.filmfly.domain.file.service;

import com.sparta.filmfly.domain.file.util.FileUtils;
import com.sparta.filmfly.domain.media.dto.MediaResponseDto;
import com.sparta.filmfly.domain.media.entity.Media;
import com.sparta.filmfly.domain.media.entity.MediaTypeEnum;
import com.sparta.filmfly.domain.media.service.MediaService;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileUtils fileUtils;

    private final MediaService mediaService;
    //img src 패턴 검사용
    private final String imgReg = "(<img[^>]*src\s*=\s*[\"']?([^>\"\']+)[\"']?[^>]*>)"; // <img src 패턴
    private final Pattern pattern = Pattern.compile(imgReg);

    public String saveFileToLocal(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String folderLocation = fileUtils.getAbsoluteUploadFolder();
        String uuidFileName = fileUtils.createUuidFileName(originalFilename);
        String fileFullPath = folderLocation + uuidFileName;
        File saveFile = new File(fileFullPath);
        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            log.error("local에 파일을 저장할 수 없습니다. (원본 파일 이름 : {})", originalFilename, e);
            return null;
        }
        return "/temp/" + uuidFileName;
    }

    public void deleteFileToLocal(String imageName) {
        fileUtils.deleteFileToLocal(imageName);
    }

    /**
     * localhost temp image 글자 있으면 s3에 업로드 후 temp 이미지 삭제
     * local 주소를 s3 주소로 변경된 값을 반환
     */
    public String uploadLocalImageToS3(MediaTypeEnum mediaType,Long boardId,String content) {
        log.info("파일 변환");
        content = fileUtils.decodeUrlsInContent(content);   // src= 찾기
//        log.info(content);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()){
            String src = matcher.group(2).trim(); // http://localhost:8080/temp/9bee7b11-3고양이.jpg
            String srcUrl = fileUtils.extractFileName(src).get("url"); // http://localhost:8080/temp
            String srcFileName = fileUtils.extractFileName(src).get("file"); // 9bee7b11-3고양이.jpg
            String currentUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString().split("://")[1]; //http://localhost:8080

            log.info("\n srcUrl : {} \n currentUrl : {} \n srcFileName : {}",srcUrl,currentUrl,srcFileName);

            //img의 src 값의 앞 부분이 임시 이미지면 http://localhost:8080/temp ?
            if(src.split("://")[1].startsWith(currentUrl+"/temp/")) {
                log.info("temp 이미지 확인");
                try {
                    String filePath = fileUtils.getAbsoluteUploadFolder() + srcFileName;

                    File file = new File(filePath); //temp 폴더의 임시 이미지 지정
                    MultipartFile multipartFile = FileUtils.convertFileToMultipartFile(file); //MultipartFile로 형식 변경
                    //System.out.println("File name: " + multipartFile.getOriginalFilename());
                    //System.out.println("File size: " + multipartFile.getSize());

                    //temp 폴더에 있는 경우
                    if(!multipartFile.isEmpty()) {
                        log.info("변환 후 저장");
                        // s3에 저장 및 media 테이블에 저장
                        MediaResponseDto mediaResponseDto = mediaService.createMedia(mediaType, boardId, multipartFile);
                        // local 임시 이미지 삭제
                        fileUtils.deleteFileToLocal(srcFileName);
                        content = content.replace(src, mediaResponseDto.getUrl()); //local 이미지 -> s3 주소로 변경
                        matcher = pattern.matcher(content);
                    }
                    else {
                        //없는 파일이면 공백으로 지우기
                        String img = matcher.group(1); // <img src="http://localhost:8080/temp/9bee7b11-3고양이.jpg" style="width: 25%;">
                        content = content.replace(img, "");
                        matcher = pattern.matcher(content);
                    }
                } catch (IOException e) {
                    throw new UploadException(ResponseCodeEnum.BOARD_FILE_PROCESSING_ERROR);
                }
            }
        }
        return content;
    }

    /**
     * 변경된 이미지 파일 검사
     * 사라진 s3 경로는 s3에서 삭제
     */
    public void checkModifiedImageFile(MediaTypeEnum mediaType,Long mediaId,String content) {
        content = fileUtils.decodeUrlsInContent(content);

        List<Media> mediaList = mediaService.getListMedia(mediaType,mediaId);
        List<Boolean> mediaExists = new ArrayList<>(Collections.nCopies(mediaList.size(), false));

        Matcher matcher = pattern.matcher(content);
        while (matcher.find()){
            String src = matcher.group(2).trim();
            String mediaText;
            if(mediaType == MediaTypeEnum.BOARD) { //S3Uploader.createMediaPath 랑 내용 비슷함
                mediaText = "boards/";
            }
            else if(mediaType == MediaTypeEnum.OFFICE_BOARD) {
                mediaText = "officeBoards/";
            }
            else {
                mediaText = "etc/";
            }
            String s3Url = "https://filmfly-backend.s3.ap-northeast-2.amazonaws.com/"+mediaText+mediaId; // 팀플용 은규님 S3
            String s3Url2 = "https://outsourcing-profile.s3.ap-northeast-2.amazonaws.com/"+mediaText+mediaId; //테스트용 JunMo S3
            //log.error("src = {}",src);
            //log.error("s3Url2 = {}",s3Url2);
            //s3 url로 시작하는지 확인
//            log.info("\n src: {} \n s3Url: {}",src,s3Url);
            if(src.startsWith(s3Url) || src.startsWith(s3Url2)) {
                // s3 제거 대상 예외 추가 로직
//                log.info("s3 이미지 확인 + size : {}",mediaList.size());
                for (int i=0;i<mediaList.size();i++) {
                    Media media = mediaList.get(i);
//                    log.info("media.getS3Url : {}",media.getS3Url());
                    if(media.getS3Url().equals(src)) {
                        mediaExists.set(i,true); //media에 있던 파일이면 true
                    }
                }
            }
        }
        for(int i=0;i<mediaExists.size();i++) {
            //사라진 이미지면, content에 존재하지 않던 s3면 삭제
            if(!mediaExists.get(i)) {
                Media media = mediaList.get(i);
                log.info("S3 파일 제거 대상 : {}",media.getS3Url());
                mediaService.deleteMediaAndS3(media);
            } else {
                log.info("S3 파일 유지 대상 : {}",mediaList.get(i).getS3Url());
            }
        }
    }
}