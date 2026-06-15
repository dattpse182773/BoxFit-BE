package com.boxfit.backend.service.impl;

import com.boxfit.backend.common.exception.AppException;
import com.boxfit.backend.common.exception.ErrorCode;
import com.boxfit.backend.common.util.SlugUtils;
import com.boxfit.backend.domain.entity.Category;
import com.boxfit.backend.domain.entity.Product;
import com.boxfit.backend.domain.enums.ProductStatus;
import com.boxfit.backend.dto.request.product.ProductFilterRequest;
import com.boxfit.backend.dto.request.product.ProductRequest;
import com.boxfit.backend.dto.response.product.ProductResponse;
import com.boxfit.backend.mapper.ProductMapper;
import com.boxfit.backend.repository.CategoryRepository;
import com.boxfit.backend.repository.ProductRepository;
import com.boxfit.backend.repository.specification.ProductSpecifications;
import com.boxfit.backend.service.ProductService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                             CategoryRepository categoryRepository,
                             ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(ProductFilterRequest filter, Pageable pageable) {
        ProductFilterRequest safeFilter = filter == null ? new ProductFilterRequest() : filter;
        Specification<Product> specification = Specification.where(ProductSpecifications.isActive())
            .and(ProductSpecifications.categorySlugEquals(safeFilter.getCategorySlug()))
            .and(ProductSpecifications.hasVariantSize(safeFilter.getVariantSize()))
            .and(ProductSpecifications.hasVariantColor(safeFilter.getColor()))
            .and(ProductSpecifications.priceBetween(safeFilter.getMinPrice(), safeFilter.getMaxPrice()))
            .and(ProductSpecifications.genderEquals(safeFilter.getGender()))
            .and(ProductSpecifications.sportTypeEquals(safeFilter.getSportType()))
            .and(ProductSpecifications.statusEquals(safeFilter.getStatus()));

        validateCategorySlug(safeFilter.getCategorySlug());
        return productRepository.findAll(specification, pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getBySlug(String slug) {
        return productRepository.findBySlugAndIsActiveTrue(slug)
            .map(productMapper::toResponse)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAdminProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getAdminById(UUID id) {
        return productMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        String slug = buildSlug(request.getName());
        if (productRepository.existsBySlug(slug)) {
            throw new AppException(ErrorCode.SLUG_ALREADY_EXISTS, "Product slug already exists");
        }

        Category category = loadCategory(request.getCategoryId());

        Product product = new Product();
        product.setSlug(slug);
        productMapper.apply(request, product, category);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = findByIdOrThrow(id);
        String slug = buildSlug(request.getName());
        if (!slug.equals(product.getSlug()) && productRepository.existsBySlugAndIdNot(slug, id)) {
            throw new AppException(ErrorCode.SLUG_ALREADY_EXISTS, "Product slug already exists");
        }

        Category category = loadCategory(request.getCategoryId());

        product.setSlug(slug);
        productMapper.apply(request, product, category);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse disable(UUID id) {
        Product product = findByIdOrThrow(id);
        product.setActive(false);
        product.setStatus(ProductStatus.INACTIVE);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse enable(UUID id) {
        Product product = findByIdOrThrow(id);
        product.setActive(true);
        product.setStatus(ProductStatus.ACTIVE);
        return productMapper.toResponse(productRepository.save(product));
    }

    private Product findByIdOrThrow(UUID id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Product not found"));
    }

    private Category loadCategory(UUID categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found"));
    }

    private String buildSlug(String name) {
        String slug = SlugUtils.slugify(name);
        if (slug == null) {
            throw new AppException(ErrorCode.VALIDATION_ERROR, "Invalid product name");
        }
        return slug;
    }

    private void validateCategorySlug(String categorySlug) {
        if (categorySlug == null || categorySlug.isBlank()) {
            return;
        }
        categoryRepository.findBySlug(categorySlug)
            .filter(Category::isActive)
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, "Category not found"));
    }

}

