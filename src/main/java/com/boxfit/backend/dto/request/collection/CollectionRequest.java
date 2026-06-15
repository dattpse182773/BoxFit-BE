package com.boxfit.backend.dto.request.collection;

import com.boxfit.backend.domain.enums.CollectionStatus;
import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CollectionRequest {

    @NotBlank
    private String name;

    private String slug;

    private String description;

    private String imageUrl;

    private String heroImageUrl;

    private CollectionStatus status;

    private Set<UUID> productIds = new HashSet<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getHeroImageUrl() { return heroImageUrl; }
    public void setHeroImageUrl(String heroImageUrl) { this.heroImageUrl = heroImageUrl; }

    public CollectionStatus getStatus() { return status; }
    public void setStatus(CollectionStatus status) { this.status = status; }

    public Set<UUID> getProductIds() { return productIds; }
    public void setProductIds(Set<UUID> productIds) { this.productIds = productIds; }
}
