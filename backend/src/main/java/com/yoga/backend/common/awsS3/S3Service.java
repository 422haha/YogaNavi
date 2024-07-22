package com.yoga.backend.common.awsS3;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3Service {

    // S3 클라이언트
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    // S3 버킷 이름
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // S3 클라이언트 주입
    @Autowired
    public S3Service(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    /**
     * S3 버킷에 파일을 업로드
     *
     * @param key           업로드할 파일의 키(파일 이름)
     * @param inputStream   업로드할 파일의 입력 스트림
     * @throws IOException  파일 업로드 중 오류가 발생할 경우
     */
    public void uploadFile(String key, InputStream inputStream) throws IOException {
        s3Client.putObject(PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build(), RequestBody.fromInputStream(inputStream, inputStream.available()));
    }

    /**
     * S3 버킷에서 객체를 가져옴
     *
     * @param key   가져올 객체의 키(파일 이름)
     * @return      객체 데이터를 포함하는 입력 스트림
     */
    public InputStream getObject(String key) {
        return s3Client.getObject(GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build());
    }

    /**
     * 파일을 S3 버킷에 업로드하고 S3 URL을 반환
     *
     * @param file      업로드할 파일
     * @param directory 파일이 업로드될 S3 디렉토리 경로
     * @return 업로드된 파일의 S3 URL
     * @throws IOException 파일 업로드 중 오류가 발생할 경우
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        // 고유한 파일 이름을 생성
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        // S3 버킷에 저장될 키를 생성
        String key = directory + "/" + fileName;
        // 파일을 S3에 업로드
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucketName)  // 버킷 이름 설정
                .key(key)            // 키 설정
                .build(),
            RequestBody.fromInputStream(file.getInputStream(), file.getSize()));  // 파일의 InputStream과 크기 설정
        // 업로드된 파일의 S3 URL을 반환
        return generateS3Url(key);
    }

    /**
     * S3 버킷에서 파일을 삭제
     *
     * @param fileUrl 삭제할 파일의 S3 URL
     */
    public void deleteFile(String fileUrl) {
        // S3 URL에서 키를 추출
        String key = extractKeyFromUrl(fileUrl);
        // S3에서 파일을 삭제
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(bucketName)  // 버킷 이름 설정
            .key(key)            // 키 설정
            .build());
    }

    /**
     * 고유한 파일 이름을 생성
     *
     * @param originalFilename 원본 파일 이름
     * @return 고유한 파일 이름
     */
    private String generateUniqueFileName(String originalFilename) {
        // UUID와 원본 파일 이름을 결합하여 고유한 파일 이름 생성
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    /**
     * S3 URL을 생성
     *
     * @param key S3 버킷에 저장된 파일의 키
     * @return 파일의 S3 URL
     */
    private String generateS3Url(String key) {
        // 버킷 이름과 키를 사용하여 S3 URL 생성
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }

    /**
     * S3 URL에서 키를 추출
     *
     * @param fileUrl S3 URL
     * @return 추출된 키
     */
    private String extractKeyFromUrl(String fileUrl) {
        // S3 URL에서 ".com/" 이후의 부분을 키로 추출
        return fileUrl.substring(fileUrl.indexOf(".com/") + 5);
    }

    public String generatePresignedUrl(String key, long expirationInSeconds) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(expirationInSeconds))
            .getObjectRequest(getObjectRequest)
            .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }
}
