package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.domain.entity.Product;
import com.boxfit.backend.domain.entity.ProductVariant;
import com.boxfit.backend.dto.request.productvariant.ProductVariantRequest;
import com.boxfit.backend.dto.request.productvariant.ProductVariantStockRequest;
import com.boxfit.backend.dto.response.productvariant.ProductVariantResponse;
import com.boxfit.backend.mapper.ProductVariantMapper;
import com.boxfit.backend.repository.ProductRepository;
import com.boxfit.backend.repository.ProductVariantRepository;
import com.boxfit.backend.service.ProductVariantService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper productVariantMapper;

    public ProductVariantServiceImpl(ProductVariantRepository productVariantRepository,
                                     ProductRepository productRepository,
                                     ProductVariantMapper productVariantMapper) {
        this.productVariantRepository = productVariantRepository;
        this.productRepository = productRepository;
        this.productVariantMapper = productVariantMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVariantResponse> getByProductId(UUID productId, Pageable pageable) {
        ensureProductExistsAndActive(productId);
        java.util.List<ProductVariantResponse> variants = productVariantRepository
            .findByProduct_IdAndIsActiveTrueOrderByCreatedAtAsc(productId)
            .stream()
            .map(productVariantMapper::toResponse)
            .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), variants.size());
        java.util.List<ProductVariantResponse> content = start >= variants.size()
            ? java.util.List.of()
            : variants.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(content, pageable, variants.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVariantResponse> getByProductSlug(String productSlug, Pageable pageable) {
        Product product = productRepository.findBySlugAndIsActiveTrue(productSlug)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found"));
        return getByProductId(product.getId(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVariantResponse> getAdminVariants(Pageable pageable) {
        return productVariantRepository.findAll(pageable).map(productVariantMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getAdminById(UUID id) {
        return productVariantMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    public ProductVariantResponse create(ProductVariantRequest request) {
        Product product = ensureProductExists(request.getProductId());
        validateSku(request.getSku(), null);

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        productVariantMapper.apply(request, variant);
        return productVariantMapper.toResponse(productVariantRepository.save(variant));
    }

    @Override
    public ProductVariantResponse update(UUID id, ProductVariantRequest request) {
        ProductVariant variant = findByIdOrThrow(id);
        ensureProductExists(request.getProductId());
        validateSku(request.getSku(), id);

        variant.setProduct(ensureProductExists(request.getProductId()));
        productVariantMapper.apply(request, variant);
        return productVariantMapper.toResponse(productVariantRepository.save(variant));
    }

    @Override
    public ProductVariantResponse disable(UUID id) {
        ProductVariant variant = findByIdOrThrow(id);
        variant.setActive(false);
        return productVariantMapper.toResponse(productVariantRepository.save(variant));
    }

    @Override
    public ProductVariantResponse enable(UUID id) {
        ProductVariant variant = findByIdOrThrow(id);
        variant.setActive(true);
        return productVariantMapper.toResponse(productVariantRepository.save(variant));
    }

    @Override
    public ProductVariantResponse updateStock(UUID id, ProductVariantStockRequest request) {
        ProductVariant variant = findByIdOrThrow(id);
        variant.setStockQuantity(request.getStockQuantity());
        return productVariantMapper.toResponse(productVariantRepository.save(variant));
    }

    private ProductVariant findByIdOrThrow(UUID id) {
        return productVariantRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_VARIANT_NOT_FOUND, "Product variant not found"));
    }

    private Product ensureProductExists(UUID productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found"));
    }

    private void ensureProductExistsAndActive(UUID productId) {
        productRepository.findById(productId)
            .filter(Product::isActive)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found"));
    }

    private void validateSku(String sku, UUID currentId) {
        boolean exists = currentId == null
            ? productVariantRepository.existsBySku(sku)
            : productVariantRepository.existsBySkuAndIdNot(sku, currentId);
        if (exists) {
            throw new AppException(ErrorCode.SKU_ALREADY_EXISTS, "SKU already exists");
        }
    }
}


