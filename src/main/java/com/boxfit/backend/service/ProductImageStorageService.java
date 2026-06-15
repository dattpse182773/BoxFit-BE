package com.boxfit.backend.service;

import com.boxfit.backend.dto.response.product.ProductImageUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageStorageService {
    ProductImageUploadResponse uploadProductImage(MultipartFile file);
}
