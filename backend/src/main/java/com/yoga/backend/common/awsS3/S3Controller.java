package com.yoga.backend.common.awsS3;

import com.yoga.backend.common.awsS3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    // 로거 객체
    private static final Logger logger = LoggerFactory.getLogger(S3Controller.class);

    // S3 서비스 객체
    private final S3Service s3Service;

    // S3 서비스 객체 주입
    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    /**
     * S3 버킷에 파일을 업로드
     *
     * @param file  업로드할 파일
     * @return      파일 업로드 성공 메시지 또는 실패 메시지
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            s3Service.uploadFile(file.getOriginalFilename(), file.getInputStream());
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            logger.error("File upload failed", e);
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * S3 버킷에서 파일을 다운로드
     *
     * @param key   다운로드할 파일의 키(파일 이름)
     * @return      파일 데이터를 포함하는 응답 엔티티
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("key") String key) {
        try (InputStream s3Object = s3Service.getObject(key);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] tmp = new byte[4096];
            int bytesRead;
            while ((bytesRead = s3Object.read(tmp)) != -1) {
                buffer.write(tmp, 0, bytesRead);
            }
            return new ResponseEntity<>(buffer.toByteArray(), HttpStatus.OK);
        } catch (IOException e) {
            logger.error("File download failed", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}