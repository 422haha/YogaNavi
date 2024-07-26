package com.yoga.backend.common.awsS3;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Service
public class S3Service {

    private static final String BASE_URL = "https://yoga-navi.s3.ap-northeast-2.amazonaws.com/";

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
        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key != null && !key.isEmpty()) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
                log.info("Successfully deleted file with key: {}", key);
            } else {
                log.warn("Unable to delete file. Invalid URL: {}", fileUrl);
            }
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", fileUrl, e);
        }
    }

    /**
     * S3 URL에서 키를 추출
     *
     * @param fileUrl S3 URL
     * @return 추출된 키
     */
    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String path = url.getPath();
            // Remove leading '/' if present
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (MalformedURLException e) {
            log.error("Invalid S3 URL: {}", fileUrl, e);
            return null;
        }
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


    public String generatePresignedUrl(String key, long expirationInSeconds) {
        try {
            // BASE_URL로 시작하는 전체 URL이 넘어올 경우를 처리
            if (key.startsWith(BASE_URL)) {
                key = key.substring(BASE_URL.length());
            }
            // 버킷 이름으로 시작하는 경우 처리
            else if (key.startsWith(bucketName + "/")) {
                key = key.substring(bucketName.length() + 1);
            }

            log.debug("Generating presigned URL for key: {}", key);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expirationInSeconds))
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            String presignedUrl = presignedGetObjectRequest.url().toString();

            log.debug("Generated presigned URL: {}", presignedUrl);

            return normalizeUrl(presignedUrl);
        } catch (S3Exception e) {
            log.error("Error generating presigned URL for key: {}. Error: {}", key, e.getMessage());
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    private String normalizeUrl(String url) {
        String decodedUrl = url;
        // URL 디코딩을 여러 번 수행하여 중첩된 인코딩 해결
        while (decodedUrl.contains("%")) {
            try {
                String newDecodedUrl = URLDecoder.decode(decodedUrl, StandardCharsets.UTF_8);
                if (newDecodedUrl.equals(decodedUrl)) {
                    break;  // 더 이상 디코딩할 것이 없으면 중단
                }
                decodedUrl = newDecodedUrl;
            } catch (IllegalArgumentException e) {
                // 디코딩 중 오류 발생 시 현재 상태로 중단
                break;
            }
        }

        // 중복된 기본 URL 제거
        if (decodedUrl.contains(BASE_URL + BASE_URL)) {
            decodedUrl = decodedUrl.replace(BASE_URL + BASE_URL, BASE_URL);
        }

        try {
            URL parsedUrl = new URL(decodedUrl);
            String baseUrl = parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + parsedUrl.getPath();
            String query = parsedUrl.getQuery();

            if (query == null) {
                return baseUrl;
            }

            // 중복 쿼리 파라미터 제거
            Map<String, String> params = new LinkedHashMap<>();
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    params.putIfAbsent(keyValue[0], keyValue[1]);
                }
            }

            StringBuilder normalizedQuery = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (normalizedQuery.length() > 0) {
                    normalizedQuery.append('&');
                }
                normalizedQuery.append(entry.getKey()).append('=').append(entry.getValue());
            }

            return baseUrl + "?" + normalizedQuery;
        } catch (MalformedURLException e) {
            log.error("URL normalization failed", e);
            return url;
        }
    }

    public Map<String, String> generatePresignedUrls(Set<String> keys, long expirationInSeconds) {
        Map<String, String> presignedUrls = new HashMap<>();
        for (String key : keys) {
            if (key.startsWith(bucketName + "/")) {
                key = key.substring(bucketName.length() + 1);
            }

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(expirationInSeconds))
                .getObjectRequest(getObjectRequest)
                .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
            String presignedUrl = presignedGetObjectRequest.url().toString();
            presignedUrls.put(key, normalizeUrl(presignedUrl));  // normalizeUrl 적용
        }
        return presignedUrls;
    }
}
