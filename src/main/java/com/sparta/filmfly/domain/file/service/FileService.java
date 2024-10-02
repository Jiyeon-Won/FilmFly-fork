package com.sparta.filmfly.domain.file.service;

import com.sparta.filmfly.domain.board.entity.Board;
import com.sparta.filmfly.domain.board.repository.BoardRepository;
import com.sparta.filmfly.domain.file.etc.MediaTypeEnum;
import com.sparta.filmfly.global.common.response.ResponseCodeEnum;
import com.sparta.filmfly.global.exception.custom.detail.UploadException;
import com.sparta.filmfly.global.infra.S3Uploader;
import com.sparta.filmfly.global.util.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Uploader s3Uploader;
    private final BoardRepository boardRepository;
    //img src 패턴 검사용
    private final String imgReg = "(<img[^>]*src\s*=\s*[\"']?([^>\"\']+)[\"']?[^>]*>)"; // <img src 패턴
    private final Pattern pattern = Pattern.compile(imgReg);

    public String saveFileToLocal(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String folderLocation = FileUtils.getAbsoluteUploadFolder(FileUtils.LOCAL_UPLOAD_PATH);
        String uuidFileName = FileUtils.createUuidFileName(originalFilename);
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
        Map<String, String> map = FileUtils.extractFileName(imageName);
        if (!map.get("url").contains(FileUtils.S3_URL)) {
            FileUtils.deleteFileToLocal(map.get("file"));
        }
    }

    /**
     * localhost temp image 글자 있으면 s3에 업로드 후 temp 이미지 삭제 local 주소를 s3 주소로 변경된 값을 반환
     */
    public String uploadLocalImageToS3(MediaTypeEnum mediaType, Long boardId, String content) {
        content = FileUtils.decodeUrlsInContent(content);   // src= 찾기
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String src = matcher.group(2).trim(); // http://localhost:8080/temp/9bee7b11-3고양이.jpg
            String srcFileName = FileUtils.extractFileName(src).get("file"); // 9bee7b11-3고양이.jpg
            String currentUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build()
                .toUriString().split("://")[1]; //http://localhost:8080

            //img의 src 값의 앞 부분이 임시 이미지면 http://localhost:8080/temp ?
            if (src.split("://")[1].startsWith(currentUrl + "/temp/")) {
                try {
                    String filePath = FileUtils.getAbsoluteUploadFolder(FileUtils.LOCAL_UPLOAD_PATH) + srcFileName;

                    File file = new File(filePath); //temp 폴더의 임시 이미지 지정
                    MultipartFile multipartFile = FileUtils.convertFileToMultipartFile(file);

                    //temp 폴더에 있는 경우
                    if (!multipartFile.isEmpty()) {
                        // s3에 저장 및 media 테이블에 저장
                        String uploadedFileUrl = s3Uploader.uploadFile(mediaType, boardId, multipartFile);
                        // local 임시 이미지 삭제
                        FileUtils.deleteFileToLocal(srcFileName);
                        content = content.replace(src, uploadedFileUrl); //local 이미지 -> s3 주소로 변경
                        matcher = pattern.matcher(content);
                    } else {
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
     * 변경된 이미지 파일 검사 사라진 s3 경로는 s3에서 삭제
     */
    public void checkModifiedImageFile(MediaTypeEnum mediaType, Long typeId, String content) {
        content = FileUtils.decodeUrlsInContent(content);

        Board board = null;
        if (MediaTypeEnum.BOARD == mediaType) {
            board = boardRepository.findByIdOrElseThrow(typeId);
        }

        List<String> changeImageUrls = extractedImageUrls(mediaType, typeId, content);
        List<String> savedImageUrls = extractedImageUrls(mediaType, typeId, board.getContent());
        for (String url : changeImageUrls) {
            try {
                if (!savedImageUrls.contains(url)) {
                    String fileName = FileUtils.extractFileName(url).get("file");
                    String filePath = FileUtils.getAbsoluteUploadFolder(FileUtils.LOCAL_UPLOAD_PATH) + fileName;
                    File file = new File(filePath); //temp 폴더의 임시 이미지 지정
                    MultipartFile multipartFile = FileUtils.convertFileToMultipartFile(file);
                    s3Uploader.uploadFile(mediaType, typeId, multipartFile);
                }
            } catch (IOException e) {
                throw new UploadException(ResponseCodeEnum.BOARD_FILE_PROCESSING_ERROR);
            }
        }
        for (String url : savedImageUrls) {
            if (!changeImageUrls.contains(url)) {
                s3Uploader.deleteFile(url);
            }
        }
    }

    public List<String> extractedImageUrls(MediaTypeEnum mediaType, Long typeId, String content) {
        Matcher matcher = pattern.matcher(content);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            String src = matcher.group(2).trim();
            String mediaText;
            if (mediaType == MediaTypeEnum.BOARD) {
                mediaText = "boards/";
            } else {
                mediaText = "etc/";
            }
            String s3Url = FileUtils.S3_URL + mediaText + typeId;
            if (src.startsWith(s3Url)) {
                result.add(src);
            }
        }
        return result;
    }


}