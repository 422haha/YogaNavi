package com.yoga.backend.common.awsS3;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

@Service
public class S3Service {

    // S3 클라이언트
    private final S3Client s3Client;

    // S3 버킷 이름
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // S3 클라이언트 주입
    @Autowired
    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
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
}
