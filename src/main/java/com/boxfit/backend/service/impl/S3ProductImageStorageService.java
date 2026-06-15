package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.dto.response.product.ProductImageUploadResponse;
import com.boxfit.backend.service.ProductImageStorageService;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3ProductImageStorageService implements ProductImageStorageService {

    private static final String PRODUCT_IMAGE_PREFIX = "products/images/";
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;
    private final long maxImageSizeBytes;

    public S3ProductImageStorageService(S3Client s3Client,
                                        @Value("${aws.s3.bucket-name}") String bucketName,
                                        @Value("${aws.s3.region}") String region,
                                        @Value("${app.upload.max-image-size-bytes:5242880}") long maxImageSizeBytes) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
        this.maxImageSizeBytes = maxImageSizeBytes;
    }

    @Override
    public ProductImageUploadResponse uploadProductImage(MultipartFile file) {
        validateFile(file);

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
        String contentType = file.getContentType();
        String key = PRODUCT_IMAGE_PREFIX + UUID.randomUUID() + resolveExtension(originalFileName, contentType);

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return new ProductImageUploadResponse(buildPublicUrl(key), key, originalFileName, contentType, file.getSize());
        } catch (IOException | SdkException ex) {
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to upload product image");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Image file is required");
        }
        if (!StringUtils.hasText(bucketName)) {
            throw new AppException(ErrorCode.INTERNAL_ERROR, "S3 bucket is not configured");
        }
        if (file.getSize() > maxImageSizeBytes) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Image file is too large");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Only JPEG, PNG, and WEBP images are allowed");
        }
    }

    private String resolveExtension(String fileName, String contentType) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (StringUtils.hasText(extension)) {
            return "." + extension.toLowerCase(Locale.ROOT);
        }
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }

    private String buildPublicUrl(String key) {
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20").replace("%2F", "/");
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + encodedKey;
    }
}
