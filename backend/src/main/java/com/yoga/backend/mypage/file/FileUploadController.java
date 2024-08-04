package com.yoga.backend.mypage.file;

/*
import com.yoga.backend.common.awsS3.S3Service;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/mypage/files")
public class FileUploadController {

    @Autowired
    private S3Service s3Service;

    */
/**
     * 파일을 S3에 업로드하고 URL을 반환
     *
     * @param file 업로드할 파일
     * @param directory S3 버킷 내 저장될 디렉토리
     * @return 업로드된 파일의 URL
     *//*

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
        @RequestParam("directory") String directory) {
        try {
            String fileUrl = s3Service.uploadFile(file, directory);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("파일 업로드 실패: " + e.getMessage());
        }
    }
}
*/
