package com.boxfit.backend.mapper;

import com.boxfit.backend.domain.entity.ProductCollection;
import com.boxfit.backend.dto.request.collection.CollectionRequest;
import com.boxfit.backend.dto.response.collection.CollectionResponse;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    public CollectionResponse toResponse(ProductCollection collection) {
        if (collection == null) {
            return null;
        }

        CollectionResponse response = new CollectionResponse();
        response.setId(collection.getId());
        response.setSlug(collection.getSlug());
        response.setName(collection.getName());
        response.setDescription(collection.getDescription());
        response.setImageUrl(collection.getImageUrl());
        response.setHeroImageUrl(collection.getHeroImageUrl());
        response.setStatus(collection.getStatus());
        response.setProductCount(collection.getProducts() == null ? 0 : collection.getProducts().size());
        response.setCreatedAt(collection.getCreatedAt());
        response.setUpdatedAt(collection.getUpdatedAt());
        return response;
    }

    public void apply(CollectionRequest request, ProductCollection collection) {
        collection.setName(request.getName());
        collection.setDescription(request.getDescription());
        collection.setImageUrl(request.getImageUrl());
        collection.setHeroImageUrl(request.getHeroImageUrl());
        if (request.getStatus() != null) {
            collection.setStatus(request.getStatus());
        }
    }
}
