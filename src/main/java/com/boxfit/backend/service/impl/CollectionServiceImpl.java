package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.common.util.SlugUtils;
import com.boxfit.backend.domain.entity.Product;
import com.boxfit.backend.domain.entity.ProductCollection;
import com.boxfit.backend.domain.enums.CollectionStatus;
import com.boxfit.backend.dto.request.collection.CollectionRequest;
import com.boxfit.backend.dto.response.collection.CollectionResponse;
import com.boxfit.backend.dto.response.product.ProductResponse;
import com.boxfit.backend.mapper.CollectionMapper;
import com.boxfit.backend.mapper.ProductMapper;
import com.boxfit.backend.repository.ProductCollectionRepository;
import com.boxfit.backend.repository.ProductRepository;
import com.boxfit.backend.service.CollectionService;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

    private final ProductCollectionRepository collectionRepository;
    private final ProductRepository productRepository;
    private final CollectionMapper collectionMapper;
    private final ProductMapper productMapper;

    public CollectionServiceImpl(ProductCollectionRepository collectionRepository,
                                 ProductRepository productRepository,
                                 CollectionMapper collectionMapper,
                                 ProductMapper productMapper) {
        this.collectionRepository = collectionRepository;
        this.productRepository = productRepository;
        this.collectionMapper = collectionMapper;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CollectionResponse> getActiveCollections(Pageable pageable) {
        return collectionRepository.findByStatus(CollectionStatus.ACTIVE, pageable).map(collectionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse getActiveBySlug(String slug) {
        return collectionMapper.toResponse(collectionRepository.findBySlugAndStatus(slug, CollectionStatus.ACTIVE)
            .orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND, "Collection not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getActiveProductsBySlug(String slug, Pageable pageable) {
        collectionRepository.findBySlugAndStatus(slug, CollectionStatus.ACTIVE)
            .orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND, "Collection not found"));
        return productRepository.findActiveProductsByActiveCollectionSlug(slug, pageable).map(productMapper::toResponse);
    }

    @Override
    public CollectionResponse create(CollectionRequest request) {
        String slug = buildSlug(request);
        if (collectionRepository.existsBySlug(slug)) {
            throw new AppException(ErrorCode.SLUG_ALREADY_EXISTS, "Collection slug already exists");
        }

        ProductCollection collection = new ProductCollection();
        collection.setSlug(slug);
        collectionMapper.apply(request, collection);
        collection.setProducts(loadProducts(request.getProductIds()));
        return collectionMapper.toResponse(collectionRepository.save(collection));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CollectionResponse> getAdminCollections(Pageable pageable) {
        return collectionRepository.findAll(pageable).map(collectionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse getAdminById(UUID id) {
        return collectionMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    public CollectionResponse update(UUID id, CollectionRequest request) {
        ProductCollection collection = findByIdOrThrow(id);
        String slug = buildSlug(request);
        if (!slug.equals(collection.getSlug()) && collectionRepository.existsBySlugAndIdNot(slug, id)) {
            throw new AppException(ErrorCode.SLUG_ALREADY_EXISTS, "Collection slug already exists");
        }

        collection.setSlug(slug);
        collectionMapper.apply(request, collection);
        collection.setProducts(loadProducts(request.getProductIds()));
        return collectionMapper.toResponse(collectionRepository.save(collection));
    }

    @Override
    public CollectionResponse disable(UUID id) {
        ProductCollection collection = findByIdOrThrow(id);
        collection.setStatus(CollectionStatus.DISABLED);
        return collectionMapper.toResponse(collectionRepository.save(collection));
    }

    @Override
    public CollectionResponse enable(UUID id) {
        ProductCollection collection = findByIdOrThrow(id);
        collection.setStatus(CollectionStatus.ACTIVE);
        return collectionMapper.toResponse(collectionRepository.save(collection));
    }

    private ProductCollection findByIdOrThrow(UUID id) {
        return collectionRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.COLLECTION_NOT_FOUND, "Collection not found"));
    }

    private String buildSlug(CollectionRequest request) {
        String source = request.getSlug() == null || request.getSlug().isBlank() ? request.getName() : request.getSlug();
        String slug = SlugUtils.slugify(source);
        if (slug == null) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Invalid collection slug");
        }
        return slug;
    }

    private Set<Product> loadProducts(Set<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<Product> products = new HashSet<>(productRepository.findAllById(productIds));
        if (products.size() != productIds.size()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND, "One or more products were not found");
        }
        return products;
    }
}
